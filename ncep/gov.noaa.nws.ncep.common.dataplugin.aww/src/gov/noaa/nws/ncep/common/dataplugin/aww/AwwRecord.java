/**
 * AwwRecord
 * 
 * This java class performs the mapping to the database tables for AWW.
 * 
 * HISTORY
 *
 * Date         Ticket#         Engineer    Description
 * ------------ ----------      ----------- --------------------------
 * 12/2008      38				L. Lin     	Initial coding
 * 04/2009      38				L. Lin      Convert to TO10.
 * 07/2009		38			    L. Lin		Migration to TO11
 * 05/2010      38              L. Lin      Migration to TO11DR11
 * 01/11/2011   N/A             M. Gao      Add mndTime as the 5th element to construct 
 *                                          dataUri value that is used as a unique constraint 
 *                                          when the aww record is inserted into relational DB
 *                                          The reason mndTime is used is because the combination 
 *                                          of original 4 elements is not unique in some scenarios.                                       
 * 01/26/2011   N/A             M. Gao      Add designatorBBB as the 6th (No.4) element to construct 
 *                                          dataUri value that is used as a unique constraint 
 *                                          when the aww record is inserted into relational DB
 *                                          The reason mndTime is used is because the combination 
 *                                          of original 5 elements is not unique in some scenarios.                                       
 * 09/2011      				Chin Chen   changed to improve purge performance and
 * 											removed xml serialization as well
 * Apr 4, 2013        1846 bkowal      Added an index on refTime and forecastTime
 * </pre>
 * 
 * This code has been developed by the SIB for use in the AWIPS2 system.
 */

package gov.noaa.nws.ncep.common.dataplugin.aww;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import gov.noaa.nws.ncep.common.dataplugin.aww.AwwUgc;
import com.raytheon.uf.common.dataplugin.PluginDataObject;
import com.raytheon.uf.common.dataplugin.IDecoderGettable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.raytheon.uf.common.dataplugin.annotations.DataURI;
import com.raytheon.uf.common.serialization.annotations.DynamicSerialize;
import com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement;

@Entity
@Table(name = "aww", uniqueConstraints = { @UniqueConstraint(columnNames = { "dataURI" }) })
/*
 * Both refTime and forecastTime are included in the refTimeIndex since
 * forecastTime is unlikely to be used.
 */
@org.hibernate.annotations.Table(
		appliesTo = "aww",
		indexes = {
				@Index(name = "aww_refTimeIndex", columnNames = { "refTime", "forecastTime" } )
		}
)
@DynamicSerialize

public class AwwRecord extends PluginDataObject{
	
	private static final long serialVersionUID = 1L;
	
	/*
	 * There are many report types as follows:
	 * 1. SEVERE THUNDERSTORM WARNING
	 * 2. SEVERE THUNDERSTORM WATCH
	 * 3. TORNADO WARNING
	 * 4. TORNADO WATCH
	 * 5. SEVERE THUNDERSTORM OUTLINE UPDATE
	 * 6. TORNADO WATCH OUTLINE UPDATE
	 * 7. FLASH FLOOD WARNING
	 * 8. FLASH FLOOD WATCH
	 * 9. FLOOD WARNING
	 * 10. FLOOD WATCH
	 * 11. FLOOD STATEMENT
	 * 12. WINTER STORM WARNING
	 * 13. WINTER STORM WATCH
	 * 14. WATCH COUNTY NOTIFICATION 
	 * 15. SEVERE WEATHER STATEMENT
	 * 16. WIND ADVISORY 
	 * 17. FOG ADVISORY
	 * 18. HEAT ADVISORY
	 * 19. FROST ADVISORY
	 * 20. SMOKE ADVISORY
	 * 21. WEATHER ADVISORY
	 * 22. WINTER WEATHER ADVISORY
	 * 23. SIGNIGICANT WEATHER ADVISORY
	 * 24. SPECIAL WEATHER STATEMENT
	 * 25. RED FLAG WARNING
	 * 26. TORNADO REPORT
	 * 27. HIGH WIND WARNING
	 * 28. FREEZE WARNING
	 * 29. ADVERTENCIA DE INUNDACIONES
	 * 30. HYDROLOGIC STATEMENT
	 * 31. URGENT WEATHER MESSAGE
	 */
	@Column(length=40)
	@DataURI(position=1)
	@DynamicSerializeElement
	private String reportType;
	
	// The issue office where the report from
	@Column(length=32)
	@DataURI(position=2)
	@DynamicSerializeElement
	private String issueOffice;
	
	// The collection of watch numbers in the report
	@Column(length=160)
	@DataURI(position=5)
	@DynamicSerializeElement
	private String watchNumber;
	
