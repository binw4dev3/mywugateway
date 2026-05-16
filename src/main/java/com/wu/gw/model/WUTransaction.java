package com.wu.gw.model;

import java.util.HashMap;

import com.wu.gw.model.ais.BankDetails;
import com.wu.gw.model.ais.Channel;
import com.wu.gw.model.ais.DeliveryServices;
import com.wu.gw.model.ais.Financials;
import com.wu.gw.model.ais.ForeignRemoteSystem;
import com.wu.gw.model.ais.GwpGbsDevice;
import com.wu.gw.model.ais.PaymentDetails;
import com.wu.gw.model.ais.Receiver;
import com.wu.gw.model.ais.Sender;
import com.wu.gw.model.das.DASQueryResult;

import lombok.Builder;

//@Builder
public class WUTransaction {
	
	private Channel channel;
	
	private GwpGbsDevice device;
	
	private BankDetails bankDetails;
	
	private DeliveryServices deliveryServices;

	private Sender sender;
	
	private Receiver receiver;
	
	private PaymentDetails paymentDetails;
	
	private Financials financials;
	
	private String MTCN;
	
	private String newMTCN;
	
	private ForeignRemoteSystem foreignRemoteSystem;
	
	private String dasName;
	
	private HashMap<String, String> dasFilters = new HashMap<String, String>();
	
	private DASQueryResult dasQueryData = new DASQueryResult();

	public WUTransaction() {
		
	}
	
	public void setSender(Sender sender) {
		this.sender = sender;
	}
	
	public Sender getSender() {
		return this.sender;
	}

	public Receiver getReceiver() {
		return receiver;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public PaymentDetails getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(PaymentDetails paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public Financials getFinancials() {
		return financials;
	}

	public void setFinancials(Financials financials) {
		this.financials = financials;
	}
	
	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public GwpGbsDevice getDevice() {
		return device;
	}

	public void setDevice(GwpGbsDevice device) {
		this.device = device;
	}

	public BankDetails getBankDetails() {
		return bankDetails;
	}

	public void setBankDetails(BankDetails bankDetails) {
		this.bankDetails = bankDetails;
	}

	public DeliveryServices getDeliveryServices() {
		return deliveryServices;
	}

	public void setDeliveryServices(DeliveryServices deliveryServices) {
		this.deliveryServices = deliveryServices;
	}
	
	public String getMTCN() {
		return MTCN;
	}

	public void setMTCN(String mTCN) {
		MTCN = mTCN;
	}

	public String getNewMTCN() {
		return newMTCN;
	}

	public void setNewMTCN(String newMTCN) {
		this.newMTCN = newMTCN;
	}

	public ForeignRemoteSystem getForeignRemoteSystem() {
		return foreignRemoteSystem;
	}

	public void setForeignRemoteSystem(ForeignRemoteSystem foreignRemoteSystem) {
		this.foreignRemoteSystem = foreignRemoteSystem;
	}
	
	public String getDasName() {
		return dasName;
	}

	public void setDasName(String dasName) {
		this.dasName = dasName;
	}

	public HashMap<String, String> getDasFilters() {
		return dasFilters;
	}

	public void setDasFilters(HashMap<String, String> dasFilters) {
		this.dasFilters = dasFilters;
	}
	
	public DASQueryResult getDasQueryData() {
		return dasQueryData;
	}

	public void setDasQueryData(DASQueryResult dasQueryData) {
		this.dasQueryData = dasQueryData;
	}
}
