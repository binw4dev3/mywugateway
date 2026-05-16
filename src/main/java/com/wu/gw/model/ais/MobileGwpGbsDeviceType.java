
package com.wu.gw.model.ais;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mobile_gwp_gbs_device_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="mobile_gwp_gbs_device_type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ATM"/>
 *     &lt;enumeration value="WEB"/>
 *     &lt;enumeration value="AGENT"/>
 *     &lt;enumeration value="CSR"/>
 *     &lt;enumeration value="IVR"/>
 *     &lt;enumeration value="TMSI"/>
 *     &lt;enumeration value="IMSI"/>
 *     &lt;enumeration value="MSISDN"/>
 *     &lt;enumeration value="ESN"/>
 *     &lt;enumeration value="IMEI"/>
 *     &lt;enumeration value="MEID"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "mobile_gwp_gbs_device_type")
@XmlEnum
public enum MobileGwpGbsDeviceType {

    ATM,
    WEB,
    AGENT,
    CSR,
    IVR,
    TMSI,
    IMSI,
    MSISDN,
    ESN,
    IMEI,
    MEID;

    public String value() {
        return name();
    }

    public static MobileGwpGbsDeviceType fromValue(String v) {
        return valueOf(v);
    }

}
