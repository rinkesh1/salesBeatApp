package com.newsalesbeatApp.utilityclass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.newsalesbeatApp.interfaces.OnHomePressedListener;

/**
 * Created by abc on 9/14/18.
 */

public class HomeWatcher {
    static final String TAG = "hg";
    private Context mContext;
    private IntentFilter mFilter;
    private OnHomePressedListener mListener;
    private InnerReceiver mReceiver;

    public HomeWatcher(Context context) {
        mContext = context;
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    public void setOnHomePressedListener(OnHomePressedListener listener) {
        mListener = listener;
        mReceiver = new InnerReceiver();
    }

    public void startWatch() {
        if (mReceiver != null) {
            mContext.registerReceiver(mReceiver, mFilter, "android.permission.BROADCAST_CLOSE_SYSTEM_DIALOGS", null, Context.RECEIVER_NOT_EXPORTED);
        }
    }

    public void stopWatch() {
        if (mReceiver != null) {
            try {
                mContext.unregisterReceiver(mReceiver);
            } catch (Exception e) {
            }
        }
    }

    class InnerReceiver extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    Log.e(TAG, "action:" + action + ",reason:" + reason);
                    if (mListener != null) {
                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                            mListener.onHomePressed();
                        } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                            mListener.onHomeLongPressed();
                        }
                    }
                }
            }
        }
    }
}
