
package com.wu.gw.model.ais;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for channel_customer_identity_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="channel_customer_identity_type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MOBILE_ACCOUNT"/>
 *     &lt;enumeration value="BANK_ACCOUNT"/>
 *     &lt;enumeration value="PREPAID_CARD_NUMBER"/>
 *     &lt;enumeration value="EMAIL_ADDRESS"/>
 *     &lt;enumeration value="LOYALTY_CARD_NUMBER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "channel_customer_identity_type")
@XmlEnum
public enum ChannelCustomerIdentityType {

    MOBILE_ACCOUNT,
    BANK_ACCOUNT,
    PREPAID_CARD_NUMBER,
    EMAIL_ADDRESS,
    LOYALTY_CARD_NUMBER;

    public String value() {
        return name();
    }

    public static ChannelCustomerIdentityType fromValue(String v) {
        return valueOf(v);
    }

}
