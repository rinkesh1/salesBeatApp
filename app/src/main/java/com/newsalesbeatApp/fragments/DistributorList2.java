package com.newsalesbeatApp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.DistributorListAdapter2;
import com.newsalesbeatApp.pojo.DistrebutorItem;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.StatusLine;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;

public class DistributorList2 extends Fragment {

    ShimmerRecyclerView rcvDistrebutorList;
    ArrayList<DistrebutorItem> distrebutorItems = new ArrayList<>();
    Button btnChangeTown;
    ImageView imgNoRecord;
    TextView tvTemp;
    SharedPreferences myPref, tempPref;
    SalesBeatDb salesBeatDb;
    String townName;
    UtilityClass utilityClass;
    RelativeLayout rlDisList;
    DistributorListAdapter2 adapter;
    private MenuItem mSearchItem;
    private Toolbar mToolbar;

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.distrebutor_list, container, false);
        setHasOptionsMenu(true);
        myPref = requireContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = requireContext().getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        rcvDistrebutorList = view.findViewById(R.id.rcDistrebutorList);
        btnChangeTown = view.findViewById(R.id.btnChangeTown);
        imgNoRecord = view.findViewById(R.id.imgNoRecord);
        tvTemp = view.findViewById(R.id.tvTemp);
        rlDisList = view.findViewById(R.id.rlDisList);

        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(requireContext());
        utilityClass = new UtilityClass(requireContext());

        mToolbar = view.findViewById(R.id.toolbar2);
        ImageView imgBack = mToolbar.findViewById(R.id.imgBack);
        final TextView userImage = mToolbar.findViewById(R.id.userPic);
        TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);

        imgNoRecord.setVisibility(View.GONE);
        tvTemp.setVisibility(View.GONE);
        btnChangeTown.setVisibility(View.GONE);
        rlDisList.setVisibility(View.VISIBLE);

        townName = tempPref.getString(getString(R.string.town_name_key), "");
        tvPageTitle.setText(townName);
        userImage.setText(townName);

        initializeDistributorList();

//        try {
//
//            Bundle bundle1 = getArguments();
//            if (bundle1 != null) {
//
//                String from = bundle1.getString("from");
//                if (from != null && from.equalsIgnoreCase("town") && utilityClass.isInternetConnected()) {
//                    startServiceToDownloadList();
//                }
//
//            } else {
//
//                initializeDistributorList();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FragmentManager fragManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragTransaction = fragManager.beginTransaction();
                //fragTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
                Fragment fragment = new TownList2();
                fragTransaction.replace(R.id.frmClosing, fragment);
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
        });

        btnChangeTown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragTransaction = fragManager.beginTransaction();
                //fragTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
                Fragment fragment = new TownList2();
                fragTransaction.replace(R.id.frmClosing, fragment);
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
        });

        return view;
    }

    private void initializeDistributorList() {

        //String tN = getArguments().getString("tN");

        new DownloadDistributors(townName).execute();

//        try {
//
//            Bundle bundle = getArguments();
//            distrebutorItems = (ArrayList<DistrebutorItem>) bundle.getSerializable("dis_list");
//
//
//            if (distrebutorItems != null && distrebutorItems.size() > 0 ) {
//
//                Collections.sort(distrebutorItems, new Comparator<DistrebutorItem>() {
//                    @Override
//                    public int compare(DistrebutorItem o1, DistrebutorItem o2) {
//                        return o1.getDistrebutorName().compareTo(o2.getDistrebutorName());
//                    }
//                });
//
//
//                adapter = new DistributorListAdapter(getContext(), distrebutorItems,rcvDistrebutorList);
//                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//                rcvDistrebutorList.setLayoutManager(layoutManager);
//                int resId = R.anim.layout_animation_fall_down;
//                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
//                rcvDistrebutorList.setLayoutAnimation(animation);
//                rcvDistrebutorList.setAdapter(adapter);
//
//            } else {
//
//                imgNoRecord.setVisibility(View.VISIBLE);
//                tvTemp.setVisibility(View.VISIBLE);
//                btnChangeTown.setVisibility(View.VISIBLE);
//                rcvDistrebutorList.setVisibility(View.GONE);
//                Toast.makeText(getContext(), getString(R.string.norecordfound), Toast.LENGTH_SHORT).show();
//
//            }
//
//
//        } catch (Exception e) {
//            e.getMessage();
//        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action) {
            View menuItemView = getActivity().findViewById(R.id.action);
            showDialog(menuItemView);
            return true;
        }

        return super.onOptionsItemSelected(item);


//        switch (item.getItemId()) {
//            case R.id.action:
//                View menuItemView = getActivity().findViewById(R.id.action);
//                showDialog(menuItemView);
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//        }

