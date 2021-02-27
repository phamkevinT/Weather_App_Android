package com.kevinpham.climapm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChangeCityController extends AppCompatActivity {

    /**
     * Change the activity page/layout when trying to change the city
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout background
        setContentView(R.layout.change_city_layout);

        // Get the user input for new city name
        final EditText editTextField = findViewById(R.id.queryET);

        ImageButton backButton = findViewById(R.id.backButton);

        // Back-button will close the ChangeCityController activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Grab the user's text entered in the editTextField
        editTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // Grab the text entered
                String newCity = editTextField.getText().toString();

                // New Intent to navigate back to WeatherController Activity
                Intent newCityIntent = new Intent(ChangeCityController.this, WeatherController.class);

                // Inform WeatherController of new city name.
                // Packaged as an 'Extra' key-value pair
                newCityIntent.putExtra("City", newCity);

                // Launch activity
                startActivity(newCityIntent);

                return false;
            }
        });
    }
}