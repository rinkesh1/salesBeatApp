package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.fragments.DistributorList2;
import com.newsalesbeatApp.pojo.TownItem;

import java.util.ArrayList;
import java.util.Random;

public class TownListAdapter2 extends RecyclerView.Adapter<TownListAdapter2.ViewHolder> {

    private String TAG = "TownListAdapter";
    private Context context;
    //private SalesBeatDb salesBeatDb;
    private ArrayList<TownItem> townList;
    private SharedPreferences tempPref, myPref;


//    private UtilityClass utilityClassObj;
//    private Dialog view;

    //private LinearLayout llDownloadStatus;
//    private ImageView imgDisMappingDownloadDone, imgDisDownloadDone, imgBeatDownloadDone, imgRetDownloadDone;
//    private TextView tvTryAgainDisMap, tvTryAgainDis, tvTryAgainBeat,
//            tvTryAgainRet, tvDownloadingDisMap, tvDownloadingDis, tvDownloadingBeats, tvDownloadingRet;
//
//    private GifImageView imgLoaderDisMap, imgLoaderDis, imgLoaderBeat, imgLoaderRet;
//
//    private ArrayList<String> dids = new ArrayList<>();
//    private ArrayList<String> didUpdatedAt = new ArrayList<>();
//    private ArrayList<String> bids = new ArrayList<>();
//    private ArrayList<String> bidUpdatedAt = new ArrayList<>();
//    private ArrayList<String> retId = new ArrayList<>();
//    private ArrayList<String> ridUpdatedAt = new ArrayList<>();
//    //private ArrayList<String> beatIdList = new ArrayList<>();
//
//    private DownloadMappingDetails downloadTask;
//    private DownloadDistributors downloadDis;
//    private DownloadBeats downloadBeats;
//    private DownloadRetailers downloadRetailers;
//    private GoToFragment goToFragment;
//
//    private boolean isCancelled = false;

    public TownListAdapter2(Context ctx, ArrayList<TownItem> tList) {

        try {

            this.context = ctx;
            this.townList = tList;
            tempPref = ctx.getSharedPreferences(ctx.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
            myPref = ctx.getSharedPreferences(ctx.getString(R.string.pref_name), Context.MODE_PRIVATE);
//            //requestQueue = Volley.newRequestQueue(context);
//
//            //salesBeatDb = new SalesBeatDb(ctx);
//            salesBeatDb = SalesBeatDb.getHelper(ctx);
//            //serverCall = new ServerCall(ctx);
//            utilityClassObj = new UtilityClass(ctx);
//
//            String townName = tempPref.getString(ctx.getString(R.string.town_name_key),"");
//            if (!townName.isEmpty()){
//
//                initDistDialog(townName);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.town_list_row, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.tvTownName.setText(townList.get(position).getTownName());
        String letter = String.valueOf(townList.get(position).getTownName().charAt(0));
        holder.icon1.setText(letter);
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.icon1.setTextColor(color);


        try {

            PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
            Drawable drawable = context.getDrawable(R.drawable.gray_rectangle);
            int color2 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            drawable.setColorFilter(color2, mMode);

            Drawable drawable1 = context.getDrawable(R.drawable.ic_keyboard_arrow_right_black_48dp);
            drawable1.setColorFilter(Color.BLACK, mMode);

            holder.icon1.setBackground(drawable);
            holder.icon2.setBackground(drawable1);

        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }

        holder.rlTownLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tName = townList.get(position).getTownName();

                SharedPreferences.Editor editor = tempPref.edit();
                editor.putString(context.getString(R.string.town_name_key), tName);
                editor.apply();

                //new DownloadDistributors(tName).execute();
                Bundle bundle = new Bundle();
                bundle.putString("from", "town");

                FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
                Fragment fragment = new DistributorList2();
                fragment.setArguments(bundle);
                ft.replace(R.id.frmClosing, fragment);
                ft.commitAllowingStateLoss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return townList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTownName;
        RelativeLayout rlTownLayout;
        TextView icon1;
        ImageView icon2;

        public ViewHolder(View itemView) {
            super(itemView);
            //Layouts Linear,Relative
            rlTownLayout = itemView.findViewById(R.id.rlTownLayout);
            //TextViews
            tvTownName = itemView.findViewById(R.id.tvTownName);
            icon1 = itemView.findViewById(R.id.townIcon);
            //ImageViews
            icon2 = itemView.findViewById(R.id.nextIcon);

        }
    }


//    private class DownloadDistributors extends AsyncTask<Void, String, String> {
//
//        String townName;
//        JSONArray updatedAtArr = new JSONArray();
//        RecyclerView rvDistributorList;
//        ArrayList<DistrebutorItem> disList = new ArrayList<>();
//
//        public DownloadDistributors(String townName) {
//
//            this.rvDistributorList = rvDistributorList;
//            this.townName = townName;
//        }
//
//        @TargetApi(Build.VERSION_CODES.M)
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(Void... voids) {
//
//
//            Log.e("DistributorList", "Distributor Json-->" + updatedAtArr.toString());
//
//
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(SbAppConstants.API_GET_DISTRIBUTORS_2+"town="+townName);
//            httpGet.addHeader("authorization", myPref.getString("token", ""));
//
//            try {
//
//                HttpResponse response = httpClient.execute(httpGet);
//
//                StatusLine statusLine = response.getStatusLine();
//                int statusCode = statusLine.getStatusCode();
//                Log.e("DistributorList", "Distributor Response Status code-->" + statusCode);
//                if (statusCode == 200) {
//
//                    HttpEntity entity = response.getEntity();
//                    InputStream content = entity.getContent();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
//                    String res = reader.readLine();
//
//                    Log.e("Dis Response is", "::" + res);
//
//                    JSONObject object = new JSONObject(res);
//
//                    String status = object.getString("status");
//
//                    if (status.equalsIgnoreCase("success")) {
//
//                        JSONArray distributors = object.getJSONArray("distributors");
//                        for (int i = 0; i < distributors.length(); i++) {
//
//
//
//                            JSONObject obj = (JSONObject) distributors.get(i);
//
//                            DistrebutorItem distrebutorItem = new DistrebutorItem();
//
//                            // JSONObject zoneObj = obj.getJSONObject("zone");
//                            distrebutorItem.setDistrebutorId(obj.getString("did"));
//                            distrebutorItem.setDistrebutorName(obj.getString("name"));
//
//
//                            disList.add(distrebutorItem);
//
//                        }
//
//
//                    }
//
//                    return status;
//
//                } else {
//                    //Log.e("Error....", "Failed to download file");
//                    return "error";
//                }
//
//            } catch (Exception e1) {
//                e1.printStackTrace();
//                return "error";
//            }
//        }
//
//
//        @Override
//        protected void onPostExecute(String status) {
//            super.onPostExecute(status);
//
//            if (status.contains("success")) {
//
//
//
//
//
//            } else {
//
//
//            }
//        }
//    }
}
