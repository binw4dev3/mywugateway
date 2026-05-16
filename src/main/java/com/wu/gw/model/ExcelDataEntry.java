package com.wu.gw.model;

import com.wu.gw.model.ais.CountryCurrencyInfo;
import com.wu.gw.model.ais.GeneralName;

public class ExcelDataEntry {
	
	private String amount = null;
	
	private String Fee = null;

	private String transactionType = null;
	
	private GeneralName senderName = null;
	
	private GeneralName receiverName = null;
	
	private CountryCurrencyInfo sendCountryCurrency = null;

	private CountryCurrencyInfo receiveCountryCurrency = null;
	
	private String MTCN = null;
	
	private String refundMTCN = null;

	private String counterId = null;
	
	private String naid = null;

	private String apiType = null;
	
	private String serviceType = null;

	private int rowNum = 0;
	
	private boolean isSenderNameAutoGen = false;
	
	private boolean isReceiverNameAutoGen = false;
	
	private String fxRate = null;
	
	private String senderAddress = null;
	
	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public boolean isSenderNameAutoGen() {
		return isSenderNameAutoGen;
	}

	public void setSenderNameAutoGen(boolean isSenderNameAutoGen) {
		this.isSenderNameAutoGen = isSenderNameAutoGen;
	}

	public boolean isReceiverNameAutoGen() {
		return isReceiverNameAutoGen;
	}

	public void setReceiverNameAutoGen(boolean isReceiverNameAutoGen) {
		this.isReceiverNameAutoGen = isReceiverNameAutoGen;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public String getMTCN() {
		return MTCN;
	}

	public void setMTCN(String mTCN) {
		MTCN = mTCN;
	}

	public String getRefundMTCN() {
		return refundMTCN;
	}

	public void setRefundMTCN(String refundMTCN) {
		this.refundMTCN = refundMTCN;
	}
	
	public String getNaid() {
		return naid;
	}

	public void setNaid(String naid) {
		this.naid = naid;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public String getFee() {
		return Fee;
	}

	public void setFee(String fee) {
		Fee = fee;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String amountType) {
		this.transactionType = amountType;
	}

	public GeneralName getSenderName() {
		return senderName;
	}

	public void setSenderName(GeneralName senderName) {
		this.senderName = senderName;
	}

	public GeneralName getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(GeneralName receiverName) {
		this.receiverName = receiverName;
	}

	public CountryCurrencyInfo getSendCountryCurrency() {
		return sendCountryCurrency;
	}

	public void setSendCountryCurrency(CountryCurrencyInfo sendCountryCurrency) {
		this.sendCountryCurrency = sendCountryCurrency;
	}

	public CountryCurrencyInfo getReceiveCountryCurrency() {
		return receiveCountryCurrency;
	}

	public void setReceiveCountryCurrency(CountryCurrencyInfo receiveCountryCurrency) {
		this.receiveCountryCurrency = receiveCountryCurrency;
	}

	public String getFxRate() {
		return fxRate;
	}

	public void setFxRate(String fxRate) {
		this.fxRate = fxRate;
	}

	public String getCounterId() {
		return counterId;
	}

	public void setCounterId(String counterId) {
		this.counterId = counterId;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}
}
