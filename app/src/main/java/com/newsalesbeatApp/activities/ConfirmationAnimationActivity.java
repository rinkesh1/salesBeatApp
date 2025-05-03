package com.newsalesbeatApp.activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.fragments.ConfirmationAnimationFragment;

/*
 * Created by MTC on 02-11-2017.
 */

public class ConfirmationAnimationActivity extends AppCompatActivity {
    FrameLayout fragmentAnimationContainer;
    Fragment fragment;

    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.animation_activity);
        fragmentAnimationContainer = findViewById(R.id.fragmentAnimationContainer);

        Bundle bundle1 = new Bundle();
        bundle1.putInt("value", 2);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = new ConfirmationAnimationFragment();
        fragment.setArguments(bundle1);
        ft.replace(R.id.fragmentAnimationContainer, fragment);
        ft.commit();


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onBackPressed() {

    }
}
