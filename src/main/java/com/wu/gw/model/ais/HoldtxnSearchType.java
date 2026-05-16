
package com.wu.gw.model.ais;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for holdtxn_search_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="holdtxn_search_type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EDIT"/>
 *     &lt;enumeration value="SELECT"/>
 *     &lt;enumeration value="RELEASE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "holdtxn_search_type")
@XmlEnum
public enum HoldtxnSearchType {

    EDIT,
    SELECT,
    RELEASE;

    public String value() {
        return name();
    }

    public static HoldtxnSearchType fromValue(String v) {
        return valueOf(v);
    }

}
