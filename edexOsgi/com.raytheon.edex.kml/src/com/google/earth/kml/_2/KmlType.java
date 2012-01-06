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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.08.29 at 09:59:50 AM CDT 
//

package com.google.earth.kml._2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for KmlType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;KmlType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;all&gt;
 *         &lt;element name=&quot;NetworkLinkControl&quot; type=&quot;{http://earth.google.com/kml/2.1}NetworkLinkControlType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}Feature&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "kml")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KmlType", propOrder = {

})
public class KmlType {

    @XmlElement(name = "NetworkLinkControl")
    protected NetworkLinkControlType networkLinkControl;

    @XmlElementRef(name = "Feature", namespace = "http://earth.google.com/kml/2.1", type = JAXBElement.class)
    protected JAXBElement<? extends FeatureType> feature;

    /**
     * Gets the value of the networkLinkControl property.
     * 
     * @return possible object is {@link NetworkLinkControlType }
     * 
     */
    public NetworkLinkControlType getNetworkLinkControl() {
        return networkLinkControl;
    }

    /**
     * Sets the value of the networkLinkControl property.
     * 
     * @param value
     *            allowed object is {@link NetworkLinkControlType }
     * 
     */
    public void setNetworkLinkControl(NetworkLinkControlType value) {
        this.networkLinkControl = value;
    }

    /**
     * Gets the value of the feature property.
     * 
     * @return possible object is {@link JAXBElement }{@code <}{@link FolderType }{@code >}
     *         {@link JAXBElement }{@code <}{@link FeatureType }{@code >}
     *         {@link JAXBElement }{@code <}{@link PlacemarkType }{@code >}
     *         {@link JAXBElement }{@code <}{@link ScreenOverlayType }{@code >}
     *         {@link JAXBElement }{@code <}{@link GroundOverlayType }{@code >}
     *         {@link JAXBElement }{@code <}{@link NetworkLinkType }{@code >}
     *         {@link JAXBElement }{@code <}{@link DocumentType }{@code >}
     * 
     */
    public JAXBElement<? extends FeatureType> getFeature() {
        return feature;
    }

    /**
     * Sets the value of the feature property.
     * 
     * @param value
     *            allowed object is {@link JAXBElement }{@code <}{@link FolderType }{@code >}
     *            {@link JAXBElement }{@code <}{@link FeatureType }{@code >}
     *            {@link JAXBElement }{@code <}{@link PlacemarkType }{@code >}
     *            {@link JAXBElement }{@code <}{@link ScreenOverlayType }{@code >}
     *            {@link JAXBElement }{@code <}{@link GroundOverlayType }{@code >}
     *            {@link JAXBElement }{@code <}{@link NetworkLinkType }{@code >}
     *            {@link JAXBElement }{@code <}{@link DocumentType }{@code >}
     * 
     */
    public void setFeature(JAXBElement<? extends FeatureType> value) {
        this.feature = ((JAXBElement<? extends FeatureType>) value);
    }

}
