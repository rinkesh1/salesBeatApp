package com.newsalesbeatApp.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.customview.TouchImageView;

/*
 * Created by Dhirendra Thakur on 15-01-2018.
 */

public class FullScreenImageAdapter extends PagerAdapter {

    private Activity _activity;
    private String[] filepath;

    // constructor
    public FullScreenImageAdapter(Activity activity, String[] catalogItems) {
        this._activity = activity;
        this.filepath = catalogItems;
    }

    @Override
    public int getCount() {
        return this.filepath.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView imgDisplay;

        LayoutInflater inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);

        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        imgDisplay.setImageURI(Uri.parse(filepath[position]));

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}

