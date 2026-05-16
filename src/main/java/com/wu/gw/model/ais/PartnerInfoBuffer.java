
package com.wu.gw.model.ais;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for partner_info_buffer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="partner_info_buffer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="state_license_number" type="{http://www.westernunion.com/schema/xrsi}state_license_number" minOccurs="0"/>
 *         &lt;element name="action_indicator" type="{http://www.westernunion.com/schema/xrsi}action_indicator" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "partner_info_buffer", propOrder = {
    "stateLicenseNumber",
    "actionIndicator"
})
public class PartnerInfoBuffer {

    @XmlElement(name = "state_license_number")
    protected String stateLicenseNumber;
    @XmlElement(name = "action_indicator")
    @XmlSchemaType(name = "string")
    protected ActionIndicator actionIndicator;

    /**
     * Gets the value of the stateLicenseNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStateLicenseNumber() {
        return stateLicenseNumber;
    }

    /**
     * Sets the value of the stateLicenseNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStateLicenseNumber(String value) {
        this.stateLicenseNumber = value;
    }

    /**
     * Gets the value of the actionIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link ActionIndicator }
     *     
     */
    public ActionIndicator getActionIndicator() {
        return actionIndicator;
    }

    /**
     * Sets the value of the actionIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionIndicator }
     *     
     */
    public void setActionIndicator(ActionIndicator value) {
        this.actionIndicator = value;
    }

}
