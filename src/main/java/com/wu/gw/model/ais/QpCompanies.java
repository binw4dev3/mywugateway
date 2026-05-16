
package com.wu.gw.model.ais;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for qp_companies complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="qp_companies">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.westernunion.com/schema/xrsi}collection_base">
 *       &lt;sequence>
 *         &lt;element name="qp_company" type="{http://www.westernunion.com/schema/xrsi}qp_company" maxOccurs="10"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "qp_companies", propOrder = {
    "qpCompany"
})
public class QpCompanies
    extends CollectionBase
{

    @XmlElement(name = "qp_company", required = true)
    protected List<QpCompany> qpCompany;

    /**
     * Gets the value of the qpCompany property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qpCompany property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQpCompany().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QpCompany }
     * 
     * 
     */
    public List<QpCompany> getQpCompany() {
        if (qpCompany == null) {
            qpCompany = new ArrayList<QpCompany>();
        }
        return this.qpCompany;
    }

}
