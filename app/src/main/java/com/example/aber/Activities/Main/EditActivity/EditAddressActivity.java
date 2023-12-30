package com.example.aber.Activities.Main.EditActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aber.Adapters.UserHomeAdapter;
import com.example.aber.Models.User.Home;
import com.example.aber.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditAddressActivity extends AppCompatActivity {
    private ImageView buttonBack, buttonCancelPopUp;
    private UserHomeAdapter userHomeAdapter;
    private FirebaseAuth mAuth;

    private TextView textView;

    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

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
                        List<Map<String, Object>> homes = (List<Map<String, Object>>) documentSnapshot.get("homes");
                        List<Home> addressList = fetchAddressData(homes);
                        setUpAddressRecyclerView(addressList);
                    }
                })
                .addOnFailureListener(e -> showErrorAndFinish());

    }

    private void showErrorAndFinish() {

        finish();
    }

    private void setUpAddressRecyclerView(List<Home> addressList) {
        RecyclerView addressRecyclerView = findViewById(R.id.addressRecyclerView);

        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        UserHomeAdapter userHomeAdapter = new UserHomeAdapter(
                this,
                addressList
        );

        addressRecyclerView.setAdapter(userHomeAdapter);
    }

    private List<Home> fetchAddressData(List<Map<String, Object>> homeAddressData) {
        List<Home> addressList = new ArrayList<>();
        if (homeAddressData != null) {
            for (Map<String, Object> addressData : homeAddressData) {
                String address = (String) addressData.get("address");

                Home homeAddress = new Home();
                homeAddress.setAddress(address);



                addressList.add(homeAddress);
            }
        }
        return addressList;
    }

    private void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_address_form, null);

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

        Button submitBtn = popupView.findViewById(R.id.submitNewAddressBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the submit button click
            }
        });
    }



    public void newAddress(View view) {

        popupWindow.showAsDropDown(view, 0, 0);
    }
}
