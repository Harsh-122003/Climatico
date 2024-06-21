package com.example.climatico;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout background;
    RequestQueue weatherdata;
    String url;
    SearchView searchView;
    LottieAnimationView animationView;
    TextView location, temperature, maxTemp, minTemp, day, date, sunset, humidity, wind, sea, condition, sunrise, weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        background = findViewById(R.id.background);
        searchView = findViewById(R.id.searchView);
        animationView = findViewById(R.id.lottieAnimationView);
        location = findViewById(R.id.location);
        temperature = findViewById(R.id.temperature);
        maxTemp = findViewById(R.id.maxTemp);
        minTemp = findViewById(R.id.minTemp);
        day = findViewById(R.id.day);
        date = findViewById(R.id.date);
        sunset = findViewById(R.id.sunset);
        humidity = findViewById(R.id.humidity);
        wind = findViewById(R.id.wind);
        sea = findViewById(R.id.sea);
        condition = findViewById(R.id.condition);
        sunrise = findViewById(R.id.sunrise);
        weather = findViewById(R.id.weather);

        weatherdata = Volley.newRequestQueue(this);
        fetchWeatherData("Surat");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null) {
                    fetchWeatherData(query);
                } else {
                    Toast.makeText(MainActivity.this, "Please Enter A Valid City Name", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void fetchWeatherData(String cityName) {
        url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=45db6c7ff523cb1c80f7416b61513aaf";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        location.setText(response.getString("name"));

                        JSONArray weatherData = response.getJSONArray("weather");
                        JSONObject weatherObject = (JSONObject) weatherData.get(0);
                        String weatherCondition = weatherObject.getString("main");
                        weather.setText(weatherCondition);
                        condition.setText(weatherObject.getString("description"));

                        DecimalFormat df_obj = new DecimalFormat("#.##");
                        JSONObject main = response.getJSONObject("main");
                        temperature.setText(df_obj.format(Float.parseFloat(main.getString("temp")) / 10) + " ℃");
                        maxTemp.setText("Max Temp: " + (Float.parseFloat(main.getString("temp_max")) / 10) + " ℃");
                        minTemp.setText("Min Temp: " + (Float.parseFloat(main.getString("temp_min")) / 10) + " ℃");
                        humidity.setText(main.getString("humidity") + " %");
                        sea.setText(main.getString("pressure") + " hPa");

                        JSONObject windSpeed = response.getJSONObject("wind");
                        wind.setText(windSpeed.getString("speed") + " m/s");

                        JSONObject sys = response.getJSONObject("sys");
                        sunrise.setText(time(Long.parseLong(sys.getString("sunrise"))));
                        sunset.setText(time(Long.parseLong(sys.getString("sunset"))));

                        date.setText(new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date()));
                        day.setText(new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date()));

                        changeImage(weatherCondition);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "You Are Offline", Toast.LENGTH_LONG).show();;
            }
        });

        weatherdata.add(request);
    }

    public void changeImage(String condition) {
        switch (condition) {
            case "Clear Sky":
            case "Sunny":
            case "Clear":
                background.setBackgroundResource(R.drawable.sunny_background);
                animationView.setAnimation(R.raw.sun);
                break;

            case "Partly Clouds":
            case "Clouds":
            case "Overcast":
            case "Mist":
            case "Foggy":
                background.setBackgroundResource(R.drawable.colud_background);
                animationView.setAnimation(R.raw.cloud);
                break;

            case "Light Rain":
            case "Drizzle":
            case "Moderate Rain":
            case "Showers":
            case "Heavy Rain":
                background.setBackgroundResource(R.drawable.rain_background);
                animationView.setAnimation(R.raw.rain);
                break;

            case "Light Snow":
            case "Moderate Snow":
            case "Heavy Snow":
            case "Blizzard":
                background.setBackgroundResource(R.drawable.snow_background);
                animationView.setAnimation(R.raw.snow);
                break;

            default:
                background.setBackgroundResource(R.drawable.sunny_background);
                animationView.setAnimation(R.raw.sun);

        }
        animationView.playAnimation();
    }

    private String time(long timeStamp) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timeStamp * 1000));
    }
}