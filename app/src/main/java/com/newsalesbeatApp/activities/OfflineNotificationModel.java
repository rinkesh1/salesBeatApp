package com.newsalesbeatApp.activities;

import java.io.Serializable;

public class OfflineNotificationModel implements Serializable {

    private String strPrayerHeader;
    private String strPrayer;
    private String strNumber;
    private String readStatus;
    private int notifId;

    public OfflineNotificationModel(String strPrayerHeader, String strPrayer, String strNumber, String readStatus, int notifId) {
        this.strPrayerHeader = strPrayerHeader;
        this.strPrayer = strPrayer;
        this.strNumber = strNumber;
        this.notifId = notifId;
        this.readStatus = readStatus;
    }

    public String getStrPrayerHeader() {
        return strPrayerHeader;
    }

    public void setStrPrayerHeader(String strPrayerHeader) {
        this.strPrayerHeader = strPrayerHeader;
    }

    public String getStrPrayer() {
        return strPrayer;
    }

    public void setStrPrayer(String strPrayer) {
        this.strPrayer = strPrayer;
    }

    public String getStrNumber() {
        return strNumber;
    }

    public void setStrNumber(String strNumber) {
        this.strNumber = strNumber;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public int getNotifId() {
        return notifId;
    }

    public void setNotifId(int notifId) {
        this.notifId = notifId;
    }
}
