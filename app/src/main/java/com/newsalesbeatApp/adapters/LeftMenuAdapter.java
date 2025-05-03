package com.newsalesbeatApp.adapters;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.RequiresApi;

import com.newsalesbeatApp.duomenu.DuoOptionView;

import java.util.ArrayList;

/*
 * Created by Dhirendra Thakur on 14-11-2017.
 */

public class LeftMenuAdapter extends BaseAdapter {

    private ArrayList<String> mOptions = new ArrayList<>();
    private ArrayList<DuoOptionView> mOptionViews = new ArrayList<>();

    public LeftMenuAdapter(ArrayList<String> options) {
        mOptions = options;
    }

    @Override
    public int getCount() {
        return mOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return mOptions.get(position);
    }

    public void setViewSelected(int position, boolean selected) {
        Log.d("TAG", "setViewSelected: "+position);
        // Looping through the options in the menu
        // Selecting the chosen option
        for (int i = 0; i < mOptionViews.size(); i++) {
            if (i == position) {
                mOptionViews.get(i).setSelected(selected);
            } else {
                mOptionViews.get(i).setSelected(!selected);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String option = mOptions.get(position);

        // Using the DuoOptionView to easily recreate the demo
        final DuoOptionView optionView;
        if (convertView == null) {
            optionView = new DuoOptionView(parent.getContext());
        } else {
            optionView = (DuoOptionView) convertView;
        }

        if (position == 0)
            optionView.bind(option, null, null);
        else if (position == 1)
            optionView.bind(option, null, null);
        else if (position == 2)
            optionView.bind(option, null, null);
        else if (position == 3)
            optionView.bind(option, null, null);

        optionView.setSideSelectorEnabled(false);
        mOptionViews.add(optionView);

        return optionView;
    }
}
