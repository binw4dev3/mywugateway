
package com.wu.gw.model.ais;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wu_cards complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wu_cards">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.westernunion.com/schema/xrsi}collection_base">
 *       &lt;sequence>
 *         &lt;element name="wu_card" type="{http://www.westernunion.com/schema/xrsi}wu_card" maxOccurs="10"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wu_cards", propOrder = {
    "wuCard"
})
public class WuCards
    extends CollectionBase
{

    @XmlElement(name = "wu_card", required = true)
    protected List<WuCard> wuCard;

    /**
     * Gets the value of the wuCard property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wuCard property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWuCard().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WuCard }
     * 
     * 
     */
    public List<WuCard> getWuCard() {
        if (wuCard == null) {
            wuCard = new ArrayList<WuCard>();
        }
        return this.wuCard;
    }

}
