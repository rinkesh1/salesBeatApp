package com.newsalesbeatApp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.OrderBookingRetailing;
import com.newsalesbeatApp.adapters.BeatListAdapter;
import com.newsalesbeatApp.pojo.BeatItem;
import com.newsalesbeatApp.services.MyFirebaseMessagingService;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import static com.newsalesbeatApp.customview.Tools.drawableToBitmap;

/*
 * Created by MTC on 25-07-2017.
 */

public class BeatList extends Fragment {

    ArrayList<BeatItem> beatList = new ArrayList<>();
    BeatListAdapter adapter;
    private SharedPreferences tempPref;
    private ShimmerRecyclerView rcvBeatList;
    private MenuItem mSearchItem;
    private Toolbar mToolbar;
    private SalesBeatDb salesBeatDb;

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle bundle) {
        View view = inflater.inflate(R.layout.beat_list, container, false);
        setHasOptionsMenu(true);
        //prefSFA = getContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = requireContext().getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        rcvBeatList = view.findViewById(R.id.beatList);
        Button btnChangeDistributor = view.findViewById(R.id.btnChangeDistributor);
        ImageView imgNoRecord = view.findViewById(R.id.imgNoRecord);
        TextView tvTemp = view.findViewById(R.id.tvTemp);

        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(getContext());

        //to check stock exist or not
        Cursor stockAvailable = salesBeatDb.getAllDataFromSkuEntryListTable2
                (tempPref.getString(getString(R.string.dis_id_key), ""));


        mToolbar = view.findViewById(R.id.toolbar2);
        ImageView imgBack = mToolbar.findViewById(R.id.imgBack);
        final TextView userImage = mToolbar.findViewById(R.id.userPic);
        TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);
        ((AppCompatActivity) requireContext()).setSupportActionBar(mToolbar);

        tvPageTitle.setText(tempPref.getString(getString(R.string.dis_name_key), ""));
        userImage.setText(tempPref.getString(getString(R.string.dis_name_key), ""));

        imgNoRecord.setVisibility(View.GONE);
        tvTemp.setVisibility(View.GONE);
        btnChangeDistributor.setVisibility(View.GONE);

        Cursor cursor = null;
        beatList.clear();

        try {
            beatList.clear();
            //cursor = salesBeatDb.getAllDataFromBeatListTable(tempPref.getString(getString(R.string.dis_id_key), ""));

            Cursor cursor2 = salesBeatDb.getDisBeatMap(tempPref.getString(getString(R.string.dis_id_key), ""));
            if (cursor2 != null && cursor2.getCount() > 0 && cursor2.moveToFirst()) {

                do {

                    String bid = cursor2.getString(cursor2.getColumnIndex(SalesBeatDb.KEY_BID));

                    cursor = salesBeatDb.getAllDataFromBeatListTable2(bid);
                    if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                        do {

                            BeatItem beatItem = new BeatItem();
                            beatItem.setBeatId(cursor.getString(cursor.getColumnIndex("beat_id")));
                            beatItem.setBeatName(cursor.getString(cursor.getColumnIndex("beat_name")));
                            beatList.add(beatItem);

                        } while (cursor.moveToNext());

                    }

                } while (cursor2.moveToNext());

            }

            Collections.sort(beatList, new Comparator<BeatItem>() {
                @Override
                public int compare(BeatItem o1, BeatItem o2) {
                    return o1.getBeatName().compareTo(o2.getBeatName());
                }
            });

            if (beatList.size() > 0) {

                adapter = new BeatListAdapter(requireContext(), beatList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                rcvBeatList.setLayoutManager(layoutManager);
                int resId = R.anim.layout_animation_fall_down;
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
                rcvBeatList.setLayoutAnimation(animation);
                rcvBeatList.setAdapter(adapter);

            } else {

                tvTemp.setText(tempPref.getString(getString(R.string.beatErrorKey), ""));
                tvTemp.setVisibility(View.VISIBLE);
                rcvBeatList.setVisibility(View.GONE);
            }


//            cursor = salesBeatDb.getAllDataFromBeatListTable2()
//            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
//
//                do {
//
//                    BeatItem beatItem = new BeatItem();
//                    beatItem.setBeatId(cursor.getString(cursor.getColumnIndex("beat_id")));
//                    beatItem.setBeatName(cursor.getString(cursor.getColumnIndex("beat_name")));
//                    beatList.add(beatItem);
//
//                } while (cursor.moveToNext());
//
//                Collections.sort(beatList, new Comparator<BeatItem>() {
//                    @Override
//                    public int compare(BeatItem o1, BeatItem o2) {
//                        return o1.getBeatName().compareTo(o2.getBeatName());
//                    }
//                });
//
//                final BeatListAdapter adapter = new BeatListAdapter(getContext(), beatList);
//                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//                rcvBeatList.setLayoutManager(layoutManager);
//                int resId = R.anim.layout_animation_fall_down;
//                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
//                rcvBeatList.setLayoutAnimation(animation);
//                rcvBeatList.setAdapter(adapter);
//
//            } else {
//
//                imgNoRecord.setVisibility(View.VISIBLE);
//                tvTemp.setVisibility(View.VISIBLE);
//                btnChangeDistributor.setVisibility(View.VISIBLE);
//                rcvBeatList.setVisibility(View.GONE);
//                Toast.makeText(getContext(), getString(R.string.norecordfound), Toast.LENGTH_SHORT).show();
//            }

        } catch (Exception e) {
            e.getMessage();
        } finally {

            if (cursor != null)
                cursor.close();
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragTransaction = fragManager.beginTransaction();
                //fragTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
                Fragment fragment = new DistributorList();
                fragTransaction.replace(R.id.flContainer, fragment);
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
        });


        btnChangeDistributor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragTransaction = fragManager.beginTransaction();
                //fragTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
                Fragment fragment = new DistributorList();
                fragTransaction.replace(R.id.flContainer, fragment);
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
        });

        if (stockAvailable != null && stockAvailable.getCount() > 0) {

        } else {

            SharedPreferences.Editor editor = tempPref.edit();
            editor.putString(getString(R.string.dis_id_key_noti), tempPref.getString(getString(R.string.dis_id_key), ""));
            editor.putString(getString(R.string.dis_name_key_noti), tempPref.getString(getString(R.string.dis_name_key), ""));
            editor.apply();
            addNotification();
        }


        return view;
    }

    private void addNotification() {

        String strTitle = "Opening stock pending", strBody = "Please take stock for distributor name :  " + tempPref.getString(getString(R.string.dis_name_key), "");
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.ic_stat_sb)
                        .setContentTitle("Opening stock pending")   //this is the title of notification
                        .setColor(101)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Please take stock for distributor name :  " + tempPref.getString(getString(R.string.dis_name_key), "")));   //this is the message showed in notification

        PendingIntent contentIntent;
        Intent intent;

        tempPref = getActivity().getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        if (tempPref.getString(getString(R.string.attendance_key), "").equalsIgnoreCase("Present")) {

            intent = new Intent(getActivity(), OrderBookingRetailing.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            SharedPreferences.Editor editor = tempPref.edit();
            editor.putString("dash", "0");
            editor.apply();
            /*contentIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);*/
            Notifications(strTitle, strBody, "", intent);

        }


        // Add as notification
        /*NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());*/
        String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date());
        long insertSuccess = salesBeatDb.insertInappNotification("Opening stock pending", "Please take stock for distributor name :- " + tempPref.getString(getString(R.string.dis_name_key), ""), "", date);


    }

    /**/
    private void Notifications(String title, String message, String action, Intent intent) {
        Random random = new Random();
        String CHANNEL_ID = "SalesBeat";
        CharSequence name = "SalesBeat";
        String Description = "SalesBeat";

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        //@Umesh 17-08-2022
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                    .setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentText(message)
                    .setLargeIcon(drawableToBitmap(getResources().getDrawable(R.drawable.logo)))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSmallIcon(R.drawable.ic_stat_sb)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setColor(getResources().getColor(R.color.colorPrimary));
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder = new NotificationCompat.Builder(getActivity());
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_sb)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setLargeIcon(drawableToBitmap(getResources().getDrawable(R.drawable.logo)))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setContentIntent(pendingIntent);
        } else {
            notificationBuilder = new NotificationCompat.Builder(getActivity());
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_sb)
                    .setAutoCancel(true)
                    .setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setLargeIcon(drawableToBitmap(getResources().getDrawable(R.drawable.logo)))
                    .setColor(getResources().getColor(android.R.color.transparent))
                    .setContentText(message)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        }
        if (notificationManager != null) {
            notificationManager.notify(random.nextInt(1000), notificationBuilder.build());
        }
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, MyFirebaseMessagingService.class.getSimpleName());
        wl.acquire(15000);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getView() != null)
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action) {
            View menuItemView = requireActivity().findViewById(R.id.action);
            showDialog(menuItemView);
            return true;
        }

        return super.onOptionsItemSelected(item);


