
package com.wu.gw.model.ais;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for phone_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="phone_type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Home Phone"/>
 *     &lt;enumeration value="Business Phone"/>
 *     &lt;enumeration value="Cell Phone"/>
 *     &lt;enumeration value="Pay Phone"/>
 *     &lt;enumeration value="Other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "phone_type")
@XmlEnum
public enum PhoneType {

    @XmlEnumValue("Home Phone")
    HOME_PHONE("Home Phone"),
    @XmlEnumValue("Business Phone")
    BUSINESS_PHONE("Business Phone"),
    @XmlEnumValue("Cell Phone")
    CELL_PHONE("Cell Phone"),
    @XmlEnumValue("Pay Phone")
    PAY_PHONE("Pay Phone"),
    @XmlEnumValue("Other")
    OTHER("Other");
    private final String value;

    PhoneType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PhoneType fromValue(String v) {
        for (PhoneType c: PhoneType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
