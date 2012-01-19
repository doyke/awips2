/**
 * ConvsigmetLocation
 * 
 * This java class defines the getters and setters for the 
 *      convective sigmet location table.
 * 
 * HISTORY
 *
 * Date         Ticket#         Engineer    Description
 * ------------ ----------      ----------- --------------------------
 * 03/2009      87/114			L. Lin     	Initial coding
 * 06/2009		87/114			L. Lin      Enlarge size of locationLine and location.
 * 07/2009		87/114		    L. Lin		Migration to TO11
 * 09/2009		87/114		    L. Lin		Add latitude/longitude to location table
 * </pre>
 *
 * This code has been developed by the SIB for use in the AWIPS2 system.
 */
package gov.noaa.nws.ncep.common.dataplugin.convsigmet;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import com.raytheon.uf.common.serialization.ISerializableObject;
import com.raytheon.uf.common.serialization.annotations.DynamicSerialize;
import com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement;
import gov.noaa.nws.ncep.common.tools.IDecoderConstantsN;

@Entity
@Table(name="convsigmet_location")
@XmlAccessorType(XmlAccessType.NONE)
@DynamicSerialize
public class ConvSigmetLocation implements Serializable, ISerializableObject {

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue
    private Integer recordId = null;
	
	// The consigmet record this object belongs to 
	@ManyToOne
    @JoinColumn(name="parentID", nullable=false)
	private ConvSigmetSection parentID;
	
	// Collection of locations
    @Column(length=480)
    @XmlElement
    @DynamicSerializeElement
	private String locationLine;
	
	// Each location of a convective sigmet forecast area
    @Column(length=48)
    @XmlElement
    @DynamicSerializeElement
	private String location;
    
    // Each latitude of a convective sigmet forecast area
    @Column
    @XmlElement
    @DynamicSerializeElement
    private double latitude;

	// Each longitude of a convective sigmet forecast area
    @Column
    @XmlElement
    @DynamicSerializeElement
	private double longitude;   
    
	// Index for the order of a complete location set
    @Column
    @XmlElement
    @DynamicSerializeElement
	private Integer index;
			
        /**
         * No-Arg Convstructor.
         */
	public ConvSigmetLocation() {
		this.locationLine="";
		this.location="";
		this.index=IDecoderConstantsN.INTEGER_MISSING;
    }
	

	/**
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * @return the parentID
	 */
	public ConvSigmetSection getParentID() {
		return parentID;
	}

	/**
	 * @param parentID to set
	 */
	public void setParentID(ConvSigmetSection parentID) {
		this.parentID = parentID;
	}

	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * @param index to set
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}

	/**
	 * @return the locationLine
	 */
	public String getLocationLine() {
		return locationLine;
	}

	/**
	 * @param locationLine to set
	 */
	public void setLocationLine(String locationLine) {
		this.locationLine = locationLine;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	/**
     * Get the record id.
     *
     * @return The recordId. If not set returns null.
     */
	 public Integer getRecordId() {
		return recordId;
	}

	 /**
	  * Set the record id.
	  * @param record
	  */
	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}
	
}
