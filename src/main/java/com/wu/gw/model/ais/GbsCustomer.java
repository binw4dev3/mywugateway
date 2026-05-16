
package com.wu.gw.model.ais;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for gbs_customer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gbs_customer">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.westernunion.com/schema/xrsi}channel_customer">
 *       &lt;sequence>
 *         &lt;element name="bank_account" type="{http://www.westernunion.com/schema/xrsi}gbs_bank_details" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gbs_customer", propOrder = {
    "bankAccount"
})
public class GbsCustomer
    extends ChannelCustomer
{

    @XmlElement(name = "bank_account")
    protected GbsBankDetails bankAccount;

    /**
     * Gets the value of the bankAccount property.
     * 
     * @return
     *     possible object is
     *     {@link GbsBankDetails }
     *     
     */
    public GbsBankDetails getBankAccount() {
        return bankAccount;
    }

    /**
     * Sets the value of the bankAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link GbsBankDetails }
     *     
     */
    public void setBankAccount(GbsBankDetails value) {
        this.bankAccount = value;
    }

}
