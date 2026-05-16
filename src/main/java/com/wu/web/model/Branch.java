package com.wu.web.model;

public class Branch {
    private Integer id;
    private String bankcode;
    private String branchcode;
    private String branchname;
    private String locationcode;
    private String locationname;

    public Branch() {
    }

    public Branch(String bankcode, String branchcode, String branchname, String locationcode, String locationname) {
        this.bankcode = bankcode;
        this.branchcode = branchcode;
        this.branchname = branchname;
        this.locationcode = locationcode;
        this.locationname = locationname;
    }

    public String getBranchcode() {
        return branchcode;
    }

    public void setBranchcode(String branchcode) {
        this.branchcode = branchcode;
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

    public String getBranchname() {
        return branchname;
    }

    public void setBranchname(String branchname) {
        this.branchname = branchname;
    }

    public String getLocationcode() {
        return locationcode;
    }

    public void setLocationcode(String locationcode) {
        this.locationcode = locationcode;
    }

    public String getLocationname() {
        return locationname;
    }

    public void setLocationname(String locationname) {
        this.locationname = locationname;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "id=" + id +
                ", bankcode='" + bankcode + '\'' +
                ", branchcode='" + branchcode + '\'' +
                ", branchname='" + branchname + '\'' +
                ", locationcode='" + locationcode + '\'' +
                ", locationname='" + locationname + '\'' +
                '}';
    }
}
