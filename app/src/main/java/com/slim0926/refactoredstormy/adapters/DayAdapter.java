package com.slim0926.refactoredstormy.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.slim0926.refactoredstormy.R;
import com.slim0926.refactoredstormy.weather.Day;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by sue on 7/27/16.
 */

public class DayAdapter extends BaseAdapter {

    private Context mContext;
    private Day[] mDays;

    public DayAdapter(Context context, Day[] days) {
        mContext = context;
        mDays = days;
    }

    @Override
    public int getCount() {
        return mDays.length;
    }

    @Override
    public Object getItem(int position) {
        return mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0; // We aren't going to use this. Tag items for easy reference.
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // create a new view if one hasn't been created already
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.temperatureLabel = (TextView) convertView.findViewById(R.id.temperatureLabel);
            holder.dayLabel = (TextView) convertView.findViewById(R.id.dayNameLabel);

            convertView.setTag(holder);
        } else {
            // assign the already created view
            holder = (ViewHolder) convertView.getTag();
        }

        Day day = mDays[position];

        // populate the elements of the view with data
        holder.iconImageView.setImageResource(day.getIconId());
        holder.temperatureLabel.setText(day.getTemperatureMax() + "");

        if (position == 0) {
            holder.dayLabel.setText("Today");
        } else {
            holder.dayLabel.setText(day.getDayOfTheWeek());
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView; // public by default
        TextView temperatureLabel;
        TextView dayLabel;
    }
}