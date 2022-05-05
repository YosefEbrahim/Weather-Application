package com.cis.myproject_weather;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cis.myproject_weather.Model.Root;
import com.cis.myproject_weather.adapter.weather_adapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Hourley_Search_Weather extends AppCompatActivity {
    EditText input_city;
    Button Btn_search;
    RecyclerView Recycle_Weather;
    weather_adapter adapter;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hourley_search_weather);
        input_city=findViewById(R.id.City);
        Btn_search=findViewById(R.id.btn_search);
        Btn_search.setOnClickListener((view)-> {
            performSearch();
        });
        input_city.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

    }

    public void  performSearch()
    {
        if (!input_city.getText().toString().isEmpty()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiInterface apiInterface = retrofit.create(ApiInterface.class);

            Call<Root> call = apiInterface.GetAllbyName(input_city.getText().toString(), "metric", "f7c7798e4e1861f10a6c393a533d873c");

            call.enqueue(new Callback<Root>() {

                @Override
                public void onResponse(Call<Root> call, Response<Root> response) {
                    if (response.body() != null) {
                        adapter = new weather_adapter(response.body());
                        Recycle_Weather = findViewById(R.id.recycle_weather);
                        Recycle_Weather.setLayoutManager(new LinearLayoutManager(Hourley_Search_Weather.this, RecyclerView.VERTICAL, false));
                        Recycle_Weather.setAdapter(adapter);
                    } else {

                        Toast.makeText(Hourley_Search_Weather.this, "Your City is Invalid Name", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Root> call, Throwable t) {
                    Toast.makeText(Hourley_Search_Weather.this, "No Connection check the internet", Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {

            Toast.makeText(Hourley_Search_Weather.this,"The Field is Empty",Toast.LENGTH_LONG).show();
        }
    }

}
