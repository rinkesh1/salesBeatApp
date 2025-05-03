package com.newsalesbeatApp.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.RetailerActivity;
import com.newsalesbeatApp.customview.Animation;
import com.newsalesbeatApp.customview.AxisRenderer;
import com.newsalesbeatApp.customview.LineChartView;
import com.newsalesbeatApp.customview.LineSet;
import com.newsalesbeatApp.customview.Tools;
import com.newsalesbeatApp.customview.Tooltip;
import com.newsalesbeatApp.pojo.BeatItem;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/*
 * Created by MTC on 25-07-2017.
 */

public class BeatListAdapter extends RecyclerView.Adapter<BeatListAdapter.ViewHolder>
        implements Filterable {

    private static FirebaseAnalytics firebaseAnalytics;
    UtilityClass utilityClass;
    private ArrayList<BeatItem> beatNameList;
    private ArrayList<BeatItem> beatNameList2;
    private SharedPreferences tempPref, myPref;
    private Context context;
    private GPSLocation locationProvider;
    private SalesBeatDb salesBeatDb;
    private int totalRetailer;

    public BeatListAdapter(Context ctx, ArrayList<BeatItem> beatNameList) {

        this.beatNameList = beatNameList;
        this.beatNameList2 = beatNameList;
        this.context = ctx;
        tempPref = ctx.getSharedPreferences(ctx.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        myPref = ctx.getSharedPreferences(ctx.getString(R.string.pref_name), Context.MODE_PRIVATE);
        locationProvider = new GPSLocation(ctx);
        //salesBeatDb = new SalesBeatDb(context);
        salesBeatDb = SalesBeatDb.getHelper(ctx);
        utilityClass = new UtilityClass(context);
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beat_list_row, parent, false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.tvBeatName.setText(beatNameList.get(position).getBeatName());
        String letter = String.valueOf(beatNameList.get(position).getBeatName().charAt(0));
        holder.beatIcon.setText(letter);
        Random rnd = new Random();
        final int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.beatIcon.setTextColor(color);

        Cursor cursor = salesBeatDb.getAllDataFromRetailerListTable(beatNameList.get(position).getBeatId());
        if (cursor != null) {
            totalRetailer = cursor.getCount();
            if (cursor.getCount() > 0 && cursor.moveToFirst())
                holder.tvRetailerCount.setText(String.valueOf(totalRetailer) + " Retailers");
            if (totalRetailer == 0) {
                holder.tvRecap.setEnabled(false);
                holder.tvRecap.setBackgroundColor(Color.GRAY);
            }
        }

        try {

            PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
            Drawable drawable = context.getDrawable(R.drawable.gray_rectangle);
            int color2 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            drawable.setColorFilter(color2, mMode);

            Drawable drawable1 = context.getDrawable(R.drawable.ic_keyboard_arrow_right_black_48dp);
            drawable1.setColorFilter(Color.BLACK, mMode);

            holder.beatIcon.setBackground(drawable);
            holder.nextIcon.setBackground(drawable1);

        } catch (NoSuchMethodError error) {
            error.printStackTrace();
        }


        holder.rlBeatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = tempPref.edit();
                editor.putString(context.getString(R.string.beat_id_key), beatNameList.get(position).getBeatId());
                editor.putString(context.getString(R.string.beat_name_key), beatNameList.get(position).getBeatName());
                editor.apply();

                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        Toast.makeText(context, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {
                        new MyTask(holder, beatNameList.get(position).getBeatId(), tempPref.getString(context.getString(R.string.dis_id_key), ""))
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                });


            }
        });

        holder.tvRecap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Added soon", Toast.LENGTH_SHORT).show();

                Bundle params = new Bundle();
                params.putString("Action", "Recap Visit");
                params.putString("UserId", "" + myPref.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("BeatList", params);

                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        Toast.makeText(context, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {
                        if (utilityClass.isInternetConnected()) {
                            holder.tvRecap.setText("Loading...");
                            holder.tvRecap.setClickable(false);
                            showRecap(holder.tvRecap, beatNameList.get(position).getBeatId());
                        } else
                            Toast.makeText(context, "You are not connected to internet", Toast.LENGTH_SHORT).show();
                    }

                });


            }
        });

    }

    private void showRecap(final TextView tvRecap, String beatId) {

        ///api/v3/getRecap/{bid}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_RECAP + "?bid=" + beatId,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("onResponse", "RECAP===" + response);

                try {
                    //@Umesh
                    if(response.getInt("status")==1)
                    {
                        JSONObject responsedata = response.getJSONObject("data");
                        JSONObject data = responsedata.getJSONObject("beatdata");

                        String beatName = data.getString("beatName");
                        String visitedBy = data.getString("visitedBy");
                        String visitedAt = data.getString("visitedAt");
                        String sc = data.getString("sc");
                        String tc = data.getString("tc");
                        String pc = data.getString("pc");
                        String prod = data.getString("prod");

                        JSONArray visits = responsedata.getJSONArray("visits");
                        ArrayList<String> dateList = new ArrayList<>();
                        //dateList.add("");
                        ArrayList<String> pcList = new ArrayList<>();
                        //pcList.add("0");
                        ArrayList<String> tcList = new ArrayList<>();
                        //tcList.add("0");
                        for (int i = 0; i < visits.length(); i++)
                        {
                            JSONObject object = (JSONObject) visits.get(i);
                            String stDate = object.getString("date");
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            //Date/time pattern of desired output date
                            DateFormat outputformat = new SimpleDateFormat("dd MMM");
                            Date date = null;
                            String output = null;
//                            try {
                                //Conversion of input String to date
                                //date = df.parse(stDate);
                                //old date format to new date format
                                //output = outputformat.format(date);
                                output=stDate; //Umesh
                                dateList.add(output);

//                            } catch (ParseException pe) {
//                                pe.printStackTrace();
//                            }
                            tcList.add(object.getString("tc"));
                            pcList.add(object.getString("pc"));
                        }
                        tvRecap.setText("Recap");
                        tvRecap.setClickable(true);
                        showRecapDialog(beatName, visitedAt, visitedBy, sc, tc, pc, prod,
                                dateList, tcList, pcList);
                    }
                    else
                    {
                        tvRecap.setText("Recap");
                        tvRecap.setClickable(true);
                        Toast.makeText(context, "" + response.get("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    tvRecap.setText("Recap");
                    tvRecap.setClickable(true);
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                tvRecap.setText("Recap");
                tvRecap.setClickable(true);
            }
        }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    private void showRecapDialog(String beatName, String visitedAt, String visitedBy, String sc,
                                 String tc, String pc, String prod, ArrayList<String> dateList,
                                 ArrayList<String> tcList, ArrayList<String> pcList) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.recap_dialog);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvSc = dialog.findViewById(R.id.tvScRecap);
        TextView tvTc = dialog.findViewById(R.id.tvTcRecap);
        TextView tvPc = dialog.findViewById(R.id.tvPcRecap);
        TextView tvProd = dialog.findViewById(R.id.tvProductivityRecap);
        TextView tvVisitedBy = dialog.findViewById(R.id.tvVisitedByRecap);
        TextView tvBeatNameRecap = dialog.findViewById(R.id.tvBeatNameRecap);
        TextView tvTextLastVisitsCounts = dialog.findViewById(R.id.tvTextLastVisitsCounts);
        LinearLayout llOkRecap = dialog.findViewById(R.id.llOkRecap);
        final LineChartView lcRecap = dialog.findViewById(R.id.lcRecap);

        tvSc.setText(sc);
        tvTc.setText(tc);
        tvPc.setText(pc);
        tvProd.setText(prod + "%(PC/SC)");
        tvBeatNameRecap.setText(beatName);
        tvTextLastVisitsCounts.setText("\u27f5 Last " + dateList.size() + "Visits \u27f6");

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("dd MMM");
        Date date = null;
        String output = null;
        try {
            //Conversion of input String to date
            date = df.parse(visitedAt);
            //old date format to new date format
            output = outputformat.format(date);
            tvVisitedBy.setText(visitedBy + "(" + output + ")");

        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        Collections.reverse(dateList);
        String[] mLabels = new String[dateList.size()];
        for (int i = 0; i < dateList.size(); i++) {
            mLabels[i] = dateList.get(i);
        }

        Collections.reverse(pcList);
        final float[] mValues = new float[pcList.size()];

        for (int i = 0; i < pcList.size(); i++) {
            float pcF = Float.valueOf(pcList.get(i));
            float scF = Float.valueOf(sc);
            float per = ((pcF * 100) / scF);
            mValues[i] = Float.parseFloat(new DecimalFormat("##.##").format(per));
        }

        final Tooltip mTip;
        // Tooltip
//        mTip = new Tooltip(context);
//
//        mTip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
//        mTip.setDimensions((int) Tools.fromDpToPx(58), (int) Tools.fromDpToPx(25));
//
//        mTip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
//                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
//                PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)).setDuration(200);
//
//        mTip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
//                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f),
//                PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)).setDuration(200);
//
//        mTip.setPivotX(Tools.fromDpToPx(65) / 2);
//        mTip.setPivotY(Tools.fromDpToPx(25));

        // Data
        LineSet dataset = new LineSet(mLabels, mValues);
        dataset.setColor(Color.parseColor("#2d374c"))
                // .setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#2d374c"))
                .setThickness(4)
                .setDashed(new float[]{10f, 10F});
//                .beginAt(1);
        lcRecap.addData(dataset);

        dataset = new LineSet(mLabels, mValues);
        dataset.setColor(Color.parseColor("#2d374c"))
                //.setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#2d374c"))
                .setThickness(4);
//                .endAt(6);
        lcRecap.addData(dataset);

        Paint thresPaint = new Paint();
        thresPaint.setColor(Color.parseColor("#5aac82"));
        thresPaint.setStyle(Paint.Style.STROKE);
        thresPaint.setAntiAlias(true);
        thresPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        int step = 100 / 4;

        lcRecap.setAxisBorderValues(0, 100)
                .setStep(step)
                .setGrid(10, 10, thresPaint)
                .setYLabels(AxisRenderer.LabelPosition.OUTSIDE)
                //.setTooltips(mTip)
                .show(new Animation().setInterpolator(new BounceInterpolator())
                        .fromAlpha(0));

        llOkRecap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return beatNameList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    beatNameList = beatNameList2;
                } else {
                    ArrayList<BeatItem> filteredList = new ArrayList<>();
                    for (BeatItem row : beatNameList2) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getBeatName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    beatNameList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = beatNameList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                beatNameList = (ArrayList<BeatItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBeatName, tvRetailerCount, tvRecap;
        RelativeLayout rlBeatLayout;
        ImageView nextIcon;
        TextView beatIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            //Layouts Linaer,Relative etc
            rlBeatLayout = itemView.findViewById(R.id.rlBeatLayout);
            //TextView
            tvBeatName = itemView.findViewById(R.id.tvBeatName);
            tvRetailerCount = itemView.findViewById(R.id.tvRetailerCount);
            tvRecap = itemView.findViewById(R.id.tvRecap);
            beatIcon = itemView.findViewById(R.id.beatIcon);
            //ImageView
            nextIcon = itemView.findViewById(R.id.nextIcon);
        }
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        String bid, did;
        ViewHolder holder;

        private MyTask(ViewHolder holder, String beatId, String distrebutorId) {
            this.bid = beatId;
            this.did = distrebutorId;
            this.holder = holder;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //syncBeatVisited();
            return null;
        }

        @Override
        protected void onPreExecute() {

            Cursor beatVisitedCursor = null;
            try {

                beatVisitedCursor = salesBeatDb.getBeatList(bid, did);
                if (beatVisitedCursor == null || beatVisitedCursor.getCount() == 0) {

                    //beat not visited
                    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String checkInTimeStamp = dateFormat1.format(Calendar.getInstance().getTime());

                    salesBeatDb.insertBeatVisited(bid, did, checkInTimeStamp,
                            locationProvider.getLatitudeStr(), locationProvider.getLongitudeStr());

                } /*else {


                }*/

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (beatVisitedCursor != null)
                    beatVisitedCursor.close();
            }


        }

        @Override
        protected void onPostExecute(Void aVoid) {

            try {
//                String[] ret = holder.tvRetailerCount.getText().toString().split(" ");
//                int count = Integer.parseInt(ret[0]);
//
//                if (count > 0){

                Intent intent = new Intent(context, RetailerActivity.class);
                SharedPreferences.Editor editor = tempPref.edit();
                editor.putString("dash", "2");
                editor.apply();
                context.startActivity(intent);
                ((Activity) context).finish();

//                } else {
//                    Toast.makeText(context, "There is no retailer in this beat", Toast.LENGTH_SHORT).show();
//                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
