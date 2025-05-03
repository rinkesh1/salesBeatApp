package com.newsalesbeatApp.pojo;

/**
 * Created by Dhirendra Thakur on 12-02-2018.
 */

public class ChatItem {

    String message;
    String date;
    String contact;
    String channel;
    String timeStamp;
    String image;
    String video;
    String audio;
    String docs;
    String location;
    String status;
    String empName;

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String dt) {
        this.date = dt;
    }

    public String getContact() {
        return this.contact;
    }

    public void setContact(String cnct) {
        this.contact = cnct;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(String time) {
        this.timeStamp = time;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String img) {
        this.image = img;
    }

    public String getVideo() {
        return this.video;
    }

    public void setVideo(String vdo) {
        this.video = vdo;
    }

    public String getAudio() {
        return this.audio;
    }

    public void setAudio(String ado) {
        this.audio = ado;
    }

    public String getDocs() {
        return this.docs;
    }

    public void setDocs(String dcs) {
        this.docs = dcs;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String loc) {
        this.location = loc;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String st) {
        this.status = st;
    }
}
