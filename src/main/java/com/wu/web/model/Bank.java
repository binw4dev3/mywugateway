package com.wu.web.model;

import java.util.List;

public class Bank {
    private Integer id;
    private String bankcode;
    private String bankname;
    private List<Branch> branch;

    public Bank() {
    }

    public Bank(String bankcode, String bankname) {
        this.bankcode = bankcode;
        this.bankname = bankname;
    }

    public Bank(Integer id, String bankcode, String bankname) {
        this.id = id;
        this.bankcode = bankcode;
        this.bankname = bankname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public List<Branch> getBranchlist() {
        return branch;
    }

    public void setBranchlist(List<Branch> branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return "Banklist{" +
                "id=" + id +
                ", bankcode='" + bankcode + '\'' +
                ", bankname='" + bankname + '\'' +
                ", branchlist=" + branch +
                '}';
    }
}
