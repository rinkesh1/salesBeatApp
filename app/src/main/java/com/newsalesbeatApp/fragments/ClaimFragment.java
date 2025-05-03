package com.newsalesbeatApp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.newsalesbeatApp.R;

import java.util.ArrayList;

/*
 * Created by Dhirendra Thakur on 02-12-2017.
 */

public class ClaimFragment extends Fragment {

    TabLayout claimTab;
    ViewPager claimViewPager;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.claim_layout, parent, false);
        claimTab = view.findViewById(R.id.claimTab);
        claimViewPager = view.findViewById(R.id.claimViewpager);

        setUpViewPager(claimViewPager);
        claimTab.setupWithViewPager(claimViewPager);

        return view;
    }

    private void setUpViewPager(ViewPager claimViewPager) {

        ClaimViewPagerAdapter adapter = new ClaimViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new NewClaim(), "New Claim");
        adapter.addFragment(new ClaimHistory(), "Claim History");
        claimViewPager.setAdapter(adapter);
    }

    private class ClaimViewPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> fragmentTitle = new ArrayList<>();

        public ClaimViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
