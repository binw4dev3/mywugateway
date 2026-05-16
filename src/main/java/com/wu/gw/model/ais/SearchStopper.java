
package com.wu.gw.model.ais;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for search_stopper.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="search_stopper">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="approximate"/>
 *     &lt;enumeration value="exact"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "search_stopper")
@XmlEnum
public enum SearchStopper {

    @XmlEnumValue("approximate")
    APPROXIMATE("approximate"),
    @XmlEnumValue("exact")
    EXACT("exact");
    private final String value;

    SearchStopper(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SearchStopper fromValue(String v) {
        for (SearchStopper c: SearchStopper.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
