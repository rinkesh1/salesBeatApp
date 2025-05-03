package com.newsalesbeatApp.utilityclass;

/*
 * Created by MTC on 09-08-2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.CheckCon;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class UtilityClass {

    private String TAG = "UtilityClass";
    private Context context;
    private NetworkInfo info = null;
    private boolean flag = true;
    private CheckCon checkCon;
    private SharedPreferences prefSFA, tempSfa;
    private SalesBeatDb salesBeatDb;

    private FirebaseAnalytics mFirebaseAnalytics;


    public UtilityClass(Context context) {
        this.context = context;
        prefSFA = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempSfa = context.getSharedPreferences(context.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        salesBeatDb = SalesBeatDb.getHelper(context);
        checkCon = new CheckCon();
        new CheckConnection().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    private static Calendar getCalendarForNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    public void setOrderEvent(String eid, String rid, String orderDate, String orderType,
                              String orderTime, String latitude, String longitude) {

        Bundle bundle = new Bundle();
        bundle.putString("Eid", eid);
        bundle.putString("Rid", rid);
        bundle.putString("OrderDate", orderDate);
        bundle.putString("OrderType", orderType);
        bundle.putString("OrderTime", orderTime);
        bundle.putString("Latitude", latitude);
        bundle.putString("Longitude", longitude);
        mFirebaseAnalytics.logEvent("Order", bundle);

    }

    public void setEvent(String eid, String orderDate, String orderType,
                         String orderTime, String latitude, String longitude) {

        Bundle bundle = new Bundle();
        bundle.putString("Eid", eid);
        bundle.putString("OrderDate", orderDate);
        bundle.putString("EventType", orderType);
        bundle.putString("OrderTime", orderTime);
        bundle.putString("Latitude", latitude);
        bundle.putString("Longitude", longitude);
        mFirebaseAnalytics.logEvent("Event", bundle);

    }

    public static boolean isValidBase64(String base64) {
        try {
            if (base64.contains(",")) {
                base64 = base64.substring(base64.indexOf(",") + 1);
            }
            byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
            String reEncoded = Base64.encodeToString(decodedBytes, Base64.DEFAULT).trim();
            return base64.trim().equals(reEncoded);
        } catch (IllegalArgumentException e) {
            return false; // Not a valid Base64 string
        }
    }


    public boolean isInternetConnected() {

        ConnectivityManager cm;
        NetworkInfo wifiNetwork, mobileNetwork;

        try {

            cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null)
                info = cm.getActiveNetworkInfo();

            //for network type wi-fi
            if (cm != null && info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {

                wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifiNetwork != null && wifiNetwork.isConnected()) {

                    return true;//checkCon.isConnected();

                } else {

                    return false;
                }

                // for network type mobile
            } else if (cm != null && info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {

                mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (mobileNetwork != null && mobileNetwork.isConnected()) {

                    if (flag && isConnectionFast(info.getType(), info.getSubtype())) {

                        return true;//checkCon.isConnected();

                    } else if (flag && !isConnectionFast(info.getType(), info.getSubtype())) {

                        //Toast.makeText(context, "Seems your internet is too slow", Toast.LENGTH_SHORT).show();

                        return true;//checkCon.isConnected();

                    } else {

                        return false;
                    }

                } else {

                    return false;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    private boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                /*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public Calendar getCalendarObject() {
        return Calendar.getInstance();
    }

    public SimpleDateFormat getDMYDateFormat() {

        return new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

    }

    public SimpleDateFormat getYMDDateFormat() {

        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    }

    public SimpleDateFormat getMYDateFormat() {

        return new SimpleDateFormat("MM/yyyy", Locale.ENGLISH);

    }

//    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
//                                                  int reqWidth, int reqHeight) {
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(res, resId, options);
//
//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeResource(res, resId, options);
//    }

    public SimpleDateFormat getTimeStampFormat() {

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    }

    public String getCurrentDateInMYformat() {

        final Calendar cc = java.util.Calendar.getInstance();
        SimpleDateFormat sdff = getMYDateFormat();
        return sdff.format(cc.getTime());
    }

    private boolean isNetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            flag = !ipAddr.equals("");

        } catch (Exception e) {
            //e.printStackTrace();
            flag = false;
        }

        return flag;
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public String get12Format(String timeT) {

        DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
        Date date = null;
        String output = null;
        try {
            //Conversion of input String to date
            date = df.parse(timeT);
            //old date format to new date format
            output = outputformat.format(date);

            return output;

        } catch (ParseException pe) {
            pe.printStackTrace();
            return "";
        }
    }

    public String getDateFormat(String dateInput) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("dd, MMM, yyyy hh:mm:ss aa", Locale.ENGLISH);
        Date date = null;
        String output = null;
        try {
            //Conversion of input String to date
            date = df.parse(dateInput);
            //old date format to new date format
            output = outputformat.format(date);

            return output;
        } catch (ParseException pe) {
            pe.printStackTrace();
            return null;
        }
    }

    public String formateIn12(String time) {

        try {

            SimpleDateFormat _24HourSDF = new SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH);
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            Date _24HourDt = _24HourSDF.parse(time);
            return _12HourSDF.format(_24HourDt);

        } catch (Exception e) {
            //e.printStackTrace();
            return time;
        }

        //return "";
    }

    public String getAppVersion() {

        try {

            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            String verCode = String.valueOf(pInfo.versionCode);

            //to check live or test
            String app = "";
            if (context.getString(R.string.url_mode).equalsIgnoreCase("T"))
                app = "T";
            else if (context.getString(R.string.url_mode).equalsIgnoreCase("L"))
                app = "L";

            return version + app + "(" + verCode + ")";

        } catch (Exception e) {
            //e.printStackTrace();
        }

        return "";
    }

    public void initailizeAndResetPrefAndDatabase() {

        Calendar cal = Calendar.getInstance();
        String date = getYMDDateFormat().format(cal.getTime());

        //@Umesh 10-10-2022
//        Sentry.capture("initailizeAndResetPrefAndDatabase :Saved Date = " + prefSFA.getString(context.getString(R.string.date_key), "") +" On "+ new Date()+",eid:"+prefSFA.getString(context.getString(R.string.emp_id_key), "") );

        //clearing SharedPreferences and database....on the basis of date change.....and record..status was success
        Log.e(TAG, "Saved Date = " + prefSFA.getString(context.getString(R.string.date_key), "") + " AND Current Date = " + date);

        if (!prefSFA.getString(context.getString(R.string.date_key), "").equalsIgnoreCase(date)) {

            resetPref(date);

        }

        refreshDataBase();
    }

    public void refreshDataBase() {

        try {

            Log.e("DataClear", "===>Called");
            Calendar cal = Calendar.getInstance();
            String date = getYMDDateFormat().format(cal.getTime());

            salesBeatDb.deleteAllDataFromOderPlacedByRetailersTable4(date);
            salesBeatDb.deleteSpecificDataFromNewRetailerListTable4(date);
            salesBeatDb.deleteSpecificDataFromNewOrderEntryListTable4(date);
            salesBeatDb.deleteSpecificNewRetailerFromOrderPlacedByNewRetailersTable4(date);
            salesBeatDb.deleteAllFromDistributorOrderTable4(date);
            salesBeatDb.deleteAllDataFromNewDistributorTable4(date);
            salesBeatDb.deleteSpecificDataFromOrderEntryListTable4(date);
            salesBeatDb.deleteAllDataFromSkuEntryListTable4(date);
            salesBeatDb.deleteLeaderboardDetail2(date);
            salesBeatDb.deleteEmpKraDetails2(date);
            salesBeatDb.deleteOtherActivity2(date);
            salesBeatDb.deleteAllVisitedBeat(date);

        } catch (Exception e) {
            Log.e(TAG, "==" + e.getMessage());
        }
    }

    private void resetPref(String date) {

        SharedPreferences.Editor tempEditor = tempSfa.edit();
        tempEditor.clear();
        tempEditor.apply();

        SharedPreferences.Editor editor = prefSFA.edit();
        editor.remove(context.getString(R.string.date_key));
        editor.apply();

        SharedPreferences.Editor sfaEditor = prefSFA.edit();
        sfaEditor.putString(context.getString(R.string.date_key), date);
        sfaEditor.apply();

    }

    public Pair<String, String> getDateRange(int val, int year) {

        String begining, end;

        SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        {
            Calendar calendar = getCalendarForNow();
            calendar.add(Calendar.MONTH, val);
            calendar.add(Calendar.YEAR, year);
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);

            begining = sdff.format(calendar.getTime());
        }

        {
            Calendar calendar = getCalendarForNow();
            calendar.add(Calendar.MONTH, val);
            calendar.add(Calendar.YEAR, year);
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndofDay(calendar);
            end = sdff.format(calendar.getTime());
        }

        return Pair.create(begining, end);
    }

    public String changeDateFormat(String timeString) {
        String timeFormat;
        String newTimeString = timeString.substring(0, timeString.length() - 3);
        int time = Integer.parseInt(newTimeString.substring(0, newTimeString.indexOf(":")));

        if (time <= 12) {
            timeFormat = newTimeString + " AM";
        } else {
            time -= 12;
            timeFormat = String.valueOf(time) + newTimeString.substring(newTimeString.indexOf(":")) + " PM";
        }

        return timeFormat;
    }

    private class CheckConnection extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {

                InetAddress ipAddr = InetAddress.getByName("google.com");
                //You can replace it with your name
                return !ipAddr.equals("");

            } catch (Exception e) {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean flag) {
            super.onPostExecute(flag);
            checkCon.setConnected(flag);
        }
    }
    //@Umesh 10-08-2022
    public static String UTCToLocal(String dateFormatInPut, String dateFomratOutPut, String datesToConvert) {


        String dateToReturn = datesToConvert;

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatInPut);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date gmt = null;

        SimpleDateFormat sdfOutPutToSend = new SimpleDateFormat(dateFomratOutPut);
        sdfOutPutToSend.setTimeZone(TimeZone.getDefault());

        try {

            gmt = sdf.parse(datesToConvert);
            dateToReturn = sdfOutPutToSend.format(gmt);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateToReturn;
    }
}


