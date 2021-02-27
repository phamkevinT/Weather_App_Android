package com.kevinpham.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constant Variables:

    // Arbitrary request Code for granting location permission
    final int REQUEST_CODE = 123;
    // Base URL used to make API calls
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data (API Key)
    final String APP_ID = "277ecef3b0ad58589d622f759af64e83";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // Location Provider: GPS or Network Tower
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // Will start or stop requesting location updates
    LocationManager mLocationManager;
    // Will be notified if location has been changed
    LocationListener mLocationListener;


    /**
     * On opening the weather application
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = findViewById(R.id.locationTV);
        mWeatherImage = findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = findViewById(R.id.tempTV);
        ImageButton changeCityButton = findViewById(R.id.changeCityButton);

        // Navigate between activities using intent
        // Param: (where we are currently, where we want to send after onClick)
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(WeatherController.this, ChangeCityController.class);
                startActivity(myIntent);
            }
        });
    }


    /**
     * This life cycle method is called just before activity comes on screen
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Line below used for debugging in Logcat
        Log.d("Clima", "onResume() called");

        // Get the intent from ChangeCityController
        Intent myIntent = getIntent();
        // Get the key-value paired passed from ChangeCityController
        String city = myIntent.getStringExtra("City");

        if (city != null) {
            // Get weather for new location
            getWeatherForNewCity(city);
        } else {

            // Line below used for debugging in Logcat
            Log.d("Clima", "Getting weather for current location");

            // Get weather for current location
            getWeatherForCurrentLocation();
        }
    }


    /**
     * Get weather by providing parameters (city name, APP_ID) to API Call
     * Example: "api.openweathermap.org/data/2.5/weather?q=sanjose&appid=277ecef3b0ad58589d622f759af64e83"
     *
     * @param city the city name
     */
    private void getWeatherForNewCity(String city) {

        RequestParams params = new RequestParams();
        // 'q' is what OpenWeatherMap uses to identify we are sending in name of city
        params.put("q", city);
        params.put("appid", APP_ID);
        letsDoSomeNetworking(params);
    }


    /**
     * Get the weather for current device location
     * Process location changes
     */
    private void getWeatherForCurrentLocation() {

        // Get instance of LocationManager
        // getSystemService() return type Object, so must be casted to LocationManager
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Checking for update for device location changes
        mLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                // Line below used for debugging in Logcat
                Log.d("Clima", "onLocationChanged() callback received");

                // Get the longitude of new location
                String longitude = String.valueOf(location.getLongitude());
                // Get the latitude of new location
                String latitude = String.valueOf(location.getLatitude());

                // Lines below used for debugging in Logcat
                Log.d("Clima", "longitude is: " + longitude);
                Log.d("Clima", "latitude is:" + latitude);

                // Create parameters based on new location
                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);
                letsDoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            // Network provider or GPS has been disabled
            @Override
            public void onProviderDisabled(String s) {

                // Line below used for debugging in Logcat
                Log.d("Clima", "onProviderDisabled() callback received");
            }
        };

        // Used to check for location permission granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permission for location access
            // Result of user decision made in onRequestPermissionsResult()
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            return;
        }

        // Request location update
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }


    /**
     * Check whether user has granted permission for location access
     *
     * @param requestCode  the requestCode we made to check for validity
     * @param permissions  the device permissions
     * @param grantResults Result of permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the request code in the callback matches the REQUEST_CODE constant that we made
        if (requestCode == REQUEST_CODE) {

            // Result of permission is in the 'grantResult' parameter
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Line below used for debugging in Logcat
                Log.d("Clima", "onRequestPermissionsResult(): Permission granted!");
                // Get the weather for current location
                getWeatherForCurrentLocation();
            } else {
                // Line below used for debugging in Logcat
                Log.d("Clima", "Location permission denied");
            }
        }
    }


    /**
     * Make a http request to weather API
     *
     * @param params the longitude, the latitude, and APP_ID as a parameter bundle
     */
    private void letsDoSomeNetworking(RequestParams params) {

        AsyncHttpClient client = new AsyncHttpClient();

        // HTTP GET Request
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {

            // Get Request Successful
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                // Line below used for debugging in Logcat
                Log.d("Clima", "HTTP GET Request Successful! JSON:" + response.toString());

                // The weather data in form of JSON
                // Following MVC pattern -> Data extraction and manipulation is job of the Model (WeatherDataModel Class)
                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);

                // Update and display user interface based on weather data received
                updateUI(weatherData);
            }

            // Get Request Unsuccessful
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {

                // Lines below used for debugging in Logcat
                Log.e("Clima", "HTTP GET Request Failed: " + e.toString());
                Log.d("Clima", "Status code " + statusCode);

                // Toast message to inform user that request failed.
                Toast.makeText(WeatherController.this, "User Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Update the app user interface based on data extracted from JSON
     *
     * @param weather WeatherDataModel object containing data
     */
    private void updateUI(WeatherDataModel weather) {

        // Set the temperature displayed on screen
        mTemperatureLabel.setText(weather.getTemperature());
        // Set the city name displayed on screen
        mCityLabel.setText(weather.getCity());

        // Get the weather icon name under drawable folder and set the image on screen
        int resourceID = getResources().getIdentifier(weather.getIconName(), "drawable", getPackageName());
        mWeatherImage.setImageResource(resourceID);
    }


    /**
     * Stop checking for location updates when app is not in use.
     * Frees up resources.
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
