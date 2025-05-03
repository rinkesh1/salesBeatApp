package com.newsalesbeatApp.pojo;

import java.util.ArrayList;

/**
 * Created by MTC on 19-09-2017.
 */

public class MyPjp {

    String pjpId;
    String date;
    String beatName;
    String assigneeEmp;
    String assigneeAdmin;
    String distributorName;
    String townName;
    String beat_id;
    String distributor_id;
    String activity;
    String tc, pc, sale, emp, remarks;
    String jointworkingwith;
    ArrayList<MyPjp> myPjpArrayList = new ArrayList<>();

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

    public String getEmp() {
        return emp;
    }

    public void setEmp(String emp) {
        this.emp = emp;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getJointworkingwith() {
        return jointworkingwith;
    }

    public void setJointworkingwith(String jointworkingwith) {
        this.jointworkingwith = jointworkingwith;
    }

    public ArrayList<MyPjp> getMyPjpArrayList() {
        return myPjpArrayList;
    }

    public void setMyPjpArrayList(ArrayList<MyPjp> myPjpArrayList) {
        this.myPjpArrayList = myPjpArrayList;
    }

    public String getPjpId() {
        return this.pjpId;
    }

    public void setPjpId(String pjpId) {
        this.pjpId = pjpId;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBeatName() {
        return this.beatName;
    }

    public void setBeatName(String beatName) {
        this.beatName = beatName;
    }

    public String getBeat_id() {
        return this.beat_id;
    }

    public void setBeat_id(String beat_id) {
        this.beat_id = beat_id;
    }

    public String getAssigneeEmp() {
        return this.assigneeEmp;
    }

    public void setAssigneeEmp(String assigneeEmp) {
        this.assigneeEmp = assigneeEmp;
    }

    public String getAssigneeAdmin() {
        return this.assigneeAdmin;
    }

    public void setAssigneeAdmin(String assigneeAdmin) {
        this.assigneeAdmin = assigneeAdmin;
    }

    public String getDistributorName() {
        return this.distributorName;
    }

    public void setDistributorName(String distributorName) {
        this.distributorName = distributorName;
    }

    public String getDistributor_id() {
        return this.distributor_id;
    }

    public void setDistributor_id(String distributor_id) {
        this.distributor_id = distributor_id;
    }

    public String getTownName() {
        return this.townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }
}