//        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.e("OptionsMenu", "===Called");
        inflater.inflate(R.menu.main, menu);
        mSearchItem = menu.findItem(R.id.m_search);

        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(mSearchItem);

        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });


        // Get the search close button image view
        ImageView closeButton = (ImageView) searchViewAndroidActionBar.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                adapter.getFilter().filter("");
                //Find EditText view
                EditText et = (EditText) searchViewAndroidActionBar.findViewById(R.id.search_src_text);

                //Clear the text from EditText view
                et.setText("");

                //Clear query
                searchViewAndroidActionBar.setQuery("", false);
                //Collapse the action view
                searchViewAndroidActionBar.onActionViewCollapsed();
                //Collapse the search widget
                //searchViewAndroidActionBar.collapseActionView();
            }
        });


        MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Called when SearchView is collapsing
                if (mSearchItem.isActionViewExpanded()) {
                    animateSearchToolbar(1, false, false);
                }
                //searchTerm("");
                adapter.getFilter().filter("");
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

//    private void searchTerm(String searchString) {
//
//        DistributorListAdapter adapter = new DistributorListAdapter(getContext(), distrebutorItems,rcvDistrebutorList);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//        rcvDistrebutorList.setLayoutManager(layoutManager);
//        //rcvDistrebutorList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
//        rcvDistrebutorList.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//    }

    public void showDialog(View menuItem) {


        PopupMenu popupMenu = new PopupMenu(getContext(), menuItem);

        getActivity().getMenuInflater().inflate(R.menu.change_town, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.changeTown) {

                    FragmentManager fragManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragTransaction = fragManager.beginTransaction();
                    //fragTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
                    Fragment fragment = new TownList();
                    fragTransaction.replace(R.id.flContainer, fragment);
                    fragTransaction.addToBackStack(null);
                    fragTransaction.commit();

                }

                return false;
            }
        });

        popupMenu.show();
    }

    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));

        if (show) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int width = mToolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar,
                        isRtl(getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, 0.0f, (float) width);
                createCircularReveal.setDuration(250);
                createCircularReveal.start();
            } else {
                TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, (float) (-mToolbar.getHeight()), 0.0f);
                translateAnimation.setDuration(220);
                mToolbar.clearAnimation();
                mToolbar.startAnimation(translateAnimation);
            }

        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int width = mToolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar,
                        isRtl(getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, (float) width, 0.0f);
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

    private class DownloadDistributors extends AsyncTask<Void, String, String> {

        String townName;
        //JSONArray updatedAtArr = new JSONArray();
        ArrayList<DistrebutorItem> disList = new ArrayList<>();

        public DownloadDistributors(String townName) {

            this.townName = townName;
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {


            //Log.e("DistributorList", "Distributor Json-->" + updatedAtArr.toString());

            // Http Client
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
//                            //distrebutorItem.setDistrebutor_address(obj.getString("address"));
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
            return "";
        }


        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);

            try {
                townName = URLEncoder.encode(townName, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
                //return "error";
            }


            JsonObjectRequest getDistListReq = new JsonObjectRequest(Request.Method.GET,
                    SbAppConstants.API_GET_DISTRIBUTORS_2 + "?town=" + townName,null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject res) {


                            try {

                                Log.e("Distributor lis sec", " Distributor list: " + res);
                                //@Umesh 02-Feb-2022
                                if(res.getInt("status")==1)
                                {
                                    JSONArray distributors = res.getJSONArray("data");
                                    for (int i = 0; i < distributors.length(); i++) {


                                        JSONObject obj = (JSONObject) distributors.get(i);

                                        DistrebutorItem distrebutorItem = new DistrebutorItem();

                                        // JSONObject zoneObj = obj.getJSONObject("zone");
                                        distrebutorItem.setDistrebutorId(obj.getString("did"));
                                        distrebutorItem.setDistrebutorName(obj.getString("name"));
                                        //distrebutorItem.setDistrebutor_address(obj.getString("address"));


                                        disList.add(distrebutorItem);

                                    }

                                    if(res.getInt("status")==1)
                                    {

                                        try {


                                            distrebutorItems = disList;


                                            if (distrebutorItems != null && distrebutorItems.size() > 0) {

                                                Collections.sort(distrebutorItems, new Comparator<DistrebutorItem>() {
                                                    @Override
                                                    public int compare(DistrebutorItem o1, DistrebutorItem o2) {
                                                        return o1.getDistrebutorName().compareTo(o2.getDistrebutorName());
                                                    }
                                                });


                                                adapter = new DistributorListAdapter2(getContext(), distrebutorItems, townName);
                                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                                                rcvDistrebutorList.setLayoutManager(layoutManager);
                                                int resId = R.anim.layout_animation_fall_down;
                                                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
                                                rcvDistrebutorList.setLayoutAnimation(animation);
                                                rcvDistrebutorList.setAdapter(adapter);

                                            } else {

                                                imgNoRecord.setVisibility(View.VISIBLE);
                                                tvTemp.setVisibility(View.VISIBLE);
                                                btnChangeTown.setVisibility(View.VISIBLE);
                                                rcvDistrebutorList.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), getString(R.string.norecordfound), Toast.LENGTH_SHORT).show();

                                            }


                                        } catch (Exception e) {
                                            e.getMessage();
                                        }


                                    } else {

//                                    tvLoadingMsg.setText(tempPref.getString(getString(R.string.distErrorKey), ""));
//                                    tvLoadingMsg.setVisibility(View.VISIBLE);
//                                    llNewStartWorkList.setVisibility(View.GONE);

                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    imgNoRecord.setVisibility(View.VISIBLE);
                    tvTemp.setVisibility(View.VISIBLE);
                    btnChangeTown.setVisibility(View.VISIBLE);
                    rcvDistrebutorList.setVisibility(View.GONE);
                    if (error != null)
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("authorization", myPref.getString("token", ""));
                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            Volley.newRequestQueue(requireContext()).add(getDistListReq);


//            JsonObjectRequest distributorListReq = new JsonObjectRequest(Request.Method.GET,
//                    SbAppConstants.API_GET_DISTRIBUTORS_2 + "town=" + townName, null, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//
//                    try {
//
//
//                        String status = response.getString("status");
//
//                        if (status.equalsIgnoreCase("success")) {
//
//                            JSONArray distributors = response.getJSONArray("distributors");
//                            for (int i = 0; i < distributors.length(); i++) {
//
//
//                                JSONObject obj = (JSONObject) distributors.get(i);
//
//                                DistrebutorItem distrebutorItem = new DistrebutorItem();
//
//                                // JSONObject zoneObj = obj.getJSONObject("zone");
//                                distrebutorItem.setDistrebutorId(obj.getString("did"));
//                                distrebutorItem.setDistrebutorName(obj.getString("name"));
//                                //distrebutorItem.setDistrebutor_address(obj.getString("address"));
//
//
//                                disList.add(distrebutorItem);
//
//                            }
//
//
//                        }
//
//                        if (status.contains("success")) {
//
//                            try {
//
//
//                                distrebutorItems = disList;
//
//
//                                if (distrebutorItems != null && distrebutorItems.size() > 0) {
//
//                                    Collections.sort(distrebutorItems, new Comparator<DistrebutorItem>() {
//                                        @Override
//                                        public int compare(DistrebutorItem o1, DistrebutorItem o2) {
//                                            return o1.getDistrebutorName().compareTo(o2.getDistrebutorName());
//                                        }
//                                    });
//
//
//                                    adapter = new DistributorListAdapter2(getContext(), distrebutorItems, townName);
//                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//                                    rcvDistrebutorList.setLayoutManager(layoutManager);
//                                    int resId = R.anim.layout_animation_fall_down;
//                                    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
//                                    rcvDistrebutorList.setLayoutAnimation(animation);
//                                    rcvDistrebutorList.setAdapter(adapter);
//
//                                } else {
//
//                                    imgNoRecord.setVisibility(View.VISIBLE);
//                                    tvTemp.setVisibility(View.VISIBLE);
//                                    btnChangeTown.setVisibility(View.VISIBLE);
//                                    rcvDistrebutorList.setVisibility(View.GONE);
//                                    Toast.makeText(getContext(), getString(R.string.norecordfound), Toast.LENGTH_SHORT).show();
//
//                                }
//
//
//                            } catch (Exception e) {
//                                e.getMessage();
//                            }
//
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    if (!utilityClass.isInternetConnected())
//                        Log.e("DistributorList2", "Network Error");
//
//                    error.printStackTrace();
//                    imgNoRecord.setVisibility(View.VISIBLE);
//                    tvTemp.setVisibility(View.VISIBLE);
//                    btnChangeTown.setVisibility(View.VISIBLE);
//                    rcvDistrebutorList.setVisibility(View.GONE);
//                    if (error != null)
//                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    else
//                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
//
//                }
//            });
//
//            Volley.newRequestQueue(requireContext()).add(distributorListReq);

//            if (status.contains("success")) {
//
//                try {
//
//
//                    distrebutorItems = disList;
//
//
//                    if (distrebutorItems != null && distrebutorItems.size() > 0 ) {
//
//                        Collections.sort(distrebutorItems, new Comparator<DistrebutorItem>() {
//                            @Override
//                            public int compare(DistrebutorItem o1, DistrebutorItem o2) {
//                                return o1.getDistrebutorName().compareTo(o2.getDistrebutorName());
//                            }
//                        });
//
//
//                        adapter = new DistributorListAdapter2(getContext(), distrebutorItems,townName);
//                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//                        rcvDistrebutorList.setLayoutManager(layoutManager);
//                        int resId = R.anim.layout_animation_fall_down;
//                        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
//                        rcvDistrebutorList.setLayoutAnimation(animation);
//                        rcvDistrebutorList.setAdapter(adapter);
//
//                    } else {
//
//                        imgNoRecord.setVisibility(View.VISIBLE);
//                        tvTemp.setVisibility(View.VISIBLE);
//                        btnChangeTown.setVisibility(View.VISIBLE);
//                        rcvDistrebutorList.setVisibility(View.GONE);
//                        Toast.makeText(getContext(), getString(R.string.norecordfound), Toast.LENGTH_SHORT).show();
//
//                    }
//
//
//                } catch (Exception e) {
//                    e.getMessage();
//                }
//
//
//
//            } else {
//
//
//            }
        }
    }
}

