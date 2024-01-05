package com.example.aber.Activities.Register.Fragment;

import static com.example.aber.Utils.AndroidUtil.showToast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
    private String name, phoneNumber, gender, address, homeImage, brand, vehicleName, color, seat, plate, vehicleImage;
    private Button doneButton;
    private EditText sosNameEditText, sosPhoneNumberEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_register_sos, container, false);

        Bundle args = getArguments();
        if (args != null) {
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
                String sosName = sosNameEditText.getText().toString();
                String sosPhone = sosPhoneNumberEditText.getText().toString();

                showToast(requireContext(),"Finish Step 4/5");
                toRegisterAccountFragment(sosName, sosPhone);
            }
        });

        return root;
    }

    private void toRegisterAccountFragment(String sosName, String sosPhone){
        RegisterAccountFragment fragment = new RegisterAccountFragment();

        Bundle bundle = new Bundle();
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
}