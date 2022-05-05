package com.cis.myproject_weather;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cis.myproject_weather.Model.Weather_Parent;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Coordinates extends AppCompatActivity {
    EditText input_latitude;
    EditText input_longitude;
    Button Btn_search_coord;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coordinates_search);
        input_latitude = findViewById(R.id.latitude);
        input_longitude = findViewById(R.id.longitude);
        Btn_search_coord = findViewById(R.id.btn_search_coord);
        img_icon = findViewById(R.id.weather_icon);
        txt_Degree = findViewById(R.id.txt_degree);
        Txt_wind_speed = findViewById(R.id.txt_wind_speed);
        Txt_description = findViewById(R.id.txt_description);
        Txt_pressure = findViewById(R.id.txt_pressure);
        Txt_humidity = findViewById(R.id.txt_humidity);
        Txt_visibility = findViewById(R.id.txt_visibility);
        Txt_clouds = findViewById(R.id.txt_clouds);
        Txt_country = findViewById(R.id.txt_country);
        Txt_name = findViewById(R.id.txt_name);
        Box_weather = findViewById(R.id.box_weather);
        Btn_search_coord.setOnClickListener((view) -> {
            performSearchByCoord();
        });
        input_latitude.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearchByCoord();
                    return true;
                }
                return false;
            }
        });
        input_longitude.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearchByCoord();
                    return true;
                }
                return false;
            }
        });
    }

    public void performSearchByCoord() {
        if (!input_latitude.getText().toString().isEmpty() && !input_longitude.getText().toString().isEmpty()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiInterface apiInterface = retrofit.create(ApiInterface.class);


            //////////=============================API Key must assign===========================//////////////////////
            Call<Weather_Parent> call = apiInterface.GetAllbyCoord(input_latitude.getText().toString(), input_longitude.getText().toString(), "metric", "API Key");

            call.enqueue(new Callback<Weather_Parent>() {

                @Override
                public void onResponse(Call<Weather_Parent> call, Response<Weather_Parent> response) {
                    if (response.body() != null) {
                        String img_url = "http://openweathermap.org/img/w/" + response.body().getWeather().get(0).getIcon() + ".png";
                        Glide.with(Coordinates.this).load(img_url).into(img_icon);
                        txt_Degree.setText((response.body().getMain().getTemp() + "º"));
                        Txt_clouds.setText(response.body().getClouds().getAll() + "%");
                        Txt_country.setText(String.valueOf(response.body().getSys().getCountry()));
                        Txt_humidity.setText((response.body().getMain().getHumidity() + "%"));
                        Txt_pressure.setText((response.body().getMain().getPressure() + "hPa"));
                        Txt_description.setText(String.valueOf(response.body().getWeather().get(0).getDescription()));
                        Txt_name.setText(String.valueOf(response.body().getName()));
                        Txt_visibility.setText(((response.body().getVisibility() / 1000) + ".00Km"));
                        Txt_wind_speed.setText(((int) response.body().getWind().getSpeed() + " meter/sec"));
                        Box_weather.setVisibility(View.VISIBLE);
                    } else {

                        Toast.makeText(Coordinates.this, "Your Coordinates are Invalid Name", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Weather_Parent> call, Throwable t) {
                    Toast.makeText(Coordinates.this, "No Connection check the internet", Toast.LENGTH_LONG).show();
                }
            });
        } else {

            Toast.makeText(this, "The Field is Empty", Toast.LENGTH_LONG).show();
        }
    }
}
