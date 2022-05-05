package com.cis.myproject_weather;

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
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cis.myproject_weather.Model.Root;
import com.cis.myproject_weather.adapter.weather_adapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class hourley_weather extends AppCompatActivity implements LocationListener {
    RecyclerView Recycle_Weather;
    Button getweather;
    LocationManager locationManager;
    Location mylocation;
    weather_adapter adapter;
    Handler Mainhandler= new Handler();
    double lat;
    double longi;
    @SuppressLint({"NewApi", "MissingPermission"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hourley_weather);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getweather=findViewById(R.id.getweather);
        getweather.setOnClickListener((view)->{
            LoctionThread thread2=new LoctionThread();
            thread2.start();

        });

//        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        if(location!=null)
//        {
//            GetWeather(location.getLatitude(),location.getLongitude());
//        }
        LoctionThread thread=new LoctionThread();
        thread.start();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onRestart() {
        super.onRestart();
        if(mylocation==null)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) hourley_weather.this);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mylocation = location;
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
        //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If user presses allow
                    Toast.makeText(hourley_weather.this, "Permission granted!", Toast.LENGTH_SHORT).show();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000000, 500, this);
                } else {
                    //If user presses deny
                    Toast.makeText(hourley_weather.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    this.finishAffinity();
                }
                break;
            }
        }
    }
    public void GetWeather(double lat, double lon) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);


        //////////=============================API Key must assign===========================//////////////////////
        Call<Root> call = apiInterface.GetAllbyCoordList(String.valueOf(lat), String.valueOf(lon), "metric", "API Key");
        call.enqueue(new Callback<Root>() {

            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                if (response.body() != null) {
                    adapter = new weather_adapter(response.body());
                    Recycle_Weather = findViewById(R.id.recycle_weather);
                    Recycle_Weather.setLayoutManager(new LinearLayoutManager(hourley_weather.this, RecyclerView.VERTICAL, false));
                    Recycle_Weather.setAdapter(adapter);
                } else {

                    Toast.makeText(hourley_weather.this, "Your Coordinates are Invalid Name", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                Toast.makeText(hourley_weather.this, "No Connection check the internet", Toast.LENGTH_LONG).show();
            }
        });
    }
//    public void foo(Context context) {
//        // when you need location
//        // if inside activity context = this;
//
//        SingleShotLocationProvider.requestSingleUpdate(context,
//                new SingleShotLocationProvider.LocationCallback() {
//                    @Override
//                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
//                        GetWeather(location.latitude,location.longitude);
//                    }
//                });
//    }
//    public void statusCheck() {
//        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            buildAlertMessageNoGps();
//
//        }
//    }
//
//    private void buildAlertMessageNoGps() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        dialog.cancel();
//                    }
//                });
//        final AlertDialog alert = builder.create();
//        alert.show();
//    }
class  LoctionThread extends Thread
{
    @Override
    public void run() {
        if (ActivityCompat.checkSelfPermission(hourley_weather.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(hourley_weather.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) hourley_weather.this, new String[]{ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

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
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) hourley_weather.this);
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
