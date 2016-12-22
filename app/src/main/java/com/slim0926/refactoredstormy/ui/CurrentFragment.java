package com.slim0926.refactoredstormy.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.slim0926.refactoredstormy.R;
import com.slim0926.refactoredstormy.weather.Current;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sue on 12/12/16.
 */

public class CurrentFragment extends Fragment {

    protected Current mCurrent;

    // bind views from the fragment_current layout to variables we can use in code
    @BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.refreshImageView) ImageView mRefreshImageView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.forecastButton) Button mForecastButton;
    @BindView(R.id.current_fragment) RelativeLayout mRelativeLayout;


    // interface that the activity implements to handle CurrentFragment's mForecastButton click.
    public interface OnForecastSelectedInterface {
        // the method that the activity must handle for what happens when mForecastButton is clicked.
        void onForecastButtonClicked();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // view is created from fragment_current layout
        View view = inflater.inflate(R.layout.fragment_current, container, false);
        ButterKnife.bind(this, view);

        // initially the progress bar is set to invisible
        mProgressBar.setVisibility(View.INVISIBLE);
        // onClick listener is set on mRefreshImageView so that when it is clicked, the forecast data is updated.
        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getForecast(MainActivity.LATITUDE, MainActivity.LONGITUDE);
            }
        });

        final OnForecastSelectedInterface mainActivity = (OnForecastSelectedInterface)getActivity();
        // sets up an onClick listener on mForecastButton so that when it is clicked,
        // the onForecastButtonClicked method runs
        mForecastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onForecastButtonClicked();
            }
        });

        return view;
    }

    protected void toggleRefresh() {
        // shows progress bar while data is updating
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    protected void updateDisplay() {

        try {
            // get current data from main activity
            mCurrent = ((MainActivity) getActivity()).mForecast.getCurrent();
            // populate data on view
            mTemperatureLabel.setText(mCurrent.getTemperature() + "");
            mTimeLabel.setText("At " + mCurrent.getFormattedTime() + " it will be");
            mHumidityValue.setText(mCurrent.getHumidity() + "");
            mPrecipValue.setText(mCurrent.getPrecipChance() + "%");
            mSummaryLabel.setText(mCurrent.getSummary());

            Drawable drawable = getResources().getDrawable(mCurrent.getIconId());
            mIconImageView.setImageDrawable(drawable);

        } catch (NullPointerException e) {
            // catches nullPointer exception when updateDisplay runs when back button is pressed to end the app.
            // I didn't know how else to handle this exception. I didn't know how to make updateDisplay
            // not run in this instance. Also, I left this clause blank because I couldn't establish the
            // context to do anything else since at this point everything is shut down?
        }
    }
}
