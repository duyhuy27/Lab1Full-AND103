package com.duyle.lap1.add;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.duyle.lap1.R;
import com.duyle.lap1.databinding.ActivityAddCityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCityActivity extends AppCompatActivity{

    private EditText editTextName, editTextState, editTextCountry, editTextPopulation;
    private Button buttonAddCity;

    private ActivityAddCityBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        String cityId = getIntent().getStringExtra("cityId");
        if (cityId != null) {
            loadCityDataToUpdate(cityId);
        }
        editTextName = findViewById(R.id.editTextName);
        editTextState = findViewById(R.id.editTextState);
        editTextCountry = findViewById(R.id.editTextCountry);
        editTextPopulation = findViewById(R.id.editTextPopulation);
        buttonAddCity = findViewById(R.id.buttonAddCity);

        buttonAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrUpdateCity();
            }
        });

        binding.clickBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void addCityToFirestore() {
        binding.animationView.setVisibility(View.VISIBLE);
        String name = editTextName.getText().toString().trim();
        String state = editTextState.getText().toString().trim();
        String country = editTextCountry.getText().toString().trim();
        int population = Integer.parseInt(editTextPopulation.getText().toString().trim());
        String regionsString = binding.editTextRegions.getText().toString().trim();
        List<String> regions = Arrays.asList(regionsString.split("\\s*,\\s*"));
        String idName = binding.editIDCity.getText().toString().trim();
        // Get the selected value for capital
        RadioButton radioButtonTrue = findViewById(R.id.radioButtonTrue);
        boolean isCapital = radioButtonTrue.isChecked();

        CollectionReference citiesRef = db.collection("cities");
        Map<String, Object> city = new HashMap<>();
        city.put("name", name);
        city.put("state", state);
        city.put("country", country);
        city.put("capital", isCapital); // You may change this as needed
        city.put("population", population);
        city.put("regions", regions);
        city.put("idName", idName);

        citiesRef.document(idName).set(city)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddCityActivity.this, "City added successfully", Toast.LENGTH_SHORT).show();
                        // Clear input fields after successful addition
                        binding.animationView.setVisibility(View.GONE);
                        binding.editIDCity.setText("");
                        editTextName.setText("");
                        editTextState.setText("");
                        editTextCountry.setText("");
                        editTextPopulation.setText("");
                        binding.editTextRegions.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.animationView.setVisibility(View.GONE);
                        Toast.makeText(AddCityActivity.this, "Failed to add city: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadCityDataToUpdate(String cityId) {
        binding.title.setText("Update City");
        binding.buttonAddCity.setText("Update City");
        binding.editIDCity.setEnabled(false);
        DocumentReference docRef = db.collection("cities").document(cityId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    City city = documentSnapshot.toObject(City.class);
                    if (city != null) {
                        // Populate the UI with city data for update
                        binding.editIDCity.setText(city.getIDName());
                        editTextName.setText(city.getName());
                        editTextState.setText(city.getState());
                        editTextCountry.setText(city.getCountry());
                        editTextPopulation.setText(String.valueOf(city.getPopulation()));
                        // Set the regions if available
                        if (city.getRegions() != null) {
                            StringBuilder regionsText = new StringBuilder();
                            for (String region : city.getRegions()) {
                                regionsText.append(region).append(", ");
                            }
                            binding.editTextRegions.setText(regionsText.toString());
                        }
                        // Set the capital radio button state
                        binding.radioButtonTrue.setChecked(city.isCapital());
                        binding.radioButtonFalse.setChecked(!city.isCapital());
                    }
                }
            }
        });
    }

    private void addOrUpdateCity() {
        // Get the city ID from the intent (if available, null if adding new city)
        String cityId = getIntent().getStringExtra("cityId");

        // Check if we are adding a new city or updating an existing one
        if (cityId == null) {
            // Adding new city
            addCityToFirestore();
        } else {
            // Updating existing city
            updateCityInFirestore(cityId);
        }
    }

    private void updateCityInFirestore(String cityId) {
        // Retrieve updated city data from the UI
        String name = editTextName.getText().toString().trim();
        String state = editTextState.getText().toString().trim();
        String country = editTextCountry.getText().toString().trim();
        int population = Integer.parseInt(editTextPopulation.getText().toString().trim());
        String regionsString = binding.editTextRegions.getText().toString().trim();
        List<String> regions = Arrays.asList(regionsString.split("\\s*,\\s*"));
        boolean isCapital = binding.radioButtonTrue.isChecked();

        // Update city data in Firestore
        DocumentReference docRef = db.collection("cities").document(cityId);
        Map<String, Object> updatedCity = new HashMap<>();
        updatedCity.put("name", name);
        updatedCity.put("state", state);
        updatedCity.put("country", country);
        updatedCity.put("capital", isCapital); // You may change this as needed
        updatedCity.put("population", population);
        updatedCity.put("regions", regions);
        updatedCity.put("idName", cityId);

        docRef.update(updatedCity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddCityActivity.this, "City updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close AddCityActivity after successful update
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddCityActivity.this, "Failed to update city: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }




}
