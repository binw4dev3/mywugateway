
package com.wu.gw.model.ais;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for name_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="name_type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="C"/>
 *     &lt;enumeration value="D"/>
 *     &lt;enumeration value="M"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "name_type")
@XmlEnum
public enum NameType {


    /**
     * Comany Name Type
     * 
     */
    C,

    /**
     * Domestic Name Type
     * 
     */
    D,

    /**
     * Mexican Name Type
     * 
     */
    M;

    public String value() {
        return name();
    }

    public static NameType fromValue(String v) {
        return valueOf(v);
    }

}
