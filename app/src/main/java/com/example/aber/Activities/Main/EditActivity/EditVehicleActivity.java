package com.example.aber.Activities.Main.EditActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.aber.Adapters.UserHomeAdapter;
import com.example.aber.Adapters.UserVehicleAdapter;
import com.example.aber.Models.User.Home;
import com.example.aber.Models.User.Vehicle;
import com.example.aber.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditVehicleActivity extends AppCompatActivity {
    private ImageView buttonBack, buttonCancelPopUp;

    private FirebaseAuth mAuth;

    private PopupWindow popupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vehicle);

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        initPopupWindow();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> vehicles = (List<Map<String, Object>>) documentSnapshot.get("vehicles");
                        List<Vehicle> vehicleList = fetchVehicleData(vehicles);
                        setUpVehicleRecyclerView(vehicleList);
                    }
                })
                .addOnFailureListener(e -> showErrorAndFinish());
    }

    private void showErrorAndFinish() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    private void setUpVehicleRecyclerView(List<Vehicle> vehicleList) {
        RecyclerView vehicleRecyclerView = findViewById(R.id.vehicleRecyclerView);

        vehicleRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        UserVehicleAdapter userVehicleAdapter = new UserVehicleAdapter(
                this,
                vehicleList
        );

        vehicleRecyclerView.setAdapter(userVehicleAdapter);
    }

    private List<Vehicle> fetchVehicleData(List<Map<String, Object>> vehiclesData) {
        List<Vehicle> vehicleList = new ArrayList<>();
        if (vehiclesData != null) {
            for (Map<String, Object> vehicle : vehiclesData) {
                String userVehicleName = (String) vehicle.get("name");

                Vehicle vehicles = new Vehicle();
                vehicles.setName(userVehicleName);



                vehicleList.add(vehicles);
            }
        }
        return vehicleList;
    }

    private void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_vehicle_form, null);

        // Set the background color with alpha transparency
        popupView.setBackgroundColor(getResources().getColor(R.color.popup_background, null));

        // Create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        buttonCancelPopUp = popupView.findViewById(R.id.cancelBtn);
        buttonCancelPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        Button submitBtn = popupView.findViewById(R.id.submitNewVehicleBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void newVehicle(View view) {
        popupWindow.showAsDropDown(view, 0, 0);
    }
}