package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SecondActivity extends AppCompatActivity {

    EditText city_et;
    TextView city_tv;
    TextView description_tv;
    TextView temp_tv;
    ImageView image_iv;
    TextView humidity_tv;
    TextView max_temp_tv;
    TextView min_temp_tv;
    TextView pressure_tv;
    TextView wind_tv;
    ImageView search_icon;

    WeatherAPI serviceAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        city_et = findViewById(R.id.city_et);
        city_tv = findViewById(R.id.city_tv);
        description_tv = findViewById(R.id.description_tv);
        temp_tv = findViewById(R.id.temperature_tv);
        image_iv = findViewById(R.id.image_iv);
        humidity_tv = findViewById(R.id.humidity_tv);
        max_temp_tv = findViewById(R.id.max_temp_tv);
        min_temp_tv = findViewById(R.id.min_temp_tv);
        pressure_tv = findViewById(R.id.pressure_tv);
        wind_tv = findViewById(R.id.wind_tv);
        search_icon = findViewById(R.id.search_icon);

        View fab = findViewById(R.id.fab);

        SecondActivity ct = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ct.finish();
            }
        });

        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String q = String.valueOf(city_et.getText());
                serviceAPI.getWeatherWithName(q).enqueue(new Callback<OpenWeatherMap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {
                        assert response.body() != null;
                        displayWeather(response.body());
                    }

                    @Override
                    public void onFailure(Call<OpenWeatherMap> call, Throwable t) {
                        Toast.makeText(SecondActivity.this, "Api error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // retrofitWeather = retrofit.create(RetrofitWeather.class);
        serviceAPI = RetrofitWeather.getClient().create(WeatherAPI.class);
    }


    @SuppressLint("SetTextI18n")
    private void displayWeather(OpenWeatherMap data){
        Log.i("XXX", MessageFormat.format("{0} Temp, {1} Hum", data.getMain().getTemp(), data.getMain().getHumidity()));
        city_tv.setText(data.getName());
        temp_tv.setText(data.getMain().getTemp() + "°C");
        description_tv.setText(data.getWeather().get(0).getDescription());
        humidity_tv.setText(": " + data.getMain().getHumidity() + "%");
        max_temp_tv.setText(": " + data.getMain().getTempMax() + "°C");
        min_temp_tv.setText(": " + data.getMain().getTempMin() + "°C");
        pressure_tv.setText(": " + data.getMain().getPressure());
        wind_tv.setText(": " + data.getWind().getSpeed());
    }
}