	// WMO header
	@Column(length=32)
	@DynamicSerializeElement
	private String wmoHeader;
	
	// Issue time of the report
	@Column
	@DataURI(position=3)
	@DynamicSerializeElement
	private Calendar issueTime;
	
	// The designator
	@Column(length=8)
	@DataURI(position=4)
	@DynamicSerializeElement
	private String designatorBBB;
	
	// The designator
	@Column(length=72)
	@DataURI(position=6)
	@DynamicSerializeElement
	private String mndTime;
	
	// Attention WFO
	@Column(length=72)
	@DynamicSerializeElement
	private String attentionWFO;
	
	// The entire report
	@Column(length=40000)
	@DynamicSerializeElement
	private String bullMessage;
	

	// AWW UGC Table
	@DynamicSerializeElement
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "parentID", nullable = false)
    @Index(name = "awwUGC_parentid_idex")
	private Set<AwwUgc> awwUGC = new HashSet<AwwUgc>();

	
	/**
     * Default Convstructor
     */
    public AwwRecord() {
    	this.issueOffice=null;
    	this.watchNumber="0000";
    	this.issueTime=null;
    	this.attentionWFO=null;
    	this.wmoHeader=null;
    	this.designatorBBB=null;
    	this.bullMessage=null;
    	this.mndTime=null;
    }

    /**
     * Convstructs a consigmet record from a dataURI
     * 
     * @param uri The dataURI
     */
    public AwwRecord(String uri) {
        super(uri);
    }
    
    
    @Override
    public IDecoderGettable getDecoderGettable() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
	 * @return the reportType
	 */
    public String getReportType() {
		return reportType;
	}

    /**
	 * @return the reportType
	 */
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	/**
	 * @return the wmoHeader
	 */
	public String getWmoHeader(){
		return wmoHeader;
	}

	/**
	 * @param wnoHeader to set
	 */
	public void setWmoHeader(String wmoHeader){
		this.wmoHeader=wmoHeader;
	}
	
	/**
	 * @return the issueTime
	 */
	public Calendar getIssueTime(){
		return issueTime;
	}

	/**
	 * @param issueTime to set
	 */
	public void setIssueTime(Calendar issueTime){
		this.issueTime=issueTime;
	}

	/**
	 * @return the set of UGC
	 */
	   public Set<AwwUgc> getAwwUGC() {
	           return awwUGC;
	   }

	   /**
	    * @param awwUgc the ugc to set
	    */
	   public void setAwwUGC(Set<AwwUgc> awwUgc) {
	           this.awwUGC = awwUgc;
	   }

	   /**
	    * @param add AWW UGC to set
	    */
	   public void addAwwUGC(AwwUgc pugc){
	           awwUGC.add(pugc);
	           //pugc.setParentID(this);
	   }

	   /**
	    * Override existing set method to modify any
	    * classes that use the dataURI as a foreign key
	    */
	   public void setIdentifier(Object dataURI)
	   {

		   this.identifier = dataURI;
	      
	      
	      
	   }

	   /**
		 * @return the designator
		 */   
	public String getDesignatorBBB() {
		return designatorBBB;
	}

	/**
	 * @param designatorBBB to set
	 */
	public void setDesignatorBBB(String designatorBBB) {
		this.designatorBBB = designatorBBB;
	}

	/**
	 * @return the attentionWFO
	 */ 
	public String getAttentionWFO() {
		return attentionWFO;
	}

	/**
	 * @param attentionWFO to set
	 */
	public void setAttentionWFO(String attentionWFO) {
		this.attentionWFO = attentionWFO;
	}

	/**
	 * @return the watchNumber
	 */ 
	public String getWatchNumber() {
		return watchNumber;
	}

	/**
	 * @param watchNumber to set
	 */
	public void setWatchNumber(String watchNumber) {
		this.watchNumber = watchNumber;
	}

	/**
	 * @return the bullMessage
	 */ 
	public String getBullMessage() {
		return bullMessage;
	}

	/**
	 * @param bullMessage to set
	 */
	public void setBullMessage(String bullMessage) {
		this.bullMessage = bullMessage;
	}

	/**
	 * @return the issueOffice
	 */ 
	public String getIssueOffice() {
		return issueOffice;
	}

	/**
	 * @param issueOffice to set
	 */
	public void setIssueOffice(String issueOffice) {
		this.issueOffice = issueOffice;
	}

	/**
	 * @return the mndTime
	 */
	public String getMndTime() {
		return mndTime;
	}

	/**
	 * @param mndTime to set
	 */
	public void setMndTime(String mndTime) {
		this.mndTime = mndTime;
	}

}
