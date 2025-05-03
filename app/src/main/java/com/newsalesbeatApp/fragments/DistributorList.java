package com.newsalesbeatApp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.DistributorListAdapter;
import com.newsalesbeatApp.pojo.DistrebutorItem;
import com.newsalesbeatApp.services.DownloadAllListService;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
 * Created by MTC on 26-07-2017.
 */

public class DistributorList extends Fragment {

    ShimmerRecyclerView rcvDistrebutorList;
    ArrayList<DistrebutorItem> distrebutorItems = new ArrayList<>();
    Button btnChangeTown;
    ImageView imgNoRecord;
    TextView tvTemp;
    SharedPreferences myPref, tempPref;
    SalesBeatDb salesBeatDb;
    String townName;
    DataDownload dataDownload;
    UtilityClass utilityClass;
    RelativeLayout rlDisList;
    DistributorListAdapter adapter;
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
        salesBeatDb = SalesBeatDb.getHelper(getContext());
        utilityClass = new UtilityClass(getContext());

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

                SharedPreferences.Editor editor = tempPref.edit();
                editor.remove(getString(R.string.town_name_key));
                editor.remove(getString(R.string.is_on_retailer_page));
                editor.remove(getString(R.string.beat_id_key));
                editor.remove(getString(R.string.beat_name_key));
                editor.remove(getString(R.string.dis_id_key));
                editor.remove(getString(R.string.dis_name_key));
                editor.apply();

