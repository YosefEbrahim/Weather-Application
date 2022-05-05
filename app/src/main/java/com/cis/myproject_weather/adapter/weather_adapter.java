package com.cis.myproject_weather.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.cis.myproject_weather.Model.Root;
import com.cis.myproject_weather.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class weather_adapter extends RecyclerView.Adapter<weather_adapter.MyViewHolder> {
    Root Model;
    ArrayList<Root.List> model;
    private Context context;
    public weather_adapter(Root model) {
        this.Model=model;
        this.model=Model.getList();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hourley_weather_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat outputformat =new SimpleDateFormat("E, dd-MM-yyyy hh:mm:ss aa", Locale.US);
        Date date = null;
        String output = null;
        try{
            date= df.parse(model.get(position).dt_txt);
            output = outputformat.format(date);
            holder.txt_date.setText(String.valueOf(output));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.Txt_wind_speed.setText((model.get(position).getWind().getSpeed() + " m/s"));
            holder.Txt_gust.setText((model.get(position).getWind().getGust() + " m/s"));
            holder.Txt_description.setText(model.get(position).getWeather().get(0).getDescription());
            holder.Txt_pressure.setText(model.get(position).getMain().getPressure() + "hPa");
            holder.Txt_humidity.setText(model.get(position).getMain().getHumidity() + "%");
            holder.Txt_visibility.setText((model.get(position).getVisibility() / 1000) + ".00Km");
            holder.Txt_clouds.setText(model.get(position).getClouds().getAll() + "%");
            holder.Txt_country.setText(Model.getCity().getCountry());
            holder.Txt_name.setText(Model.getCity().getName());
            holder.txt_Degree.setText(model.get(position).getMain().getTemp() + "º");
            String img_url = "http://openweathermap.org/img/w/" + model.get(position).getWeather().get(0).getIcon() + ".png";
            Glide.with(context).load(img_url).into(holder.img_icon);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Txt_wind_speed;
        TextView Txt_description;
        TextView Txt_pressure;
        TextView Txt_humidity;
        TextView Txt_visibility;
        TextView Txt_clouds;
        TextView Txt_country;
        TextView Txt_name;
        TextView Txt_gust;
        TextView txt_Degree;
        TextView txt_date;
        ImageView img_icon;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_icon=itemView.findViewById(R.id.weather_icon);
            txt_Degree=itemView.findViewById(R.id.txt_degree);
            Txt_wind_speed=itemView.findViewById(R.id.txt_wind_speed);
            Txt_description=itemView.findViewById(R.id.txt_description);
            Txt_pressure=itemView.findViewById(R.id.txt_pressure);
            Txt_humidity=itemView.findViewById(R.id.txt_humidity);
            Txt_visibility=itemView.findViewById(R.id.txt_visibility);
            Txt_clouds=itemView.findViewById(R.id.txt_clouds);
            Txt_country=itemView.findViewById(R.id.txt_country);
            Txt_name=itemView.findViewById(R.id.txt_name);
            Txt_gust=itemView.findViewById(R.id.txt_gust);
            txt_date=itemView.findViewById(R.id.txt_date);
        }
    }
}
