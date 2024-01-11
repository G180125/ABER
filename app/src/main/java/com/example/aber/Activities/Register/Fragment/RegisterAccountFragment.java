package com.example.aber.Activities.Register.Fragment;

import static com.example.aber.Utils.AndroidUtil.hideLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.showLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.showToast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
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
import com.example.aber.StripeConnect.StripeClient;
import com.example.aber.StripeConnect.StripeServices;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RegisterAccountFragment extends Fragment {
    private final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                    "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Button doneButton;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private String name, phoneNumber, gender, address, homeImage, brand, vehicleName, color, seat, plate, vehicleImage, sosName, sosPhone, stripeCusID;
    private StripeServices stripeServices;
    private CompositeDisposable compositeDisposable;
    private double latitude, longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_register_account, container, false);
        firebaseManager = new FirebaseManager();
        stripeServices = StripeClient.getRetrofit().create(StripeServices.class);
        compositeDisposable = new CompositeDisposable();

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
            sosName = args.getString("sosName","");
            sosPhone = args.getString("sosPhone", "");
        }

        doneButton = root.findViewById(R.id.done_button);
        emailEditText = root.findViewById(R.id.email_edit_text);
        passwordEditText = root.findViewById(R.id.password_edit_text);
        confirmPasswordEditText = root.findViewById(R.id.confirm_password_edit_text);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog(progressDialog);
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if(validateInputs(email, password, confirmPassword)){
                    firebaseManager.register(email, password, new FirebaseManager.OnTaskCompleteListener() {
                        @Override
                        public void onTaskSuccess(String message) {
                            String userID = message;
                            addUserToFirebase(userID);
                        }

                        @Override
                        public void onTaskFailure(String message) {
                            hideLoadingDialog(progressDialog);
                            showToast(requireContext(),message);
                        }
                    });
                } else {
                    hideLoadingDialog(progressDialog);
                }
            }
        });

        return root;
    }

    private boolean validateInputs(String email, String password, String confirmPassword){

        if (!isValidEmail(email)){
            showToast(requireContext(),"Invalid Email");
            return false;
        }
        if(password.length() < 6){
            showToast(requireContext(),"Password must have at least 6 characters");
            return false;
        }
        if(!password.equals(confirmPassword)){
            showToast(requireContext(),"Passwords are not matching");
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

    private void addUserToFirebase(String userID){
        List<String> vehicleImages = new ArrayList<>();
        vehicleImages.add(vehicleImage);

        List<Home> homeList = new ArrayList<>();
        Home home = new Home(address, homeImage, latitude, longitude);
        homeList.add(home);

        List<Vehicle> vehicleList = new ArrayList<>();
        Vehicle vehicle = new Vehicle(brand, vehicleName, color, seat, plate, vehicleImages);
        vehicleList.add(vehicle);

        List<SOS> emergencyContactList = new ArrayList<>();
        if(!sosName.isEmpty() || !sosPhone.isEmpty()) {
            SOS emergencyContacts = new SOS(sosName, sosPhone);
            emergencyContactList.add(emergencyContacts);
        }

        Gender userGender = Gender.valueOf(gender);
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        compositeDisposable.add(stripeServices.createCustomer(
                        email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(customer -> {
                    if (customer.getId() == null) {
                        Log.d("Create customer on Stripe", "Try again");
                    } else {
                        stripeCusID = customer.getId();
                        Log.d("Create customer on Stripe", "===> customerID : " + customer.getId());
                        User user = new User(email, name, userGender, phoneNumber, homeList, vehicleList, emergencyContactList, stripeCusID);
                        firebaseManager.addUser(userID, user, new FirebaseManager.OnTaskCompleteListener() {
                            @Override
                            public void onTaskSuccess(String message) {
                                hideLoadingDialog(progressDialog);
                                showToast(requireContext(),message);
                                startActivity(new Intent(requireContext(), LoginActivity.class).putExtra("email", email).putExtra("password", password));
                                requireActivity().finish();
                            }

                            @Override
                            public void onTaskFailure(String message) {
                                hideLoadingDialog(progressDialog);
                                showToast(requireContext(),message);
                            }
                        });
                    }
                }, throwable -> {
                    Log.d("Error", throwable.getMessage());
                })
        );
    }
}