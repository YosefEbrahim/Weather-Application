package com.cis.myproject_weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cis.myproject_weather.Model.Root;
import com.cis.myproject_weather.Model.Weather_Parent;
import com.cis.myproject_weather.adapter.weather_adapter;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements LocationListener{
    EditText input_city;
    TextView Txt_wind_speed;
    TextView Txt_description;
    TextView Txt_pressure;
    TextView Txt_humidity;
    TextView Txt_visibility;
    TextView Txt_clouds;
    TextView Txt_country;
    TextView Txt_name;
    TextView txt_Degree;
    ImageView img_icon;
    LinearLayout Box_weather;
    NavigationView navigationView;
    ImageView nav_icon;
    DrawerLayout drawerLayout;
    LocationManager locationManager;
    Handler Mainhandler= new Handler();
    double lat;
    double longi;
    Location mylocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input_city=findViewById(R.id.City);
        img_icon=findViewById(R.id.weather_icon);
        txt_Degree=findViewById(R.id.txt_degree);
        Txt_wind_speed=findViewById(R.id.txt_wind_speed);
        Txt_description=findViewById(R.id.txt_description);
        Txt_pressure=findViewById(R.id.txt_pressure);
        Txt_humidity=findViewById(R.id.txt_humidity);
        Txt_visibility=findViewById(R.id.txt_visibility);
        Txt_clouds=findViewById(R.id.txt_clouds);
        Txt_country=findViewById(R.id.txt_country);
        Txt_name=findViewById(R.id.txt_name);
        Box_weather=findViewById(R.id.box_weather);
        drawerLayout=findViewById(R.id.drawerlayout);
        nav_icon=findViewById(R.id.nav_icon);
        navigationView=findViewById(R.id.navView);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        nav_icon.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.city_name: {
                                Intent t = new Intent(MainActivity.this, City_Name.class);
                                startActivity(t);
                                return true;
                            }
                            case R.id.city_id:
                            {
                                Intent t=new Intent(MainActivity.this,City_Id.class);
                                startActivity(t);
                                return true;
                            }
                            case R.id.coordinate:
                            {
                                Intent t=new Intent(MainActivity.this,Coordinates.class);
                                startActivity(t);
                                return true;
                            }
                            case R.id.weather_hour:
                            {
                                Intent t=new Intent(MainActivity.this,hourley_weather.class);
                                startActivity(t);
                                return true;
                            }
                            case R.id.weather_hour_search:
                            {
                                Intent t=new Intent(MainActivity.this,Hourley_Search_Weather.class);
                                startActivity(t);
                                return true;
                            }
                        }
                        return true;
                    }
                }
        );
        LoctionThread thread=new LoctionThread();
        thread.start();

    }
    public void GetWeather(double lat, double lon) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);







        //////////=============================API Key must assign===========================//////////////////////

        Call<Weather_Parent> call = apiInterface.GetAllbyCoord(String.valueOf(lat), String.valueOf(lon), "metric", "API Key");
        call.enqueue(new Callback<Weather_Parent>() {

            @Override
            public void onResponse(Call<Weather_Parent> call, Response<Weather_Parent> response) {
                if (response.body() != null) {
                    String img_url = "http://openweathermap.org/img/w/" + response.body().getWeather().get(0).getIcon() + ".png";
                    Glide.with(MainActivity.this).load(img_url).into(img_icon);
                    txt_Degree.setText((response.body().getMain().getTemp() + " º"));
                    Txt_clouds.setText((response.body().getClouds().getAll() + "%"));
                    Txt_country.setText(String.valueOf(response.body().getSys().getCountry()));
                    Txt_humidity.setText((response.body().getMain().getHumidity() + "%"));
                    Txt_pressure.setText((response.body().getMain().getPressure() + "hPa"));
                    Txt_description.setText(String.valueOf(response.body().getWeather().get(0).getDescription()));
                    Txt_name.setText(String.valueOf(response.body().getName()));
                    Txt_visibility.setText(((response.body().getVisibility() / 1000) + ".00Km"));
                    Txt_wind_speed.setText(((int) response.body().getWind().getSpeed() + " meter/sec"));
                    Box_weather.setVisibility(View.VISIBLE);
                } else {

                    Toast.makeText(MainActivity.this, "Your Coordinates are Invalid Name", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Weather_Parent> call, Throwable t) {
                Toast.makeText(MainActivity.this, "No Connection check the internet", Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onRestart() {
        super.onRestart();
        if(mylocation==null)
        {

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) MainActivity.this);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mylocation=location;
        if (mylocation!=null)
        {
            GetWeather(mylocation.getLatitude(), mylocation.getLongitude());
            locationManager.removeUpdates(this);
            return;
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }
    class  LoctionThread extends Thread
    {
        @Override
        public void run() {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) MainActivity.this, new String[]{ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    buildAlertMessageNoGps();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) MainActivity.this);
                }
            } else {
                Mainhandler.post(new Runnable() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void run() {
                        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                            buildAlertMessageNoGps();
                        }else
                        {
                            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (locationGPS != null) {
                                lat = locationGPS.getLatitude();
                                longi = locationGPS.getLongitude();
                                GetWeather(lat, longi);
                            }else
                            {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) MainActivity.this);
                            }
                        }
                    }
                });

            }
        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @SuppressLint("MissingPermission")
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        return;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
