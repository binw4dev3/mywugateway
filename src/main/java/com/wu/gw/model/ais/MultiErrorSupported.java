
package com.wu.gw.model.ais;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for multi_error_supported.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="multi_error_supported">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Y"/>
 *     &lt;enumeration value="N"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "multi_error_supported")
@XmlEnum
public enum MultiErrorSupported {

    Y,
    N;

    public String value() {
        return name();
    }

    public static MultiErrorSupported fromValue(String v) {
        return valueOf(v);
    }

}
