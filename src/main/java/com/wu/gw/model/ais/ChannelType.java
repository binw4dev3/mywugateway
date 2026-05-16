
package com.wu.gw.model.ais;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for channel_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="channel_type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CSC"/>
 *     &lt;enumeration value="AGT"/>
 *     &lt;enumeration value="H2H"/>
 *     &lt;enumeration value="MTBP"/>
 *     &lt;enumeration value="LIS"/>
 *     &lt;enumeration value="WEB"/>
 *     &lt;enumeration value="WGD"/>
 *     &lt;enumeration value="AGT"/>
 *     &lt;enumeration value="IVR"/>
 *     &lt;enumeration value="WUMT"/>
 *     &lt;enumeration value="TMT"/>
 *     &lt;enumeration value="MMT"/>
 *     &lt;enumeration value="CSC"/>
 *     &lt;enumeration value="SMT"/>
 *     &lt;enumeration value="SMARTPHONE"/>
 *     &lt;enumeration value="MYWU"/>
 *     &lt;enumeration value="SFSS"/>
 *     &lt;enumeration value="WGDT"/>
 *     &lt;enumeration value="SFPS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "channel_type")
@XmlEnum
public enum ChannelType {

    CSC("CSC"),
    AGT("AGT"),
    @XmlEnumValue("H2H")
    H_2_H("H2H"),
    MTBP("MTBP"),
    LIS("LIS"),
    WEB("WEB"),
    WGD("WGD"),
    IVR("IVR"),
    WUMT("WUMT"),
    TMT("TMT"),
    MMT("MMT"),
    SMT("SMT"),
    SMARTPHONE("SMARTPHONE"),
    MYWU("MYWU"),
    SFSS("SFSS"),
    WGDT("WGDT"),
    SFPS("SFPS");
    private final String value;

    ChannelType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ChannelType fromValue(String v) {
        for (ChannelType c: ChannelType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
