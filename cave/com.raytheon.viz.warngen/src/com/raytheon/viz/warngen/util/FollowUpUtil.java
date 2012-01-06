/**
 * This software was developed and / or modified by Raytheon Company,
 * pursuant to Contract DG133W-05-CQ-1067 with the US Government.
 * 
 * U.S. EXPORT CONTROLLED TECHNICAL DATA
 * This software product contains export-restricted data whose
 * export/transfer/disclosure is restricted by U.S. law. Dissemination
 * to non-U.S. persons whether in the United States or abroad requires
 * an export license or other authorization.
 * 
 * Contractor Name:        Raytheon Company
 * Contractor Address:     6825 Pine Street, Suite 340
 *                         Mail Stop B8
 *                         Omaha, NE 68106
 *                         402.291.0100
 * 
 * See the AWIPS II Master Rights File ("Master Rights File.pdf") for
 * further licensing information.
 **/
package com.raytheon.viz.warngen.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.raytheon.uf.common.dataplugin.warning.AbstractWarningRecord;
import com.raytheon.uf.common.dataplugin.warning.WarningRecord.WarningAction;
import com.raytheon.uf.common.dataplugin.warning.config.WarngenConfiguration;
import com.raytheon.uf.common.time.SimulatedTime;
import com.raytheon.uf.common.time.TimeRange;

/**
 * This utility will provide methods for determining what followup products are
 * applicable at a given time. Additionally, they will provide extra methods
 * which are deemed useful in followup product generation.
 * 
 * <pre>
 * SOFTWARE HISTORY
 * Date			Ticket#		Engineer	Description
 * ------------	----------	-----------	--------------------------
 * Jul 22, 2008	#1284			bwoodle	Initial creation
 * 
 * </pre>
 * 
 * @author bwoodle
 * @version 1.0
 */

public class FollowUpUtil {

    private static final Pattern pilPtrn = Pattern
            .compile("(FFW|TOR|SVR|EWW|SMW|FLW|FLS|FFS|MWS|FFA|MWW|SVS|WRK)[A-Z]{3}");

    public static final Pattern vtecPtrn = Pattern
            .compile("/[OTEX]\\.([A-Z]{3})\\.[A-Za-z0-9]{4}\\.[A-Z]{2}\\.[WAYSFON]\\.\\d{4}\\.\\d{6}T\\d{4}Z-\\d{6}T\\d{4}Z/");

    /**
     * This method checks whether a particular followup should be available
     * given a Warning Record, a vtec Action, and a template configuration
     */
    public static boolean checkApplicable(WarngenConfiguration config,
            AbstractWarningRecord record, WarningAction action) {

        // Current Time
        Calendar cal = Calendar.getInstance();
        cal.setTime(SimulatedTime.getSystemTime().getTime());

        boolean rval = false;
        if (record == null) {
            return rval;
        }

        boolean valueCheck = false;
        for (String phensig : config.getPhensigs()) {
            if (phensig.equalsIgnoreCase(record.getPhensig())) {
                valueCheck = true;
            }
        }
        if (valueCheck) {
            boolean isNewProduct = false;
            for (String s : config.getFollowUps()) {
                WarningAction act = WarningAction.valueOf(s);
                if (act == action
                        && getTimeRange(act, record).contains(cal.getTime())
                        && act != WarningAction.COR) {
                    rval = true;
                }
                if (act == WarningAction.NEW) {
                    isNewProduct = true;
                }
            }
            if (action == WarningAction.COR) {
                WarningAction recordAct = WarningAction
                        .valueOf(record.getAct());
                if (isNewProduct
                        && getTimeRange(action, record).contains(cal.getTime())) {
                    rval = true;
                } else if (!isNewProduct && (recordAct != WarningAction.NEW)
                        && (recordAct != WarningAction.COR)
                        && (recordAct != WarningAction.EXT)) {
                    rval = true;
                }
            }
        }
        return rval;
    }

