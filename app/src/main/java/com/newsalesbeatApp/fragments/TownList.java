package com.newsalesbeatApp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.TownListAdapter;
import com.newsalesbeatApp.interfaces.ClientInterface;
import com.newsalesbeatApp.netwotkcall.RetrofitClient;
import com.newsalesbeatApp.netwotkcall.ServerCall;
import com.newsalesbeatApp.pojo.TownItem;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import io.sentry.Sentry;

//import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.RequestParams;
//import com.loopj.android.http.TextHttpResponseHandler;
//import org.apache.http.Header;

/*
 * Created by MTC on 29-07-2017.
 */

public class TownList extends Fragment {

    String TAG = "TownList";
    ShimmerRecyclerView rvTownList;
    ArrayList<TownItem> townItems = new ArrayList<>();
    SharedPreferences myPref, tempPref;
    ImageView imgNoRecord;
    TextView tvTemp;
    LinearLayout llRooT;
    SalesBeatDb salesBeatDb;
    UtilityClass utilityClass;
    ServerCall serverCall;
    SwipeRefreshLayout swipeToReload;
    Context context;
    Button btnRefresh;
    ClientInterface apiIntentface;
    private MenuItem mSearchItem;
    private Toolbar mToolbar;
    //private Handler handler;

    //@Umesh 20220908
    private LinearLayout llTownBtn;
    private TextView tvOtherTown,tvRegularTown;
    boolean IsOther=false;

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.town_list, parent, false);
        context = view.getContext();
        setHasOptionsMenu(true);
        myPref = requireContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = requireContext().getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        llRooT = view.findViewById(R.id.llRooT);
        rvTownList = view.findViewById(R.id.rvTownList);
        imgNoRecord = view.findViewById(R.id.imgNoRecord);
        tvTemp = view.findViewById(R.id.tvTemp);
        swipeToReload = view.findViewById(R.id.swipeToReload);
        btnRefresh = view.findViewById(R.id.btnRefresh);

        //@Umesh 20220908
        llTownBtn= view.findViewById(R.id.llTownBtn);
        tvOtherTown= view.findViewById(R.id.tvOtherTown);
        tvRegularTown= view.findViewById(R.id.tvRegularTown);
        //tvOtherTown.setOnClickListener(v -> {IsOther=true; getTownList(); new LoadTownList().execute();});
        tvOtherTown.setOnClickListener(v -> {IsOther=true; getTownList();});
        //tvRegularTown.setOnClickListener(v -> {IsOther=false; getTownList();new LoadTownList().execute();});
        tvRegularTown.setOnClickListener(v -> {IsOther=false; getTownList();});


        apiIntentface = RetrofitClient.getClient().create(ClientInterface.class);

        mToolbar = view.findViewById(R.id.toolbar2);
        ImageView imgBack = mToolbar.findViewById(R.id.imgBack);
        //final TextView userImage = mToolbar.findViewById(R.id.userPic);
        TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);
        ((AppCompatActivity) requireContext()).setSupportActionBar(mToolbar);

        imgNoRecord.setVisibility(View.GONE);
        tvTemp.setVisibility(View.GONE);

        tvPageTitle.setText("TOWN/CITY LIST");

        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(getContext());
        utilityClass = new UtilityClass(getContext());
        serverCall = new ServerCall(getContext());
        //handler = new Handler();


        initializeTownList();

        imgBack.setOnClickListener(view1 -> {

            SharedPreferences.Editor editor = tempPref.edit();
            editor.remove(getString(R.string.town_name_key));
            editor.remove(getString(R.string.is_on_retailer_page));
            editor.remove(getString(R.string.beat_id_key));
            editor.remove(getString(R.string.beat_name_key));
            editor.remove(getString(R.string.dis_id_key));
            editor.remove(getString(R.string.dis_name_key));
            editor.remove("dash");
            editor.remove(getString(R.string.dis_id_key_noti));
            editor.apply();

            requireActivity().finish();
            //requireActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        });

        swipeToReload.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeTownList();
                swipeToReload.setRefreshing(false);
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeTownList();
            }
        });

        return view;
    }

    private void initializeTownList() {
        new PingServer(internet -> {
            /* do something with boolean response */
            if (!internet) {
             /*   Toast.makeText(getActivity(), "No internet. You are offline", Toast.LENGTH_SHORT).show();
                imgNoRecord.setVisibility(View.GONE);
                tvTemp.setVisibility(View.VISIBLE);
                tvTemp.setText("No internet.You are offline.Connect to network and pull to refresh");*/
                rvTownList.setVisibility(View.GONE);
                btnRefresh.setVisibility(View.VISIBLE);

            } else {
                btnRefresh.setVisibility(View.GONE);
                getTownList(); //@Umesh
               // new LoadTownList().execute();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action){
            View menuItemView = requireActivity().findViewById(R.id.action);
            showDialog(menuItemView);
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
//        switch (item.getItemId()) {
//            case R.id.action:
//
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.main, menu);
        mSearchItem = menu.findItem(R.id.m_search);

        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    searchTerm(newText);
                }
                return false;
            }
        });


        // Get the search close button image view
        ImageView closeButton = searchViewAndroidActionBar.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchTerm("");
                //Find EditText view
                EditText et = searchViewAndroidActionBar.findViewById(R.id.search_src_text);
                //Clear the text from EditText view
                et.setText("");
                //Clear query
                searchViewAndroidActionBar.setQuery("", false);
                //Collapse the action view
                searchViewAndroidActionBar.onActionViewCollapsed();
            }
        });


        MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Called when SearchView is collapsing
                if (mSearchItem.isActionViewExpanded()) {
                    animateSearchToolbar(1, false, false);
                }

                searchTerm("");
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Called when SearchView is expanding
                animateSearchToolbar(1, true, true);
                return true;
            }
        });

    }

    public void showDialog(View menuItem) {

        final PopupMenu popupMenu = new PopupMenu(requireContext(), menuItem);

        requireActivity().getMenuInflater().inflate(R.menu.home_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {

            popupMenu.dismiss();

            if (item.getItemId() == R.id.home) {

                requireActivity().finish();
                //requireActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
            return false;
        });

        popupMenu.show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getView() != null)
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void searchTerm(String searchText) {
        townItems.clear();
        {
            Cursor cursor = null;
            try {
                cursor = salesBeatDb.searchIntoTownListTable(searchText);
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                    //if town entry is already in database
                    do {
                        TownItem item = new TownItem();
                        item.setTownName(cursor.getString(cursor.getColumnIndex("town_name")));
                        townItems.add(item);
                    } while (cursor.moveToNext());

                    Collections.sort(townItems, new Comparator<TownItem>() {
                        @Override
                        public int compare(TownItem o1, TownItem o2) {
                            return o1.getTownName().compareTo(o2.getTownName());
                        }
                    });
                }
            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            TownListAdapter adapter = new TownListAdapter(getContext(), townItems);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            rvTownList.setLayoutManager(layoutManager);
            //rvTownList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
            rvTownList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        mToolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));

        if (show) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                int width = mToolbar.getWidth() - (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) - ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar,
                        isRtl(getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2,
                        0.0f, (float) width);
                createCircularReveal.setDuration(250);
                createCircularReveal.start();

            } else {

                TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f,
                        (float) (-mToolbar.getHeight()), 0.0f);
                translateAnimation.setDuration(220);
                mToolbar.clearAnimation();
                mToolbar.startAnimation(translateAnimation);
            }

        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int width = mToolbar.getWidth() - (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) - ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar,
                        isRtl(getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2,
                        (float) width, 0.0f);
                createCircularReveal.setDuration(250);
                createCircularReveal.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mToolbar.setBackgroundColor(getThemeColor(getContext(), android.R.attr.colorPrimary));
                    }
                });
                createCircularReveal.start();

            } else {

                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (-mToolbar.getHeight()));
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setDuration(220);
                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mToolbar.setBackgroundColor(getThemeColor(getContext(), android.R.attr.colorPrimary));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                mToolbar.startAnimation(animationSet);

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    //@Umesh 20220908
    public void getTownList() {
        String LoadMsg="";
        if(IsOther)
        {
            LoadMsg="Load Other town list...";
            tvRegularTown.setLinksClickable(false);
            tvRegularTown.setBackgroundColor(Color.parseColor("#1F000000"));

            tvOtherTown.setLinksClickable(true);
            tvOtherTown.setBackgroundColor(Color.parseColor("#F0544D"));
        }
        else
        {
            LoadMsg="Load Regular town list...";
            tvRegularTown.setLinksClickable(true);
            tvRegularTown.setBackgroundColor(Color.parseColor("#F0544D"));

            tvOtherTown.setLinksClickable(false);
            tvOtherTown.setBackgroundColor(Color.parseColor("#1F000000"));
        }

        SharedPreferences.Editor editor = tempPref.edit();
        if(IsOther)editor.putString("IsOther","true");
        else editor.putString("IsOther","false");
        editor.apply();

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(LoadMsg);
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObjectRequest townListRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.GET_TOWN_LIST+"?IsOther="+IsOther, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject JsonResponse) {
                Cursor cursor = salesBeatDb.getAllRecordFromTownListTable();
                if (cursor != null && cursor.getCount() > 0)
                    salesBeatDb.deleteAllFromTownList();

                try {
                    if(JsonResponse.getInt("status")==1)
                    {
                        townItems.clear();
                        JSONArray towns = JsonResponse.getJSONArray("data");
                        for (int i = 0; i < towns.length(); i++)
                        {
                            String town = towns.get(i).toString();
                            salesBeatDb.insertTownList(town);

                            TownItem item = new TownItem();
                            item.setTownName(town);
                            townItems.add(item);
                        }
                        if (towns.length() == 0)
                        {
                            SharedPreferences.Editor editor = tempPref.edit();
                            editor.putString(getString(R.string.townErrorKey), "No data: In Towns");
                            editor.apply();
                        }
                        if (townItems.size() > 0)
                        {

                            Collections.sort(townItems, new Comparator<TownItem>() {
                                @Override
                                public int compare(TownItem o1, TownItem o2) {
                                    return o1.getTownName().compareTo(o2.getTownName());
                                }
                            });

                            TownListAdapter adapter = new TownListAdapter(getContext(), townItems);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                            rvTownList.setLayoutManager(layoutManager);
                            int resId = R.anim.layout_animation_fall_down;
                            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(context, resId);
                            rvTownList.setLayoutAnimation(animation);
                            rvTownList.setAdapter(adapter);
                            rvTownList.getActualAdapter().notifyDataSetChanged();

                            imgNoRecord.setVisibility(View.GONE);
                            tvTemp.setVisibility(View.GONE);
                            rvTownList.setVisibility(View.VISIBLE);

                        } else {

                            imgNoRecord.setVisibility(View.VISIBLE);
                            tvTemp.setVisibility(View.VISIBLE);
                            rvTownList.setVisibility(View.GONE);
                        }
                    }
                    progressDialog.hide();

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                SharedPreferences.Editor editor = tempPref.edit();
                editor.putString(getString(R.string.townErrorKey),
                        error.networkResponse.statusCode + ": " + error.getMessage());
                editor.apply();

                serverCall.handleError2(error.networkResponse.statusCode,
                        TAG, error.getMessage(), "getTowns");

                Sentry.captureMessage(error.getMessage()); //@Umesh
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("authorization", myPref.getString("token",""));
                return headers;
            }
        };

        townListRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getContext()).add(townListRequest);
    }

    //Not Working From 20220908
    private class LoadTownList extends AsyncTask<Void, Void, ArrayList<TownItem>> {
        @SuppressLint("Range")
        @Override
        protected ArrayList<TownItem> doInBackground(Void... voids) {

            Cursor cursor = null;
            try {
                cursor = salesBeatDb.getAllRecordFromTownListTable();
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                    do {
                        TownItem item = new TownItem();
                        item.setTownName(cursor.getString(cursor.getColumnIndex("town_name")));
                        townItems.add(item);
                    } while (cursor.moveToNext());

                    Collections.sort(townItems, new Comparator<TownItem>() {
                        @Override
                        public int compare(TownItem o1, TownItem o2) {
                            return o1.getTownName().compareTo(o2.getTownName());
                        }
                    });

                }

            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (cursor != null)
                    cursor.close();
            }


            return townItems;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            townItems.clear();
        }

        @Override
        protected void onPostExecute(ArrayList<TownItem> townItems) {
            super.onPostExecute(townItems);

            if (townItems.size() > 0) {

                TownListAdapter adapter = new TownListAdapter(getContext(), townItems);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                rvTownList.setLayoutManager(layoutManager);
                int resId = R.anim.layout_animation_fall_down;
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(context, resId);
                rvTownList.setLayoutAnimation(animation);
                rvTownList.setAdapter(adapter);
                rvTownList.getActualAdapter().notifyDataSetChanged();

                imgNoRecord.setVisibility(View.GONE);
                tvTemp.setVisibility(View.GONE);
                rvTownList.setVisibility(View.VISIBLE);

            } else {

                imgNoRecord.setVisibility(View.VISIBLE);
                tvTemp.setVisibility(View.VISIBLE);
                rvTownList.setVisibility(View.GONE);
            }
        }
    }
}
