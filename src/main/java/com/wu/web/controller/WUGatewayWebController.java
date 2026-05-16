package com.wu.web.controller;

import java.util.List;
import java.util.Map;

import com.wu.console.APNDynamicRequest;
import com.wu.web.model.Bank;
import com.wu.web.model.Branch;
import com.wu.web.service.BankService;
import com.wu.web.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.wu.gw.util.UtilFunctions;

@RestController
public class WUGatewayWebController {

    @Autowired
    BankService bankService;
    @Autowired
    BranchService branchService;
    @Autowired
    APNDynamicRequest apnDynamicRequest;

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @PostMapping("/requestGWService")
    @CrossOrigin(value = "http://localhost:3000", maxAge = 1800, allowedHeaders = "*") // Add CORS support for specific
    // service
    @ResponseBody
    public String requestGWService(@RequestBody Map<String, Object> dataMap) {
        String gwURL = (String) dataMap.get("gwURL");
        String requestMsg = (String) dataMap.get("requestMsg");

        return UtilFunctions.requestGatewayService(gwURL, requestMsg);
    }

	/*@PostMapping("/requestGWService")
	@CrossOrigin
	//@CrossOrigin(value = "http://10.47.241.244:3000", maxAge = 1800, allowedHeaders = "*") // Add CORS support for specific service
	@ResponseBody()
	public String getTaskData(
			 @RequestParam(value = "gwURL", required = true) String gwURL,
			 @RequestParam(value = "requestMsg", required = true) String requestMsg) {

		return UtilFunctions.requestGatewayService(gwURL, requestMsg);
	}*/

    @GetMapping("/bank")
    public List<Bank> getAllBanks() {
        return bankService.getAllBanks();
    }
    @PostMapping("/bank")
    public boolean addBank(@RequestBody Bank bank) {
        APNDynamicRequest my = apnDynamicRequest;
        return bankService.addBank(bank);
    }
    @PostMapping("/branch")
    public boolean addBranch(@RequestBody Branch branch) {
        return branchService.addBranch(branch);
    }
}