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
package com.raytheon.uf.edex.plugin.pointset.netcdf.description;

import java.text.ParseException;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import ucar.nc2.NetcdfFile;

import com.raytheon.uf.common.time.DataTime;

/**
 * 
 * Contains the information necessary to extract a {@link DataTime} from the
 * global attributes of a {@link NetcdfFile}.
 * 
 * <pre>
 * 
 * SOFTWARE HISTORY
 * 
 * Date          Ticket#  Engineer  Description
 * ------------- -------- --------- --------------------------
 * Aug 11, 2015  4709     bsteffen  Initial creation
 * 
 * </pre>
 * 
 * @author bsteffen
 */
@XmlAccessorType(XmlAccessType.NONE)
public class DataTimeDescription {

    @XmlElement
    private DateAttributeValue refTime;

    /*
     * Eventually this should include the information necessary to pull out
     * forecast information and/or time ranges.
     */

    public DateAttributeValue getRefTime() {
        return refTime;
    }

    public void setRefTime(DateAttributeValue refTime) {
        this.refTime = refTime;
    }

    public DataTime getDataTime(NetcdfFile file) throws ParseException {
        Date refTime = this.refTime.getDate(file);
        if (refTime == null) {
            return null;
        } else {
            return new DataTime(refTime);
        }
    }

}
