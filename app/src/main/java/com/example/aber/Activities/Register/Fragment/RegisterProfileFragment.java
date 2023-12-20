package com.example.aber.Activities.Register.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.aber.R;

public class RegisterProfileFragment extends Fragment {
    private Button doneButton;
    private String userID, email, password;
    private EditText nameEditText, phoneNumberEditText;
    private RadioGroup genderRadioGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_register_profile, container, false);
        Bundle args = getArguments();
        if (args != null) {
            userID = args.getString("userID", "");
            email = args.getString("email", "");
            password = args.getString("password","");
        }

        nameEditText = root.findViewById(R.id.name_edit_text);
        phoneNumberEditText = root.findViewById(R.id.phone_number_edit_text);
        genderRadioGroup = root.findViewById(R.id.radioGroupGender);

        doneButton = root.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();
                String selectedGender = getSelectedGender();

                if(validateInputs(name, phoneNumber, selectedGender)){
                    toRegisterHomeFragment(name, phoneNumber, selectedGender);
                }
            }
        });

        return root;
    }

    private String getSelectedGender() {
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.radioButtonMale) {
            return "MALE";
        } else if (selectedId == R.id.radioButtonFemale) {
            return "FEMALE";
        }

        return null;
    }

    private boolean validateInputs(String name, String phoneNumber, String gender){
        if(name.isEmpty()){
            showToast("Name can not be empty");
            return false;
        }
        if(!validatePhoneNumber(phoneNumber)) {
            showToast("Invalid phone number");
            return false;
        }
        if (gender == null) {
            showToast("Please select your gender");
            return false;
        }
        showToast("Finish Step 2/5");
        return true;
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll("\\s", "");

        if (phoneNumber.matches("0\\d{9}")) {
            return true;
        }

        if (phoneNumber.matches("84\\d{9}")) {
            return true;
        }

        return false;
    }

    private void toRegisterHomeFragment(String name, String phoneNumber, String gender){
        RegisterHomeFragment fragment = new RegisterHomeFragment();

        Bundle bundle = new Bundle();
        bundle.putString("userID", userID);
        bundle.putString("email", email);
        bundle.putString("password", password);
        bundle.putString("name", name);
        bundle.putString("phoneNumber", phoneNumber);
        bundle.putString("gender", gender);
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
