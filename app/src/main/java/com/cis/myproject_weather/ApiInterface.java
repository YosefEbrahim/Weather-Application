package com.cis.myproject_weather;

import com.cis.myproject_weather.Model.Root;
import com.cis.myproject_weather.Model.Weather_Parent;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("weather")
     Call<Weather_Parent> GetAll(@Query("q") String City, @Query("units") String Unit, @Query("appid") String Key);
    @GET("weather")
     Call<Weather_Parent> GetAllbyCoord(@Query("lat") String lat,@Query("lon") String lon,@Query("units") String Unit,@Query("appid") String Key);
    @GET("forecast")
    Call<Root> GetAllbyCoordList(@Query("lat") String lat, @Query("lon") String lon, @Query("units") String Unit, @Query("appid") String Key);
    @GET("forecast")
    Call<Root> GetAllbyName(@Query("q") String City, @Query("units") String Unit, @Query("appid") String Key);
    @GET("weather")
    Call<Weather_Parent> GetById(@Query("id") String id, @Query("units") String Unit, @Query("appid") String Key);
}
