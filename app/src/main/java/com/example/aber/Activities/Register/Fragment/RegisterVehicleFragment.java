package com.example.aber.Activities.Register.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aber.R;

public class RegisterVehicleFragment extends Fragment {
    private final String[] PROVINCE_NUMBER = {
            "11", "65", "12", "66", "14", "67", "15", "16", "68", "17",
            "69", "18", "70", "19", "71", "20", "72", "21", "73", "22",
            "74", "23", "75", "24", "76", "25", "77", "26", "78", "27",
            "79", "28", "80", "29", "30", "31", "32", "33", "40", "81",
            "34", "82", "35", "83", "36", "84", "37", "85", "38", "86",
            "43", "88", "47", "89", "48", "90", "49", "92", "41", "50",
            "51", "52", "53", "54", "55", "56", "57", "58", "59", "93",
            "39", "60", "94", "61", "95", "62", "97", "63", "98", "64", "99"
    };
    private Button doneButton;
    private String userID, email, password, name, phoneNumber, gender, address, homeImage;
    private EditText vehicleBrandEditText, vehicleNameEditText, vehicleColorEditText, vehiclePlateEditText;
    private Spinner seatCapacitySpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_register_vehicle, container, false);
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
        }

        vehicleBrandEditText = root.findViewById(R.id.vehicle_brand_edit_text);
        vehicleNameEditText = root.findViewById(R.id.vehicle_name_edit_text);
        vehicleColorEditText = root.findViewById(R.id.vehicle_color_edit_text);
        vehiclePlateEditText = root.findViewById(R.id.vehicle_number_plate_edit_text);

        seatCapacitySpinner = root.findViewById(R.id.seat_capacity_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.seat_capacity_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seatCapacitySpinner.setAdapter(adapter);

        doneButton = root.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String brand = vehicleBrandEditText.getText().toString();
                String vehicleName = vehicleNameEditText.getText().toString();
                String color = vehicleColorEditText.getText().toString();
                String selectedSeatCapacity = seatCapacitySpinner.getSelectedItem().toString();
                String plate = vehiclePlateEditText.getText().toString();

                if(validateInputs(brand, vehicleName, color, selectedSeatCapacity, plate)){
                    toRegisterSOSFragment(brand, vehicleName, color, selectedSeatCapacity, plate);
                }
            }
        });

        return root;
    }

    private boolean validateInputs(String brand, String name, String color, String selectedSeatCapacity, String plate){
        if(brand.isEmpty()){
            showToast("Vehicle Brand can not be empty");
            return false;
        }
        if(name.isEmpty()){
            showToast("Vehicle Name can not be empty");
            return false;
        }
        if(color.isEmpty()){
            showToast("Vehicle Color can not be empty");
            return false;
        }
        if(selectedSeatCapacity.isEmpty()){
            showToast("Vehicle Seat Capacity can not be empty");
            return false;
        }
        if(!validatePlate(plate)){
            return false;
        }
        showToast("Finish Step 4/5");
        return true;
    }

    private boolean validatePlate(String plate) {
        String plateRegex = "^[0-9]{2}-[A-Z]\\s[0-9]{5}$";

        if (plate.matches(plateRegex)) {
            String provinceNumber = plate.substring(0, 2);
            if (isValidProvinceNumber(provinceNumber)) {
                return true;
            } else {
                showToast("Invalid province number");
                return false;
            }
        } else {
            showToast("Invalid plate format");
            return false;
        }
    }

    private boolean isValidProvinceNumber(String provinceNumber) {
        for (String validProvince : PROVINCE_NUMBER) {
            if (validProvince.equals(provinceNumber)) {
                return true;
            }
        }
        return false;
    }


    private void toRegisterSOSFragment(String brand, String vehicleName, String color, String seat, String plate){
        RegisterSOSFragment fragment = new RegisterSOSFragment();

        Bundle bundle = new Bundle();
        bundle.putString("userID", userID);
        bundle.putString("email", email);
        bundle.putString("password", password);
        bundle.putString("name", name);
        bundle.putString("phoneNumber", phoneNumber);
        bundle.putString("gender", gender);
        bundle.putString("address", address);
        bundle.putString("homeImage", homeImage);
        bundle.putString("brand", brand);
        bundle.putString("vehicleName", vehicleName);
        bundle.putString("color", color);
        bundle.putString("seat", seat);
        bundle.putString("plate", plate);
        bundle.putString("vehicleImage", "path");
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