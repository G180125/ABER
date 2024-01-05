package com.example.aber.Activities.Register.Fragment;

import android.app.ProgressDialog;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;

public class RegisterHomeFragment extends Fragment {
    private Button doneButton;
    private String name, phoneNumber, gender;
    private EditText addressEditText;
    private ImageView homeImageView;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_register_home, container, false);
        Bundle args = getArguments();
        if (args != null) {
            name = args.getString("name", "");
            phoneNumber = args.getString("phoneNumber", "");
            gender = args.getString("gender", "");
        }

        doneButton = root.findViewById(R.id.done_button);
        addressEditText = root.findViewById(R.id.address_edit_text);
        homeImageView = root.findViewById(R.id.home_image);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addressEditText.getText().toString();
                String homeImage = "path";
                AndroidUtil.showLoadingDialog(progressDialog);
                if (validateInputs(address, homeImage)){
                    toRegisterVehicleFragment(address, homeImage);
                }
            }
        });

        return root;
    }

    private boolean validateInputs(String address, String homeImage){
        if(address.isEmpty()){
            showToast("Address can not be empty");
            AndroidUtil.hideLoadingDialog(progressDialog);
            return false;
        }
        if(homeImage.isEmpty()){
            AndroidUtil.hideLoadingDialog(progressDialog);
            showToast("Failed to upload Image");
            return false;
        }
        AndroidUtil.hideLoadingDialog(progressDialog);
        showToast("Finish Step 2/5");
        return true;
    }

    private void toRegisterVehicleFragment(String address, String homeImage){
        RegisterVehicleFragment fragment = new RegisterVehicleFragment();

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("phoneNumber", phoneNumber);
        bundle.putString("gender", gender);
        bundle.putString("address", address);
        bundle.putString("homeImage", homeImage);
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