    /**
     * Check whether or not a given Warning Record satisfies the requirements to
     * be correctible given the selected template
     */
    public static ArrayList<AbstractWarningRecord> checkCorApplicable(
            WarngenConfiguration config,
            List<AbstractWarningRecord> correctableWarnings,
            List<AbstractWarningRecord> currentWarnings) {

        ArrayList<AbstractWarningRecord> list = new ArrayList<AbstractWarningRecord>();

        boolean allowsCONProduct = false;
        boolean allowsCORProduct = false;
        for (String s : config.getFollowUps()) {
            WarningAction act = WarningAction.valueOf(s);
            if (act == WarningAction.CON) {
                allowsCONProduct = true;
            } else if (act == WarningAction.COR) {
                allowsCORProduct = true;
            }
        }

        if (allowsCORProduct == false) {
            return list;
        }

        // Adding a COR option for continuation follow ups
        if (allowsCONProduct) {
            for (AbstractWarningRecord warnRec : correctableWarnings) {
                if (WarningAction.valueOf(warnRec.getAct()) == WarningAction.CON) {
                    list.add(warnRec);
                }
            }
        } else {
            for (AbstractWarningRecord warnRec : correctableWarnings) {
                boolean valid = false;
                if (WarningAction.valueOf(warnRec.getAct()) == WarningAction.NEW
                        || WarningAction.valueOf(warnRec.getAct()) == WarningAction.EXT) {
                    valid = true;
                    for (AbstractWarningRecord w : currentWarnings) {
                        WarningAction act = WarningAction.valueOf(w.getAct());
                        if (warnRec.getEtn().equals(w.getEtn())
                                && warnRec.getPhensig().equals(w.getPhensig())
                                && (act == WarningAction.CAN
                                        || act == WarningAction.CON || act == WarningAction.EXP)) {
                            valid = false;
                        }
                    }
                }

                if (valid) {
                    list.add(warnRec);
                }
            }
        }

        return list;
    }

    public static String getSpecialCorText(AbstractWarningRecord record) {

        String originalMessage = record.getRawmessage();

        // Removes the wmoHeader because the it will be inserted in the template
        int wmoIdx = originalMessage.indexOf(record.getWmoid());
        if (wmoIdx != -1) {
            int endIdx = originalMessage.indexOf("\n", wmoIdx);
            originalMessage = originalMessage.replace(
                    originalMessage.substring(wmoIdx, endIdx), "");
        }
        originalMessage = originalMessage.replaceFirst(record.getWmoid(), "");

        // Removes the PIL because the it will be inserted in the template
        Matcher m = pilPtrn.matcher(originalMessage);
        if (m.find()) {
            originalMessage = originalMessage.replaceFirst(m.group(0), "")
                    .trim();
        }

        m = vtecPtrn.matcher(originalMessage);
        while (m.find()) {
            String oldVtec = m.group(0);
            String newVtec = oldVtec.replace(m.group(1),
                    WarningAction.COR.toString());
            originalMessage = originalMessage.replaceFirst(m.group(0), newVtec);
        }

        return originalMessage;
    }

    /**
     * This method determines a time range for which a particular action can be
     * performed on a particular warngen product. For instance, a Reissue (NEW)
     * can only be produced from 20 minutes before expiration until 30 minutes
     * after expiration.
     * 
     * @param action
     * @param record
     * @return
     */
    public static TimeRange getTimeRange(WarningAction action,
            AbstractWarningRecord record) {
        /* Calendars for time calculations */

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(SimulatedTime.getSystemTime().getTime());
        end.setTime(SimulatedTime.getSystemTime().getTime());

        TimeRange rval = null;

        if (action == WarningAction.NEW) {
            /* Calculate NEW Time Range */
            start.setTime(record.getEndTime().getTime());
            start.add(Calendar.MINUTE, -20);
            end.setTime(record.getEndTime().getTime());
            end.add(Calendar.MINUTE, 30);
            rval = new TimeRange(start, end);
        } else if (action == WarningAction.COR) {
            /* Calculate COR Time Range */
            end.setTime(record.getIssueTime().getTime());
            end.add(Calendar.MINUTE, 10);
            rval = new TimeRange(record.getStartTime(), end);
        } else if (action == WarningAction.CAN) {
            /* Calculate CAN Time Range */
            end.setTime(record.getEndTime().getTime());
            end.add(Calendar.MINUTE, -10);
            rval = new TimeRange(record.getStartTime(), end);
        } else if (action == WarningAction.CON) {
            /* Calculate CON Time Range */
            end.setTime(record.getEndTime().getTime());
            end.add(Calendar.MINUTE, -5);
            rval = new TimeRange(record.getStartTime(), end);
        } else if (action == WarningAction.EXP) {
            /* Calculate EXP Time Range */
            start.setTime(record.getEndTime().getTime());
            start.add(Calendar.MINUTE, -10);
            end.setTime(record.getEndTime().getTime());
            end.add(Calendar.MINUTE, 10);
            rval = new TimeRange(start, end);
        } else if (action == WarningAction.EXT) {
            /* Calculate EXT Time Range */
            start.setTime(record.getStartTime().getTime());
            end.setTime(record.getEndTime().getTime());
            end.add(Calendar.MINUTE, -5);
            rval = new TimeRange(start, end);
        }

        return rval;
    }
}
