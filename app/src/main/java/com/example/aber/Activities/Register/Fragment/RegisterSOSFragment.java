package com.example.aber.Activities.Register.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aber.Activities.LoginActivity;
import com.example.aber.FirebaseManager;
import com.example.aber.Models.User.Gender;
import com.example.aber.Models.User.Home;
import com.example.aber.Models.User.SOS;
import com.example.aber.Models.User.User;
import com.example.aber.Models.User.Vehicle;
import com.example.aber.R;

import java.util.ArrayList;
import java.util.List;

public class RegisterSOSFragment extends Fragment {
    private String userID, email, password, name, phoneNumber, gender, address, homeImage, brand, vehicleName, color, seat, plate, vehicleImage;
    private Button doneButton;
    private EditText sosNameEditText, sosPhoneNumberEditText;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_register_sos, container, false);
        firebaseManager = new FirebaseManager();

        Bundle args = getArguments();
        if (args != null) {
            userID = args.getString("userID", "");
            email = args.getString("email", "");
            password = args.getString("password","");
            name = args.getString("name", "");
            phoneNumber = args.getString("phoneNumber", "");
            gender = args.getString("gender", "");
            address = args.getString("address", "");
            homeImage = args.getString("homeImage", "");
            brand = args.getString("brand", "");
            vehicleName = args.getString("vehicleName", "");
            color = args.getString("color", "");
            seat = args.getString("seat", "");
            plate = args.getString("plate", "");
            vehicleImage = args.getString("vehicleImage", "");
        }

        sosNameEditText = root.findViewById(R.id.name_sos_editText);
        sosPhoneNumberEditText = root.findViewById(R.id.sos_phone_number);


        doneButton = root.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                List<String> vehicleImages = new ArrayList<>();
                vehicleImages.add(vehicleImage);

                List<Home> homeList = new ArrayList<>();
                Home home = new Home(address, homeImage);
                homeList.add(home);

                List<Vehicle> vehicleList = new ArrayList<>();
                Vehicle vehicle = new Vehicle(brand, vehicleName, color, seat, plate, vehicleImages);
                vehicleList.add(vehicle);

                String sosName = sosNameEditText.getText().toString();
                String sosPhone = sosPhoneNumberEditText.getText().toString();

                List<SOS> emergencyContactList = new ArrayList<>();
                SOS emergencyContacts = new SOS(sosName, sosPhone);
                emergencyContactList.add(emergencyContacts);

                Gender userGender = Gender.valueOf(gender);

                User user = new User(email, name, userGender, phoneNumber, homeList, vehicleList, emergencyContactList);
                firebaseManager.addUser(userID, user, new FirebaseManager.OnTaskCompleteListener() {
                    @Override
                    public void onTaskSuccess(String message) {
                        hideLoadingDialog();
                        showToast(message);
                        startActivity(new Intent(requireContext(), LoginActivity.class).putExtra("email", email).putExtra("password", password));
                        requireActivity().finish();
                    }

                    @Override
                    public void onTaskFailure(String message) {
                        hideLoadingDialog();
                        showToast(message);
                    }
                });
            }
        });

        return root;
    }

    private void showLoadingDialog() {
        requireActivity().runOnUiThread(() -> {
            progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        });
    }

    private void hideLoadingDialog() {
        requireActivity().runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}