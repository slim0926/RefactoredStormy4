package com.slim0926.refactoredstormy.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slim0926.refactoredstormy.R;
import com.slim0926.refactoredstormy.adapters.HourAdapter;
import com.slim0926.refactoredstormy.weather.Hour;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sue on 12/14/16.
 */

public class HourlyFragment extends Fragment {

    @BindView(R.id.hourlyRecyclerView) RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // create view from fragment_hourly layout
        View view = inflater.inflate(R.layout.fragment_hourly, container, false);
        ButterKnife.bind(this, view);

        // create adapter to connect data to layout
        HourAdapter hourAdapter = new HourAdapter(getContext(), ((MainActivity)getActivity()).mHours);
        mRecyclerView.setAdapter(hourAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        return view;
    }
}
