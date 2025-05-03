package com.newsalesbeatApp.fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.RetailerActivity;
import com.newsalesbeatApp.customview.AnimCheckBox;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;

/*
 * Created by Dhirendra Thakur on 16-12-2017.
 */

public class ConfirmationAnimationFragment extends Fragment {

    AnimCheckBox checkbox;
    TextView tvDeliverMessage;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.confirmation_animation_activity, parent, false);
        //prefSFA = getContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        checkbox = view.findViewById(R.id.animCheckBox);
        tvDeliverMessage = view.findViewById(R.id.tvDeliverMessage);

        int value = getArguments().getInt("value");

        if (value == 1)
            tvDeliverMessage.setText("Stock successfully captured");
        else if (value == 2)
            tvDeliverMessage.setText("Order confirmed successfully");
        else if (value == 3)
            tvDeliverMessage.setText("Order confirmed successfully");
        else if (value == 4)
            tvDeliverMessage.setText("Closing confirmed successfully");

        new MyAsynchTask(value).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        return view;
    }

    public void onBackPressed() {
    }

    private class MyAsynchTask extends AsyncTask<Integer, Void, Integer> {

        int val = 0;
        //UtilityClass utilityClass;

        public MyAsynchTask(int value) {
            this.val = value;
            //utilityClass = new UtilityClass(getContext());
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected Integer doInBackground(Integer... voids) {

            try {
                Thread.sleep(500);
                return 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return 0;
        }

        protected void onPostExecute(Integer I) {

            try {

                if (I == 1 && val == 2) {

                    checkbox.setChecked(true);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {

                            // Do something after 5s = 5000ms
                            Intent intent = new Intent(getActivity(), RetailerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("tabPosition", 1);
                            startActivity(intent);
                            if (getActivity() != null)
                                getActivity().finish();
                            //getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

                        }
                    }, 250);

                } else if (I == 1 && val == 1) {

                    checkbox.setChecked(true);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {

                            // Do something after 5s = 5000ms
                            SalesBeatDb salesBeatDb = SalesBeatDb.getHelper(getContext());
//                            Cursor cursor = salesBeatDb.getAllDataFromBeatListTable(
//                                    requireContext().getSharedPreferences(getString(R.string.temp_pref_name),
//                                            Context.MODE_PRIVATE).getString(getString(R.string.dis_id_key), ""));

//                            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
//
//                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                                //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
//                                Fragment fragment = new BeatList();
//                                ft.replace(R.id.flContainer, fragment);
//                                ft.commit();
//
//                            } else {
//
//                                Toast.makeText(getContext(), "There is no beat in this distributor", Toast.LENGTH_SHORT).show();
//
//                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                                //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
//                                Fragment fragment = new DistributorList();
//                                ft.replace(R.id.flContainer, fragment);
//                                ft.commit();
//
//                            }


                            Cursor beatCur = null;

                            try {

                                beatCur = salesBeatDb.getDisBeatMap(requireContext().getSharedPreferences(getString(R.string.temp_pref_name),
                                        Context.MODE_PRIVATE).getString(getString(R.string.dis_id_key), ""));

                                if (beatCur != null && beatCur.getCount() > 0 && beatCur.moveToFirst()) {

                                    FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                                    //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
                                    Fragment fragment = new BeatList();
                                    ft.replace(R.id.flContainer, fragment);
                                    ft.commit();


                                } else {

                                    Toast.makeText(getContext(), "There is no beat in this distributor", Toast.LENGTH_SHORT).show();

                                    FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                                    //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
                                    Fragment fragment = new DistributorList();
                                    ft.replace(R.id.flContainer, fragment);
                                    ft.commit();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {

                                if (beatCur != null)
                                    beatCur.close();
                            }

                        }
                    }, 250);

                } else if (I == 1 && val == 3) {

                    checkbox.setChecked(true);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {

                            // Do something after 5s = 5000ms
                            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                            //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
                            Fragment fragment = new DistributorList();
                            ft.replace(R.id.flContainer, fragment);
                            ft.commit();

                        }
                    }, 250);

                } else if (I == 1 && val == 4) {

                    checkbox.setChecked(true);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {

                            // Do something after 5s = 5000ms
                            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                            //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
                            Fragment fragment = new DistributorList2();
                            ft.replace(R.id.frmClosing, fragment);
                            ft.commit();

                        }
                    }, 250);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}