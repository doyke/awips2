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
package com.raytheon.uf.common.dataplugin.scan;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.geotools.coverage.grid.GridGeometry2D;

import com.raytheon.uf.common.dataplugin.IDecoderGettable;
import com.raytheon.uf.common.dataplugin.annotations.DataURI;
import com.raytheon.uf.common.dataplugin.persist.ServerSpecificPersistablePluginDataObject;
import com.raytheon.uf.common.dataplugin.scan.data.ModelData;
import com.raytheon.uf.common.dataplugin.scan.data.ScanTableData;
import com.raytheon.uf.common.dataplugin.scan.data.SoundingData;
import com.raytheon.uf.common.datastorage.IDataStore;
import com.raytheon.uf.common.datastorage.Request;
import com.raytheon.uf.common.datastorage.records.ByteDataRecord;
import com.raytheon.uf.common.monitor.scan.config.SCANConfigEnums.ScanTables;
import com.raytheon.uf.common.serialization.DynamicSerializationManager;
import com.raytheon.uf.common.serialization.DynamicSerializationManager.SerializationType;
import com.raytheon.uf.common.serialization.annotations.DynamicSerialize;
import com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement;

/**
 * Rehash of SCAN
 * 
 * <pre>
 * 
 * SOFTWARE HISTORY
 * 
 * Date         Ticket#     Engineer    Description
 * ------------ ----------  ----------- --------------------------
 * 03/17/10     2521     D. Hladky   Initial release
 * 
 * </pre>
 * 
 * @author dhladky
 * @version 1
 */

@Entity
@Table(name = "scan", uniqueConstraints = { @UniqueConstraint(columnNames = { "dataURI" }) })
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@DynamicSerialize
public class ScanRecord extends ServerSpecificPersistablePluginDataObject {

    /**
     * 
     */
    private static final long serialVersionUID = 5983810116816447875L;

    @Column(length = 7)
    @DataURI(position = 1)
    @DynamicSerializeElement
    @XmlElement(nillable = false)
    private String icao;

    @Column(length = 7)
    @DataURI(position = 2)
    @DynamicSerializeElement
    @XmlElement(nillable = false)
    private String type;

    @Column(length = 7)
    @DataURI(position = 3)
    @DynamicSerializeElement
    @XmlElement(nillable = false)
    private double tilt;

    @Transient
    public GridGeometry2D stationGeometry = null;

    @Column
    @DynamicSerializeElement
    @XmlElement(nillable = false)
    public Date volScanTime = null;

    @Column
    @DynamicSerializeElement
    @XmlElement(nillable = false)
    private boolean lastElevationAngle;

    /** table data **/
    @Transient
    public ScanTableData<?> tableData = null;

    @Transient
    /* cell data only */
    public SoundingData sd = null;

    @Transient
    /* cell data only */
    public ModelData md = null;

    @Override
    public IDecoderGettable getDecoderGettable() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Gets the type of Table
     * 
     * @return
     */
    public String getType() {
        return type;
    }

    /** Set the table type saved */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the radar station ICAO
     * 
     * @return
     */
    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    /**
     * Sends a String from the TABLE enum for which table data to grab
     */
    public ScanTableData<?> getTableData() {
        return tableData;
    }

    public void setTableData(ScanTableData<?> table) {
        this.tableData = table;
    }

    /**
     * Get the Keys for my table
     * 
     * @param type
     * @param time
     * @return
     */
    public Set<String> getTableKeys(ScanTables type) {
        Set<String> keySet = null;
        try {
            ScanTableData<?> table = getTableData();
            keySet = table.getTableData().keySet();
        } catch (NullPointerException npe) {
            return null;
        }
        return keySet;
    }

    /**
     * Set the Sounding Data
     * 
     * @param ed
     */
    public void setSoundingData(SoundingData sd) {
        this.sd = sd;
    }

    /**
     * Get the SoundingData
     * 
     * @return
     */
    public SoundingData getSoundingData() {
        return sd;
    }

    /**
     * Set the Model Data
     * 
     * @param md
     */
    public void setModelData(ModelData md) {
        this.md = md;
    }

    /**
     * Get the ModelData
     * 
     * @return
     */
    public ModelData getModelData() {
        return md;
    }

    /**
     * Set the Model Data
     * 
     * @param md
     */
    public void setTilt(double tilt) {
        this.tilt = tilt;
    }

    /**
     * Get the ModelData
     * 
     * @return
     */
    public double getTilt() {
        return tilt;
    }

    /**
     * Gets the station Geometry for the WFO
     * 
     * @return
     */
    public GridGeometry2D getStationGeometry() {
        return stationGeometry;
    }

    public void setStationGeometry(GridGeometry2D stationGeometry) {
        this.stationGeometry = stationGeometry;
    }

    /**
     * Gets the Hash out of the datastore by HUC
     * 
     * @param dataStore
     * @param huc
     */
    public void retrieveMapFromDataStore(IDataStore dataStore) {
        try {
            ByteDataRecord byteData = (ByteDataRecord) dataStore.retrieve(
                    getDataURI(), getType(), Request.ALL);
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    byteData.getByteData());
            Object o = DynamicSerializationManager.getManager(
                    SerializationType.Thrift).deserialize(bais);
            setTableData((ScanTableData<?>) o);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the Sounding Data available in this record
     * 
     * @param dataStore
     */
    public void retrieveSoundingDataFromDataStore(IDataStore dataStore) {
        try {
            ByteDataRecord byteData = (ByteDataRecord) dataStore.retrieve(
                    getDataURI(), getType() + "/sounding", Request.ALL);
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    byteData.getByteData());
            Object o = DynamicSerializationManager.getManager(
                    SerializationType.Thrift).deserialize(bais);
            setSoundingData((SoundingData) o);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the Sounding Data available in this record
     * 
     * @param dataStore
     */
    public void retrieveModelDataFromDataStore(IDataStore dataStore) {
        try {
            ByteDataRecord byteData = (ByteDataRecord) dataStore.retrieve(
                    getDataURI(), getType() + "/model", Request.ALL);
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    byteData.getByteData());
            Object o = DynamicSerializationManager.getManager(
                    SerializationType.Thrift).deserialize(bais);
            setModelData((ModelData) o);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the volume scan time
     * 
     * @return
     */
    public Date getVolScanTime() {
        return volScanTime;
    }

    /**
     * set the volume scan time
     * 
     * @param volScanTime
     */
    public void setVolScanTime(Date volScanTime) {
        this.volScanTime = volScanTime;
    }

    public boolean isLastElevationAngle() {
        return lastElevationAngle;
    }

    public void setLastElevationAngle(boolean lastElevationAngle) {
        this.lastElevationAngle = lastElevationAngle;
    }

    /**
     * Used for debugging.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n dataURI: " + getDataURI() + "\n");
        if (tableData != null) {
            for (Object key : tableData.getTableData().keySet()) {
                sb.append(key + " : "
                        + tableData.getTableData().get(key).toString() + "\n");
            }
        }

        return sb.toString();
    }

}