//        switch (item.getItemId()) {
//            case R.id.action:
//                View menuItemView = requireActivity().findViewById(R.id.action);
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
        ImageView closeButton = searchViewAndroidActionBar.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                adapter.getFilter().filter("");
                //Find EditText view
                EditText et = searchViewAndroidActionBar.findViewById(R.id.search_src_text);

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

        PopupMenu popupMenu = new PopupMenu(requireContext(), menuItem);

        requireActivity().getMenuInflater().inflate(R.menu.change_distributor, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.changeDistributor) {

                    FragmentManager fragManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragTransaction = fragManager.beginTransaction();
                    //fragTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
                    Fragment fragment = new DistributorList();
                    fragTransaction.replace(R.id.flContainer, fragment);
                    fragTransaction.addToBackStack(null);
                    fragTransaction.commit();

                }

                return false;
            }
        });

        popupMenu.show();
    }

    private void searchTerm(String newText) {

        beatList.clear();
        Cursor cursor = null;

        try {

            cursor = salesBeatDb.searchIntoBeatListTable(newText, tempPref.getString(getString(R.string.dis_id_key), ""));

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    BeatItem beatItem = new BeatItem();
                    beatItem.setBeatId(cursor.getString(cursor.getColumnIndex("beat_id")));
                    beatItem.setBeatName(cursor.getString(cursor.getColumnIndex("beat_name")));
                    beatList.add(beatItem);
                } while (cursor.moveToNext());
            }

            //to remove duplicate value from list
            Set<BeatItem> hs2 = new LinkedHashSet<>();
            hs2.addAll(beatList);
            beatList.clear();
            beatList.addAll(hs2);

            Collections.sort(beatList, new Comparator<BeatItem>() {
                @Override
                public int compare(BeatItem o1, BeatItem o2) {
                    return o1.getBeatName().compareTo(o2.getBeatName());
                }
            });

        } catch (Exception ex) {
            ex.getMessage();
        } finally {

            if (cursor != null)
                cursor.close();
        }

        BeatListAdapter adapter = new BeatListAdapter(getContext(), beatList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rcvBeatList.setLayoutManager(layoutManager);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        rcvBeatList.setLayoutAnimation(animation);
        rcvBeatList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        mToolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        //mDrawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.quantum_grey_600));

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
                        //mDrawerLayout.setStatusBarBackgroundColor(getThemeColor(OrderBookingRetailing.this, R.attr.colorPrimaryDark));
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
            // mDrawerLayout.setStatusBarBackgroundColor(ThemeUtils.getThemeColor(OrderBookingRetailing.this, R.attr.colorPrimaryDark));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }
}
