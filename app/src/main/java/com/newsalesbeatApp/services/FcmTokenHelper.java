package com.newsalesbeatApp.services;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessaging;

public class FcmTokenHelper {

    private static final String TAG = "FcmTokenHelper";

    public static void fetchFcmToken(FcmTokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Fetching FCM token failed", task.getException());
                        callback.onFailure(task.getException());
                        return;
                    }

                    String token = task.getResult();
                    if (token == null || token.isEmpty()) {
                        Log.e(TAG, "FCM Token is null or empty!");
                        callback.onFailure(new Exception("Token is null or empty"));
                    } else {
                        Log.d(TAG, "FCM Token: " + token);
                        callback.onSuccess(token);
                    }
                });
    }

    public interface FcmTokenCallback {
        void onSuccess(String token);
        void onFailure(Exception e);
    }
}

