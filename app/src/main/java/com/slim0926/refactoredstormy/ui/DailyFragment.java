package com.slim0926.refactoredstormy.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.slim0926.refactoredstormy.R;
import com.slim0926.refactoredstormy.adapters.DayAdapter;
import com.slim0926.refactoredstormy.weather.Day;

import butterknife.BindView;

/**
 * Created by sue on 12/14/16.
 */

public class DailyFragment extends ListFragment {

    private Day[] mDays;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // create view from fragment_daily layout
        View view = inflater.inflate(R.layout.fragment_daily, container, false);

        // get an array of Day data from main activity.
        mDays = ((MainActivity)getActivity()).mDays;
        ((MainActivity)getActivity()).setBackgroundGradient();

        // create adapter to connect data to layout
        DayAdapter dayAdapter = new DayAdapter(getContext(), mDays);
        setListAdapter(dayAdapter);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // show a toast of view data when view is clicked.
        String dayOfTheWeek = mDays[position].getDayOfTheWeek();
        String conditions = mDays[position].getSummary();
        String highTemp = mDays[position].getTemperatureMax() + "";
        String message = String.format("On %s the high will be %s and it will be %s",
                dayOfTheWeek,
                highTemp,
                conditions);
        Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
    }
}
















