package com.slim0926.refactoredstormy.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.slim0926.refactoredstormy.R;
import com.slim0926.refactoredstormy.weather.Current;
import com.slim0926.refactoredstormy.weather.Day;
import com.slim0926.refactoredstormy.weather.Forecast;
import com.slim0926.refactoredstormy.weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements CurrentFragment.OnForecastSelectedInterface {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "daily_forecast";
    public static final String HOURLY_FORECAST = "hourly_forecast";
    public static final String CURRENT_FRAGMENT = "current_fragment";
    public static final String VIEWPAGER_FRAGMENT = "viewpager_fragment";
    public static final double LATITUDE = 37.8267;
    public static final double LONGITUDE = -122.423;
    private static final String CURRENT_TEMP = "current_temperature";

    private CurrentFragment mCurrentFragment;
    protected Forecast mForecast;
    protected Day[] mDays;
    protected Hour[] mHours;
    private double mCurrentTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // if there is a previously saved instance state that includes an array of Day objects,
        // then assign the appropriate data to mDays, mHours and mCurrentTemp
        // at this point, we are in either the DualPaneFragment or the ViewPagerFragment showcasing the
        // DailyFragment and the HourlyFragment
        if (savedInstanceState != null && savedInstanceState.getParcelableArray(DAILY_FORECAST) != null) {
            Parcelable[] dayParcelables = savedInstanceState.getParcelableArray(DAILY_FORECAST);
            mDays = Arrays.copyOf(dayParcelables, dayParcelables.length, Day[].class);
            Parcelable[] hourParcelables = savedInstanceState.getParcelableArray(HOURLY_FORECAST);
            mHours = Arrays.copyOf(hourParcelables, hourParcelables.length, Hour[].class);
            // we need the current temperature to set the background gradient.
            mCurrentTemp = savedInstanceState.getDouble(CURRENT_TEMP);
        }

        // get an existing CurrentFragment
        mCurrentFragment = (CurrentFragment) getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT);
        if (mCurrentFragment == null) {
            // if a CurrentFragment does not exist, create a new one
            mCurrentFragment = new CurrentFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.placeHolder, mCurrentFragment, CURRENT_FRAGMENT);
            fragmentTransaction.commit();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // get the current fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.placeHolder);
        if (currentFragment.equals(mCurrentFragment)) {
            // if the current fragment is a CurrentFragment then get forecast
            getForecast(LATITUDE, LONGITUDE);
        }

    }

    protected void getForecast(double latitude, double longitude) {
        String apiKey = "1f28907b9d0546d138867adc1cb76c50";
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitude;

        // check to see if network is available
        if (isNetworkAvailable()) {
            // turn on progress bar
            mCurrentFragment.toggleRefresh();

            // create client and request
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastUrl).build();

            // make the call
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                // if the call fails, turn off progress bar and show an error alert
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentFragment.toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                // if we get a response, turn off progress bar and extract jsonData and set mForecast
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentFragment.toggleRefresh();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                mCurrentTemp = mForecast.getCurrent().getTemperature();
                                setBackgroundGradient();
                                mCurrentFragment.updateDisplay();
                                }
                            });

                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
        }
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();
        // set Current, Hour and Day data from jsonData
        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }

    private Day[] getDailyForecast(String jsonData)  throws JSONException {
        // create new json object using jsonData
        JSONObject forecast = new JSONObject(jsonData);
        // assign daily variables from json object
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];

        // populate the array of days with json array data
        for (int i=0; i<data.length(); i++) {
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();

            day.setSummary(jsonDay.getString("summary"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimezone(timezone);

            days[i] = day;
        }

        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        // create new json object using jsonData
        JSONObject forecast = new JSONObject(jsonData);
        // assign hourly variables with json object data
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];

        // populate the array of hours with json array data
        for (int i=0; i<data.length(); i++) {
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();

            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(timezone);

            hours[i] = hour;
        }

        return hours;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        // create new json object using jsonData
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        // populate Current object with json object data
        Current currentWeather = new Current();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        Log.d(TAG, currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");

    }

    protected void setBackgroundGradient() {

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.placeHolder);

        // get current temperature from mCurrentTemp
        if (mCurrentTemp >= 80.0) {
            // if it's equal to or above 80 degrees, show yellow to orange gradient background
            frameLayout.setBackgroundResource(R.drawable.bg_gradient);
        } else if (mCurrentTemp < 80.0 && mCurrentTemp >= 60.0) {
            // if it's equal to or above 60 degrees and below 80 degrees, show green gradient background
            frameLayout.setBackgroundResource(R.drawable.bg_gradient_favorable);
        } else {
            // if it's below 60 degrees, show blue gradient background
            frameLayout.setBackgroundResource(R.drawable.bg_gradient_cold);
        }
    }

    @Override
    public void onForecastButtonClicked() {
        // check to see if we are on a tablet
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
        // assign data to variables mDays and mHours to be saved in onSaveInstanceState
        mDays = mForecast.getDailyForecast();
        mHours = mForecast.getHourlyForecast();

        if (!isTablet) {
            // if it's not a tablet, show DailyFragment and HourlyFragment in ViewPagerFragment
            ViewPagerFragment fragment = new ViewPagerFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.placeHolder, fragment, VIEWPAGER_FRAGMENT);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            // if it is a tablet, show DailyFragment and HourlyFragmentn in DualPaneFragment
            DualPaneFragment fragment = new DualPaneFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.placeHolder, fragment, VIEWPAGER_FRAGMENT);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mCurrentFragment != null && mCurrentFragment.isVisible()) {
            // if you've backpressed to a CurrentFragment then get forecast
            getForecast(LATITUDE, LONGITUDE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save an array of hourly data and an array of daily data
        outState.putParcelableArray(HOURLY_FORECAST, mHours);
        outState.putParcelableArray(DAILY_FORECAST, mDays);
        // save the current temperature
        outState.putDouble(CURRENT_TEMP, mCurrentTemp);
    }
}
