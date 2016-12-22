package com.slim0926.refactoredstormy.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slim0926.refactoredstormy.R;

/**
 * Created by sue on 12/18/16.
 */

public class DualPaneFragment extends Fragment {
    private static final String HOURLY_FRAGMENT = "hourly_fragment";
    private static final String DAILY_FRAGMENT = "daily_fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // view created from fragment_dualpane layout
        final View view = inflater.inflate(R.layout.fragment_dualpane, container, false);

        // when you are dealing with a fragment within a fragment, you must use the child fragment manager.
        FragmentManager fragmentManager = getChildFragmentManager();

        // get previously saved HourlyFragment
        HourlyFragment savedHourlyFragment = (HourlyFragment) fragmentManager.findFragmentByTag(HOURLY_FRAGMENT);
        if (savedHourlyFragment == null) {
            // if there is no previously saved HourlyFragment, create a new HourlyFragment
            HourlyFragment hourlyFragment = new HourlyFragment();
            fragmentManager.beginTransaction().add(R.id.leftPlaceholder, hourlyFragment, HOURLY_FRAGMENT).commit();
        }
        // get previously saved DailyFragment
        DailyFragment savedDailyFragment = (DailyFragment) fragmentManager.findFragmentByTag(DAILY_FRAGMENT);
        if (savedDailyFragment == null) {
            // if there is no previously saved DailyFragment, create a new DailyFragment
            DailyFragment dailyFragment = new DailyFragment();
            fragmentManager.beginTransaction().add(R.id.rightPlaceholder, dailyFragment, DAILY_FRAGMENT).commit();
        }

        return view;
    }
}