                FragmentManager fragManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragTransaction = fragManager.beginTransaction();
                //fragTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
                Fragment fragment = new TownList();
                fragTransaction.replace(R.id.flContainer, fragment);
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
                Fragment fragment = new TownList();
                fragTransaction.replace(R.id.flContainer, fragment);
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
        });

        return view;
    }

    private void startServiceToDownloadList() {
        dataDownload = new DataDownload(new Handler());
        Intent intent = new Intent(getContext(), DownloadAllListService.class);
        intent.putExtra("town", tempPref.getString(getString(R.string.town_name_key), ""));
        intent.putExtra("receiver", dataDownload);
        requireActivity().startService(intent);
    }

    public void initializeDistributorList() {

        Cursor cursor = null;


        try {

            distrebutorItems.clear();

            cursor = salesBeatDb.getAllDataFromDistributorListTable(townName);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    DistrebutorItem distrebutorItem = new DistrebutorItem();
                    String did = cursor.getString(cursor.getColumnIndex("distributor_id"));
                    distrebutorItem.setDistrebutorId(did);
                    distrebutorItem.setDistrebutorName(cursor.getString(cursor.getColumnIndex("distributor_name")));
                    distrebutorItem.setDistrebutor_phone(cursor.getString(cursor.getColumnIndex("phone")));
                    distrebutorItem.setDistrebutor_email(cursor.getString(cursor.getColumnIndex("email")));
                    distrebutorItem.setDistrebutor_type(cursor.getString(cursor.getColumnIndex("type")));
                    distrebutorItem.setDistrebutor_address(cursor.getString(cursor.getColumnIndex("address")));
                    distrebutorItem.setDistrebutor_district(cursor.getString(cursor.getColumnIndex("district")));
                    distrebutorItem.setDistrebutor_zone(cursor.getString(cursor.getColumnIndex("zone")));
                    distrebutorItem.setDistrebutor_state(cursor.getString(cursor.getColumnIndex("state")));
                    distrebutorItem.setDistrebutor_pincode(cursor.getString(cursor.getColumnIndex("pincode")));
                    distrebutorItem.setLat(cursor.getString(cursor.getColumnIndex("retailer_latitude")));
                    distrebutorItem.setLongt(cursor.getString(cursor.getColumnIndex("retailer_longtitude")));
                    distrebutorItem.setDis_gstn(cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_DISTRIBUTOR_GST)));

                    Cursor disTarAchCursor = salesBeatDb.getDistributorTarAch(did);

                    if (disTarAchCursor != null && disTarAchCursor.getCount() > 0 && disTarAchCursor.moveToFirst()) {

                        distrebutorItem.setDistributorSaleTarget(disTarAchCursor.getString(disTarAchCursor.getColumnIndex("dis_tar")));
                        distrebutorItem.setDistributorSaleAch(disTarAchCursor.getString(disTarAchCursor.getColumnIndex("dis_ach")));
                    }

                    distrebutorItems.add(distrebutorItem);
                } while (cursor.moveToNext());

                Collections.sort(distrebutorItems, new Comparator<DistrebutorItem>() {
                    @Override
                    public int compare(DistrebutorItem o1, DistrebutorItem o2) {
                        return o1.getDistrebutorName().compareTo(o2.getDistrebutorName());
                    }
                });


                adapter = new DistributorListAdapter(getContext(), distrebutorItems, rcvDistrebutorList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                rcvDistrebutorList.setLayoutManager(layoutManager);
                int resId = R.anim.layout_animation_fall_down;
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
                rcvDistrebutorList.setLayoutAnimation(animation);
                rcvDistrebutorList.setAdapter(adapter);

            } else {

                imgNoRecord.setVisibility(View.VISIBLE);
                tvTemp.setVisibility(View.VISIBLE);
                btnChangeTown.setVisibility(View.GONE); //@Umesh 20220914
                rcvDistrebutorList.setVisibility(View.GONE);
                Toast.makeText(getContext(), getString(R.string.norecordfound), Toast.LENGTH_SHORT).show();

            }


        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();
        }
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
        } else {
            return super.onOptionsItemSelected(item);
        }


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

    public void showDialog(View menuItem) {


        PopupMenu popupMenu = new PopupMenu(getContext(), menuItem);

        getActivity().getMenuInflater().inflate(R.menu.change_town, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.changeTown)
                {

//                    FragmentManager fragManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragTransaction = fragManager.beginTransaction();
//                    //fragTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
//                    Fragment fragment = new TownList();
//                    fragTransaction.replace(R.id.flContainer, fragment);
//                    fragTransaction.addToBackStack(null);
//                    fragTransaction.commit();

                    //@Umesh 20220914
                    SharedPreferences.Editor editor = tempPref.edit();
                    editor.remove(getString(R.string.town_name_key));
                    editor.remove(getString(R.string.is_on_retailer_page));
                    editor.remove(getString(R.string.beat_id_key));
                    editor.remove(getString(R.string.beat_name_key));
                    editor.remove(getString(R.string.dis_id_key));
                    editor.remove(getString(R.string.dis_name_key));
                    editor.apply();

                    FragmentManager fragManager = requireActivity().getSupportFragmentManager();
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

    private void searchTerm(String searchString) {

        distrebutorItems.clear();
        Cursor cursor = null;
        try {
            cursor = salesBeatDb.searchIntoDistributorListTable(searchString);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

                    DistrebutorItem distrebutorItem = new DistrebutorItem();
                    distrebutorItem.setDistrebutorId(cursor.getString(cursor.getColumnIndex("distributor_id")));
                    distrebutorItem.setDistrebutorName(cursor.getString(cursor.getColumnIndex("distributor_name")));
                    distrebutorItem.setDistrebutor_phone(cursor.getString(cursor.getColumnIndex("phone")));
                    distrebutorItem.setDistrebutor_email(cursor.getString(cursor.getColumnIndex("email")));
                    distrebutorItem.setDistrebutor_type(cursor.getString(cursor.getColumnIndex("type")));
                    distrebutorItem.setDistrebutor_address(cursor.getString(cursor.getColumnIndex("address")));
                    distrebutorItem.setDistrebutor_district(cursor.getString(cursor.getColumnIndex("district")));
                    distrebutorItem.setDistrebutor_zone(cursor.getString(cursor.getColumnIndex("zone")));
                    distrebutorItem.setDistrebutor_state(cursor.getString(cursor.getColumnIndex("state")));
                    distrebutorItem.setDistrebutor_pincode(cursor.getString(cursor.getColumnIndex("pincode")));
                    distrebutorItems.add(distrebutorItem);

                } while (cursor.moveToNext());

                Collections.sort(distrebutorItems, new Comparator<DistrebutorItem>() {
                    @Override
                    public int compare(DistrebutorItem o1, DistrebutorItem o2) {
                        return o1.getDistrebutorName().compareTo(o2.getDistrebutorName());
                    }
                });

            }

        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        DistributorListAdapter adapter = new DistributorListAdapter(getContext(), distrebutorItems, rcvDistrebutorList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rcvDistrebutorList.setLayoutManager(layoutManager);
        //rcvDistrebutorList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        rcvDistrebutorList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

    private class DataDownload extends ResultReceiver {

        private DataDownload(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case DownloadAllListService.DOWNLOAD_ERROR:
                    Toast.makeText(requireContext(), "Error in Downloading", Toast.LENGTH_SHORT).show();

                    FragmentManager fragManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragTransaction = fragManager.beginTransaction();
                    //fragTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
                    Fragment fragment = new TownList();
                    fragTransaction.replace(R.id.flContainer, fragment);
                    fragTransaction.addToBackStack(null);
                    fragTransaction.commit();

                    break;

                case DownloadAllListService.DOWNLOAD_SUCCESS:
                    initializeDistributorList();
                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }

    }
}
