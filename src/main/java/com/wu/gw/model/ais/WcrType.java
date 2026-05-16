
package com.wu.gw.model.ais;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wcr_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="wcr_type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="WILL CALL"/>
 *     &lt;enumeration value="RETRIEVAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "wcr_type")
@XmlEnum
public enum WcrType {

    @XmlEnumValue("WILL CALL")
    WILL_CALL("WILL CALL"),
    RETRIEVAL("RETRIEVAL");
    private final String value;

    WcrType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WcrType fromValue(String v) {
        for (WcrType c: WcrType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
