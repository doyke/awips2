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
package com.raytheon.uf.common.dataplugin.shef.tables;
// default package
// Generated Oct 17, 2008 2:22:17 PM by Hibernate Tools 3.2.2.GA

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Locimage generated by hbm2java
 * 
 * 
 * <pre>
 * 
 * SOFTWARE HISTORY
 * Date         Ticket#    Engineer    Description
 * ------------ ---------- ----------- --------------------------
 * Oct 17, 2008                        Initial generation by hbm2java
 * Aug 19, 2011      10672     jkorman Move refactor to new project
 * Oct 07, 2013       2361     njensen Removed XML annotations
 * 
 * </pre>
 * 
 * @author jkorman
 * @version 1.1
 */
@Entity
@Table(name = "locimage")
@com.raytheon.uf.common.serialization.annotations.DynamicSerialize
public class Locimage extends com.raytheon.uf.common.dataplugin.persist.PersistableDataObject implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement
    private LocimageId id;

    @com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement
    private Location location;

    @com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement
    private String title;

    @com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement
    private String descr;

    @com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement
    private String format;

    @com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement
    private String urlInternal;

    @com.raytheon.uf.common.serialization.annotations.DynamicSerializeElement
    private String urlExternal;

    public Locimage() {
    }

    public Locimage(LocimageId id, Location location) {
        this.id = id;
        this.location = location;
    }

    public Locimage(LocimageId id, Location location, String title,
            String descr, String format, String urlInternal, String urlExternal) {
        this.id = id;
        this.location = location;
        this.title = title;
        this.descr = descr;
        this.format = format;
        this.urlInternal = urlInternal;
        this.urlExternal = urlExternal;
    }

    @EmbeddedId
    @AttributeOverrides( {
            @AttributeOverride(name = "lid", column = @Column(name = "lid", nullable = false, length = 8)),
            @AttributeOverride(name = "imageid", column = @Column(name = "imageid", nullable = false, length = 10)) })
    public LocimageId getId() {
        return this.id;
    }

    public void setId(LocimageId id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lid", nullable = false, insertable = false, updatable = false)
    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Column(name = "title", length = 30)
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "descr", length = 80)
    public String getDescr() {
        return this.descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @Column(name = "format", length = 10)
    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Column(name = "url_internal", length = 120)
    public String getUrlInternal() {
        return this.urlInternal;
    }

    public void setUrlInternal(String urlInternal) {
        this.urlInternal = urlInternal;
    }

    @Column(name = "url_external", length = 120)
    public String getUrlExternal() {
        return this.urlExternal;
    }

    public void setUrlExternal(String urlExternal) {
        this.urlExternal = urlExternal;
    }

}
