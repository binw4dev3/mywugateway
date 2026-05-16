
package com.wu.gw.model.ais;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fusion_screen.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="fusion_screen">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;minLength value="0"/>
 *     &lt;maxLength value="2"/>
 *     &lt;enumeration value="MP"/>
 *     &lt;enumeration value="PL"/>
 *     &lt;enumeration value="OV"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "fusion_screen")
@XmlEnum
public enum FusionScreen {

    MP,
    PL,
    OV;

    public String value() {
        return name();
    }

    public static FusionScreen fromValue(String v) {
        return valueOf(v);
    }

}
