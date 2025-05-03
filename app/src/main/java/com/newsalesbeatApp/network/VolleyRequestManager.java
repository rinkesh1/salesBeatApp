package com.newsalesbeatApp.network;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyRequestManager {
    private static VolleyRequestManager instance;
    private RequestQueue requestQueue;
    private static Context context;

    private VolleyRequestManager(Context context) {
        VolleyRequestManager.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleyRequestManager getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyRequestManager(context.getApplicationContext());
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public void cancelAllRequests() {
        if (requestQueue != null) {
//            requestQueue.cancelAll();
        }
    }

    public void cancelRequestsWithTag(String tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
} 