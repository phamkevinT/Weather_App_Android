package com.kevinpham.climapm;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataModel {

    // Member variables:
    private String mTemperature;
    private int mCondition;
    private String mCity;
    private String mIconName;


    /**
     * Extracting weather data from JSON
     *
     * @param jsonObject the JSON object contain all the weather data from API call
     * @return WeatherDataModel Object
     */
    public static WeatherDataModel fromJson(JSONObject jsonObject) {

        try {

            // WeatherDataModel Object
            WeatherDataModel weatherData = new WeatherDataModel();

            // Access city name from JSON object via 'name' key
            weatherData.mCity = jsonObject.getString("name");

            // Get the numerical weather condition from JSON nested under 'weather/0/id'
            weatherData.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");

            // Set icon name based on numerical weather condition
            weatherData.mIconName = updateWeatherIcon(weatherData.mCondition);

            // Calculate the temperature in Fahrenheit
            double tempResult = (jsonObject.getJSONObject("main").getDouble("temp") * 1.8) - 459.67;
            int roundedValue = (int) Math.rint(tempResult);

            // Set the temperature to the String value of temperature
            weatherData.mTemperature = Integer.toString(roundedValue);

            // Return the WeatherDataModel object
            return weatherData;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Get corresponding weather icon based on the weather condition extracted from JSON
     *
     * @param condition the numerical weather condition
     * @return the name of the weather icon
     */
    private static String updateWeatherIcon(int condition) {

        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
    }


    // Getter methods for temperature, city, and icon name:

    public String getTemperature() {
        return mTemperature + "°";
    }

    public String getCity() {
        return mCity;
    }

    public String getIconName() {
        return mIconName;
    }
}
