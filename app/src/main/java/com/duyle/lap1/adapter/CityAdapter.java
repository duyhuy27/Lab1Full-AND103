package com.duyle.lap1.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.duyle.lap1.R;
import com.duyle.lap1.models.City;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    private List<City> cities;
    private Context mContext;

    private iClick listener;

    public  interface  iClick {
        void deleteCity(int position, String idName);
        void updateCity(int position,String idName);
    }
    public CityAdapter(Context context, List<City> cities, iClick listener) {
        this.cities = cities;
        this.mContext = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cityitem_city, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, @SuppressLint("RecyclerView") int position) {
        City city = cities.get(position);
        holder.textViewCityName.setText("City: " + city.getName());
        holder.textViewState.setText("State: " + city.getState());
        holder.textViewCountry.setText("Country: " + city.getCountry());

        // Display regions
        StringBuilder regions = new StringBuilder();
        for (String region : city.getRegions()) {
            regions.append(region).append(", ");
        }
        // Remove the last comma and space
        if (regions.length() > 2)
            regions.delete(regions.length() - 2, regions.length());
        holder.textViewRegionList.setText(regions.toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Please choose function ...")
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.updateCity(position, city.getIDName());
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.deleteCity(position,city.getIDName());
                            }
                        }).show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return cities.size();
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCityName;
        public TextView textViewState;
        public TextView textViewCountry;
        public TextView textViewRegionList;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCityName = itemView.findViewById(R.id.textViewCityName);
            textViewState = itemView.findViewById(R.id.textViewState);
            textViewCountry = itemView.findViewById(R.id.textViewCountry);
            textViewRegionList = itemView.findViewById(R.id.textViewRegionList);
        }
    }
}
