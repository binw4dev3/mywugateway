package com.wu.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wu.gw.mybatis.model.CountryCurrencyList;
import com.wu.web.model.RespBean;
import com.wu.web.service.CountryCurrencyListService;

@RestController
public class CountryCurrencyListController {
	
	@Autowired
	CountryCurrencyListService ccListService;
	
	@PostMapping("/addcclist")
    public RespBean addCountryCurrencyList(@RequestBody CountryCurrencyList ccList) {
		/*CountryCurrencyList ccList = new CountryCurrencyList();
		ccList.setListName("CN_USD_CN393");
		ccList.setSCountry("CN");
		ccList.setSCurrency("USD");
		ccList.setSNaid("CN393");
		ccList.setUpdateTime(new Date());*/
		
		return ccListService.addCountryCurrencyList(ccList);
    }
	
	@GetMapping("/getcclist")
	public RespBean getCountryCurrencyList(@RequestParam String listname) {
		CountryCurrencyList ccList = ccListService.getCountryCurrencyList(listname);
		if(ccList != null) {
			return RespBean.ok("Found the Country Currency List in DB.", ccList);
		}
		
		return RespBean.error("Not found the Country Currency List in DB.");
	}
	
	@PutMapping("/updatecclist")
	public RespBean updateCountryCurrencyList(@RequestParam String listname) {
		return ccListService.updateCountryCurrencyListByListName(listname);
	}
}
