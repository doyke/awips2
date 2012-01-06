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
package com.raytheon.uf.viz.alertviz.internal;

import java.io.File;
import java.sql.Timestamp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

import com.raytheon.uf.viz.alertviz.Activator;
import com.raytheon.uf.viz.alertviz.AlertvizException;
import com.raytheon.uf.viz.alertviz.Constants;
import com.raytheon.uf.viz.alertviz.LogUtil;
import com.raytheon.uf.viz.alertviz.LogUtil.Order;
import com.raytheon.uf.viz.core.VizApp;
import com.raytheon.uf.viz.core.localization.LocalizationManager;

/**
 * Purges the database, and archives the old log entries to a text file
 * 
 * <pre>
 * SOFTWARE HISTORY
 * Date         Ticket#    Engineer    Description
 * ------------ ---------- ----------- --------------------------
 * Sep 22, 2008 1433       chammack    Initial creation
 * </pre>
 * 
 * @author chammack
 * @version 1.0
 */
public class ArchiveLogJob extends Job {

    private int numberOfLogsToKeep;

    private int ageOfLogInHours;

    private static final int DEFAULT_NUMBER_OF_LOGS = 10;

    private static final int DEFAULT_AGE_OF_LOG_IN_HRS = 12;

    private static final int MILLISECONDS_IN_HOUR = 60 * 60 * 1000;

    public ArchiveLogJob() {
        super("Archive Log");
        this.setSystem(true);
        numberOfLogsToKeep = Activator.getDefault().getPreferenceStore()
                .getInt(Constants.P_NUMBER_OF_LOGS);
        if (numberOfLogsToKeep == 0) {
            this.numberOfLogsToKeep = DEFAULT_NUMBER_OF_LOGS;
        }

        ageOfLogInHours = Activator.getDefault().getPreferenceStore()
                .getInt(Constants.P_MAX_AGE_OF_LOGS);
        if (ageOfLogInHours == 0) {
            this.ageOfLogInHours = DEFAULT_AGE_OF_LOG_IN_HRS;
        }

        this.schedule();

    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {

        try {
            Timestamp date = LogMessageDAO.getInstance().getLastPurgeTime();
            long lastPurgeInMs = date.getTime();
            long now = System.currentTimeMillis();

            if ((now - lastPurgeInMs) > (ageOfLogInHours * MILLISECONDS_IN_HOUR)) {
                String s = LocalizationManager.getUserDir() + "textLogs"
                        + File.separator + VizApp.getHostName()
                        + File.separator + "log";
                LogUtil.rollLogs(s, numberOfLogsToKeep);

                Timestamp ts = new Timestamp(now
                        - (ageOfLogInHours * MILLISECONDS_IN_HOUR));
                LogUtil.saveLogToFile(new File(s + ".0"), ts, Order.BEFORE);
                LogMessageDAO.getInstance().purge(ts);
            }
        } catch (AlertvizException e) {
            final Status s = new Status(Status.ERROR, Activator.PLUGIN_ID,
                    "Error occurred during purge and rotate", e);
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    ErrorDialog
                            .openError(
                                    Display.getDefault().getActiveShell(),
                                    "Error",
                                    "Error rotating saving and rotating logs.  Archived logs may not be stored",
                                    s);
                }

            });

        }

        this.schedule(ageOfLogInHours * MILLISECONDS_IN_HOUR);

        return Status.OK_STATUS;
    }
}
