package com.example.aber.Activities.Register.Fragment;

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
import com.example.aber.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterAccountFragment extends Fragment {
    private final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Button doneButton, loginButton;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_register_account, container, false);
        firebaseManager = new FirebaseManager();

        doneButton = root.findViewById(R.id.done_button);
        loginButton = root.findViewById(R.id.login_button);
        emailEditText = root.findViewById(R.id.email_edit_text);
        passwordEditText = root.findViewById(R.id.password_edit_text);
        confirmPasswordEditText = root.findViewById(R.id.confirm_password_edit_text);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if(validateInputs(email, password, confirmPassword)){
                    firebaseManager.register(email, password, new FirebaseManager.OnTaskCompleteListener() {
                        @Override
                        public void onTaskSuccess(String message) {
                            String userID = message;
                            hideLoadingDialog();
                            showToast("Finish Step 1/5");
                            toRegisterProfileFragment(email, password, userID);
                        }

                        @Override
                        public void onTaskFailure(String message) {
                            hideLoadingDialog();
                            showToast(message);
                        }
                    });
                } else {
                    hideLoadingDialog();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
                requireActivity().finish();
            }
        });

        return root;
    }

    private boolean validateInputs(String email, String password, String confirmPassword){

        if (!isValidEmail(email)){
            showToast("Invalid Email");
            return false;
        }
        if(password.length() < 6){
            showToast("Password must have at least 6 characters");
            return false;
        }
        if(!password.equals(confirmPassword)){
            showToast("Passwords are not matching");
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void toRegisterProfileFragment(String email, String password, String userID){
        RegisterProfileFragment fragment = new RegisterProfileFragment();

        Bundle bundle = new Bundle();
        bundle.putString("userID", userID);
        bundle.putString("email", email);
        bundle.putString("password", password);
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