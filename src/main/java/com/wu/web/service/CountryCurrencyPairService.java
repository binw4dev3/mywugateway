package com.wu.web.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wu.gw.mybatis.mapper.CountryCurrencyPairMapper;
import com.wu.gw.mybatis.model.CountryCurrencyPair;
import com.wu.web.model.RespBean;

@Service
public class CountryCurrencyPairService {
	
	@Autowired
	CountryCurrencyPairMapper ccPairMapper;
	
	public RespBean addCountryCurrencyPair(CountryCurrencyPair ccPair) {
		CountryCurrencyPair ccPairDB = ccPairMapper.getCountryCurrencyPairByKeys(ccPair);
		if(ccPairDB == null) {
			ccPair.setUpdateTime(new Date());			
			if(ccPairMapper.addCountryCurrencyPair(ccPair) > 0) {
				return RespBean.ok("New CountryCurrency Pair added.", ccPair);
			} else {
				return RespBean.error("Failed to add new CountryCurrency Pair.");
			}
		} else {
			if(ccPairDB.getDelFlag() == 1) {
				ccPairDB.setDelFlag(0);
				ccPairDB.setUpdateTime(new Date());
				if(ccPairMapper.updateCountryCurrencyPair(ccPairDB) > 0) {
					return RespBean.ok("New CountryCurrency Pair added.", ccPairDB);
				} else {
					return RespBean.error("Failed to add new CountryCurrency Pair.");
				}
			} else {
				return RespBean.error("Failed to add existing CountryCurrency Pair.");
			}
		}
	};
	
	public CountryCurrencyPair getCountryCurrencyPairById(Integer id) {
		return ccPairMapper.getCountryCurrencyPairById(id);
	}
	
	/*public CountryCurrencyPair getCountryCurrencyPairByKeys(CountryCurrencyPair ccPair) {
		return ccPairMapper.getCountryCurrencyPairByKeys(ccPair);
	}*/
	
	public RespBean deleteCountryCurrencyPair(Integer id) {
		CountryCurrencyPair ccPairDB = ccPairMapper.getCountryCurrencyPairById(id);
		if(ccPairDB != null) {
			ccPairDB.setDelFlag(1);
			ccPairDB.setUpdateTime(new Date());
			if(ccPairMapper.updateCountryCurrencyPair(ccPairDB) > 0) {
				return RespBean.ok("CountryCurrency Pair deleted.", ccPairDB);
			} else {
				return RespBean.error("Failed to delete the Country Currency Pair : ", ccPairDB);
			}
		} else {
			return RespBean.error("Failed to delete. Country Currency Pair does not exist : ", ccPairDB);
		}		
	}
}
