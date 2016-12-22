package com.slim0926.refactoredstormy.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slim0926.refactoredstormy.R;

/**
 * Created by sue on 12/13/16.
 */

public class ViewPagerFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // create view from fragment_viewpager layout
        final View view = inflater.inflate(R.layout.fragment_viewpager, container, false);
        // create an instance of HourlyFragment and an instance of DailyFragment
        final HourlyFragment hourlyFragment = new HourlyFragment();
        final DailyFragment dailyFragment = new DailyFragment();

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            // shows fragment and title of fragment depending on which tab is selected
            @Override
            public Fragment getItem(int position) {
                return position == 0 ? hourlyFragment : dailyFragment;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ? "Hourly Forecast" : "Daily Forecast";
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        // sets up tabLayout with viewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
