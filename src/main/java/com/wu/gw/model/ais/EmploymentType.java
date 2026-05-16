
package com.wu.gw.model.ais;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for employment_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="employment_type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Employed"/>
 *     &lt;enumeration value="Self Employed"/>
 *     &lt;enumeration value="Unemployed"/>
 *     &lt;enumeration value="Retired"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "employment_type")
@XmlEnum
public enum EmploymentType {

    @XmlEnumValue("Employed")
    EMPLOYED("Employed"),
    @XmlEnumValue("Self Employed")
    SELF_EMPLOYED("Self Employed"),
    @XmlEnumValue("Unemployed")
    UNEMPLOYED("Unemployed"),
    @XmlEnumValue("Retired")
    RETIRED("Retired");
    private final String value;

    EmploymentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EmploymentType fromValue(String v) {
        for (EmploymentType c: EmploymentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
