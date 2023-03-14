package com.example.weatherapp;

import androidx.activity.result.contract.ActivityResultContracts;
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

public class MainActivity extends AppCompatActivity {

    TextView city_tv;
    TextView description_tv;
    TextView temp_tv;
    ImageView image_iv;
    TextView humidity_tv;
    TextView max_temp_tv;
    TextView min_temp_tv;
    TextView pressure_tv;
    TextView wind_tv;

    WeatherAPI serviceAPI;
    MainActivity mainActivity;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city_tv = findViewById(R.id.city_tv);
        description_tv = findViewById(R.id.description_tv);
        temp_tv = findViewById(R.id.temperature_tv);
        image_iv = findViewById(R.id.image_iv);
        humidity_tv = findViewById(R.id.humidity_tv);
        max_temp_tv = findViewById(R.id.max_temp_tv);
        min_temp_tv = findViewById(R.id.min_temp_tv);
        pressure_tv = findViewById(R.id.pressure_tv);
        wind_tv = findViewById(R.id.wind_tv);

        View fab = findViewById(R.id.fab);

        mainActivity = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, SecondActivity.class);
                startActivity(intent);
            }
        });

        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, 100);


        // retrofitWeather = retrofit.create(RetrofitWeather.class);
        serviceAPI = RetrofitWeather.getClient().create(WeatherAPI.class);
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
            loadWeatherFromLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //tak
                } else {
                    //nie
                }
                checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, 101);
                break;

            case 101 :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //tak
                    loadWeatherFromLocation();
                } else {
                    //nie
                }
                break;
        }

        //oadWeatherFromLocation();
    }



    @SuppressLint("MissingPermission")
    private void loadWeatherFromLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), false);


        LocationListener ll = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.i("xxx", "onLocationChanged: ");
                double lat, lon;
                try {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    Log.i("xxx", MessageFormat.format("{0} Lat, {1} Lon", lat, lon));

                    serviceAPI.getWeatherWithLocation(lat, lon).enqueue(new Callback<OpenWeatherMap>() {
                        @Override
                        public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {
                            assert response.body() != null;
                            displayWeather(response.body());
                        }

                        @Override
                        public void onFailure(Call<OpenWeatherMap> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Api error", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (NullPointerException e) {
                    lat = 0;
                    lon = 0;
                    Log.i("xxx", "listener" + String.valueOf(e));
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        double lat, lon;
        try {
            lat = location.getLatitude();
            lon = location.getLongitude();
            Log.i("xxx", MessageFormat.format("first{0} Lat, {1} Lon", lat, lon));

            serviceAPI.getWeatherWithLocation(lat, lon).enqueue(new Callback<OpenWeatherMap>() {
                @Override
                public void onResponse(Call<OpenWeatherMap> call, Response<OpenWeatherMap> response) {
                    assert response.body() != null;
                    displayWeather(response.body());
                }

                @Override
                public void onFailure(Call<OpenWeatherMap> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Api error", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NullPointerException e) {
            lat = 0;
            lon = 0;
            Log.i("xxx", "first"+String.valueOf(e));
        }


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 50, ll);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 50, ll);

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