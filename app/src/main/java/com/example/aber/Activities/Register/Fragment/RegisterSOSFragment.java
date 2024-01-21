package com.example.aber.Activities.Register.Fragment;

import static com.example.aber.Utils.AndroidUtil.showToast;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.aber.R;

public class RegisterSOSFragment extends Fragment {
    private String name, phoneNumber, gender, address, homeImage, brand, vehicleName, color, seat, plate, vehicleImage;
    private Button doneButton;
    private EditText sosNameEditText, sosPhoneNumberEditText;
    private ImageView buttonBack;
    private double latitude, longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_register_sos, container, false);

        if (savedInstanceState != null) {
            sosNameEditText.setText(savedInstanceState.getString("name"));
            sosPhoneNumberEditText.setText(savedInstanceState.getString("phone"));

        }

        Bundle args = getArguments();
        if (args != null) {
            name = args.getString("name", "");
            phoneNumber = args.getString("phoneNumber", "");
            gender = args.getString("gender", "");
            address = args.getString("address", "");
            latitude = args.getDouble("latitude");
            longitude = args.getDouble("longitude");
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
        buttonBack = root.findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        doneButton = root.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sosName = sosNameEditText.getText().toString();
                String sosPhone = sosPhoneNumberEditText.getText().toString();

                if(validateInputs(sosName, sosPhone)) {
                    toRegisterAccountFragment(sosName, sosPhone);
                }
            }
        });

        return root;
    }

    private boolean validateInputs(String name, String phoneNumber) {
        StringBuilder errorMessage = new StringBuilder();

        if (name.isEmpty()) {
            errorMessage.append("Name cannot be empty\n");
        }

        if (!validatePhoneNumber(phoneNumber)) {
            errorMessage.append("Invalid phone number\n");
        }

        if (gender == null) {
            errorMessage.append("Please select your gender\n");
        }

        // Display error messages for each field
        if (errorMessage.length() > 0) {
            showToast(errorMessage.toString().trim()); // Trim to remove trailing newline
            if (name.isEmpty()) {
                sosNameEditText.setError("Name cannot be empty");
            }
            if (!validatePhoneNumber(phoneNumber)) {
                sosPhoneNumberEditText.setError("Invalid phone number");
            }

            return false;
        }
        showToast("Finish Step 4/5");
        return true;
    }


    private boolean validatePhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll("\\s", "");
        if (phoneNumber !=null){
            if (phoneNumber.matches("\\d{9}")) {
                return true;
            }
            if (phoneNumber.matches("^\\+?84\\d{9}$")) {
                return true;
            }
        }

//        if (phoneNumber.matches("\\d{9}")) {
//            return true;
//        }
//
////        if (phoneNumber.matches("84\\d{9}")) {
////            return true;
////        }
//        if (phoneNumber.matches("^\\+?84\\d{9}$")) {
//            return true;
//        }

        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", sosNameEditText.getText().toString());
        outState.putString("phone", sosPhoneNumberEditText.getText().toString());

    }

    private void toRegisterAccountFragment(String sosName, String sosPhone){
        RegisterAccountFragment fragment = new RegisterAccountFragment();

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("phoneNumber", phoneNumber);
        bundle.putString("gender", gender);
        bundle.putString("address", address);
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        bundle.putString("homeImage", homeImage);
        bundle.putString("brand", brand);
        bundle.putString("vehicleName", vehicleName);
        bundle.putString("color", color);
        bundle.putString("seat", seat);
        bundle.putString("plate", plate);
        bundle.putString("vehicleImage", vehicleImage);
        bundle.putString("sosName", sosName);
        bundle.putString("sosPhone", sosPhone);
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_right,
                R.anim.slide_out_left
        );
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showToast(String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}