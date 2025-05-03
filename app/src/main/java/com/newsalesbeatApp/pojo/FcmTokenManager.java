package com.newsalesbeatApp.pojo;

public class FcmTokenManager {
    private static String fcmToken;

    public static String getFcmToken() {
        return fcmToken;
    }

    public static void setFcmToken(String token) {
        fcmToken = token;
    }
}

