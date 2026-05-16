package com.wu.gw.mybatis.model;

import java.util.Date;

public class CountryCurrencyList {

	private Integer id;
	private String listName;
	private String sCountry;
	private String sCurrency;
	private String sNaid;
	private Date updateTime;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getListName() {
		return listName;
	}
	public void setListName(String listName) {
		this.listName = listName;
	}
	public String getSCountry() {
		return sCountry;
	}
	public void setSCountry(String sCountry) {
		this.sCountry = sCountry;
	}
	public String getSCurrency() {
		return sCurrency;
	}
	public void setSCurrency(String sCurrency) {
		this.sCurrency = sCurrency;
	}
	public String getSNaid() {
		return sNaid;
	}
	public void setSNaid(String sNaid) {
		this.sNaid = sNaid;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	@Override
	public String toString() {
		return "CountryCurrencyList [id=" + id + ", listName=" + listName + ", sCountry=" + sCountry + ", sCurrency="
				+ sCurrency + ", sNaid=" + sNaid + ", updateTime=" + updateTime + "]";
	}
}
