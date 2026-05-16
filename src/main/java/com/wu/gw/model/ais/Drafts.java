
package com.wu.gw.model.ais;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for drafts complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="drafts">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.westernunion.com/schema/xrsi}collection_base">
 *       &lt;sequence>
 *         &lt;element name="draft_amount" type="{http://www.westernunion.com/schema/xrsi}draft_amount" maxOccurs="9"/>
 *         &lt;element name="draft_number" type="{http://www.westernunion.com/schema/xrsi}draft_amount" maxOccurs="9"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "drafts", propOrder = {
    "draftAmount",
    "draftNumber"
})
public class Drafts
    extends CollectionBase
{

    @XmlElement(name = "draft_amount", required = true)
    protected List<String> draftAmount;
    @XmlElement(name = "draft_number", required = true)
    protected List<String> draftNumber;

    /**
     * Gets the value of the draftAmount property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the draftAmount property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDraftAmount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDraftAmount() {
        if (draftAmount == null) {
            draftAmount = new ArrayList<String>();
        }
        return this.draftAmount;
    }

    /**
     * Gets the value of the draftNumber property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the draftNumber property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDraftNumber().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDraftNumber() {
        if (draftNumber == null) {
            draftNumber = new ArrayList<String>();
        }
        return this.draftNumber;
    }

}
