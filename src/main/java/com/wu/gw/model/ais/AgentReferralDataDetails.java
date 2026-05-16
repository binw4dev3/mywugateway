
package com.wu.gw.model.ais;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for agent_referral_data_details complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="agent_referral_data_details">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.westernunion.com/schema/xrsi}collection_base">
 *       &lt;sequence>
 *         &lt;element name="agent_referral_data" type="{http://www.westernunion.com/schema/xrsi}agent_referral_data" maxOccurs="9" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "agent_referral_data_details", propOrder = {
    "agentReferralData"
})
public class AgentReferralDataDetails
    extends CollectionBase
{

    @XmlElement(name = "agent_referral_data")
    protected List<AgentReferralData> agentReferralData;

    /**
     * Gets the value of the agentReferralData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the agentReferralData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAgentReferralData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AgentReferralData }
     * 
     * 
     */
    public List<AgentReferralData> getAgentReferralData() {
        if (agentReferralData == null) {
            agentReferralData = new ArrayList<AgentReferralData>();
        }
        return this.agentReferralData;
    }

}
