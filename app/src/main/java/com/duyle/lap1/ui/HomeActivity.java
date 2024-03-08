package com.duyle.lap1.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.duyle.lap1.R;
import com.duyle.lap1.ui.add.AddCityActivity;
import com.duyle.lap1.models.City;
import com.duyle.lap1.adapter.CityAdapter;
import com.duyle.lap1.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements CityAdapter.iClick {


    private RecyclerView recyclerViewCities;
    private CityAdapter cityAdapter;
    private List<City> cityList = new ArrayList<>();
    private FirebaseFirestore db;

    Context context;

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        context = this;
        recyclerViewCities = findViewById(R.id.recyclerViewCities);
        Animation slideInBottomAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(slideInBottomAnimation);
        recyclerViewCities.setLayoutAnimation(layoutAnimationController);

        recyclerViewCities.setLayoutManager(new LinearLayoutManager(this));
        cityAdapter = new CityAdapter(context,cityList, this);
        recyclerViewCities.setAdapter(cityAdapter);
        loadCitiesFromFirestore();


        LottieAnimationView animationView = findViewById(R.id.animationView);

        animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddCityActivity.class);
                startActivity(intent);

            }
        });
    }

    private void loadCitiesFromFirestore() {
        binding.animationViewLoading.setVisibility(View.VISIBLE);
        cityList.clear();
        db.collection("cities")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        cityList.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            City city = documentSnapshot.toObject(City.class);
                            cityList.add(city);
                        }
                        binding.animationViewLoading.setVisibility(View.GONE);
                        cityAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.animationViewLoading.setVisibility(View.GONE);
                        Toast.makeText(HomeActivity.this, "Failed to load cities: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadCitiesFromFirestore();
    }

    @Override
    public void deleteCity(int position, String idname) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Are you sure to delete this city name : " + idname)
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("TAG", "deleteCity: " + idname);
                        db.collection("cities").document(idname).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "City deleted successfully", Toast.LENGTH_SHORT).show();
                                        cityList.remove(position);
                                        cityAdapter.notifyItemRemoved(position);
                                        cityAdapter.notifyItemRangeChanged(position, cityList.size());
                                        dialog.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed to delete city: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setPositiveButton("Not Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void updateCity(int position, String idName) {
        Intent intent = new Intent(HomeActivity.this, AddCityActivity.class);
        intent.putExtra("cityId", idName); // Pass the city ID to retrieve its data in AddCityActivity
        startActivity(intent);
    }
}