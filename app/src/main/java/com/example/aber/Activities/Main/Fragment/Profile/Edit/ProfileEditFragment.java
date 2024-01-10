package com.example.aber.Activities.Main.Fragment.Profile.Edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.example.aber.Activities.Main.Fragment.Profile.MainProfileFragment;
import com.example.aber.FirebaseManager;

import com.example.aber.Models.User.Gender;
import com.example.aber.Models.User.SOS;
import com.example.aber.Models.User.User;
import com.example.aber.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditFragment extends Fragment {
    private static final String STORAGE_PATH = "avatar/";
    private String userID;
    private User currentUser, originalUser;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private EditText nameEditText, phoneEditText;
    private TextView addressTextView, vehicleTextView, sosTextView, emailTextView;
    private ImageView backImageView, homeImageView, vehicleImageView, sosImageView;
    private RadioButton maleRadioButton, femaleRadiusButton;
    private CircleImageView avatar;
    private MaterialButton uploadButton, editButton;
    private Bitmap cropped;
    private CardView editAddressCardView, editVehicleCardView, editSOSCardVIew;
    private final ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                launchImageCropper(imageUri);
            }
        }
    });

    private final ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            cropped = BitmapFactory.decodeFile(result.getUriFilePath(requireContext(), true));
            updateAvatar(cropped);
        }
    });

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        showLoadingDialog();

        View root =  inflater.inflate(R.layout.fragment_profile_edit, container, false);
        firebaseManager = new FirebaseManager();

        userID = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(userID, new FirebaseManager.OnFetchListener<User>() {
            @Override
            public void onFetchSuccess(User user) {
                currentUser = user;
                updateUI(currentUser);
            }

            @Override
            public void onFetchFailure(String message) {
                hideLoadingDialog();
                showToast(message);
            }
        });

        backImageView = root.findViewById(R.id.back);
        avatar = root.findViewById(R.id.avatar);
        uploadButton = root.findViewById(R.id.upload_button);
        nameEditText = root.findViewById(R.id.name);
        emailTextView = root.findViewById(R.id.email);
        maleRadioButton = root.findViewById(R.id.radioButtonMale);
        femaleRadiusButton = root.findViewById(R.id.radioButtonFemale);
        phoneEditText = root.findViewById(R.id.phone);
        addressTextView= root.findViewById(R.id.address);
        vehicleTextView = root.findViewById(R.id.vehicle);
        sosTextView = root.findViewById(R.id.sos_name);
        editButton = root.findViewById(R.id.edit_button);
        editAddressCardView = root.findViewById(R.id.defaultAddressCardView);
        editVehicleCardView = root.findViewById(R.id.defaultVehicleCardView);
        editSOSCardVIew = root.findViewById(R.id.defaultSOSCardView);

        editAddressCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                HomeListFragment fragment = new HomeListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("previous", "Profile Edit");
                fragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragment_main_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        editVehicleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                VehicleListFragment fragment = new VehicleListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("previous", "Profile Edit");
                fragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragment_main_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        editSOSCardVIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                SOSListFragment fragment = new SOSListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("previous", "Profile Edit");
                fragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragment_main_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                fragmentTransaction.replace(R.id.fragment_main_container, new MainProfileFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



        // Inside the editButton.setOnClickListener method in ProfileEditFragment
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                String name = nameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                Gender gender = getGenderFromRadiusButton();
                boolean avatarChanged = cropped != null;



                if (isProfileChanged(name, phone, gender, avatarChanged)) {

                    if (validatePhoneNumber(phone)) {
                        currentUser.setPhoneNumber(phone);
                        Log.d("After Validating", phone);
                    } else {
                        showToast("Invalid phone number format");
                        phoneEditText.setError("Please enter the correct format before editing!");
                        hideLoadingDialog();
                        return;
                    }
                    // Update user
                    currentUser.setName(name);
                    currentUser.setGender(gender);

                    if (avatarChanged) {
                        // If avatar changed, upload the new image
                        String imagePath = STORAGE_PATH + generateUniquePath() + ".jpg";
                        firebaseManager.uploadImage(cropped, imagePath, new FirebaseManager.OnTaskCompleteListener() {
                            @Override
                            public void onTaskSuccess(String message) {
                                currentUser.setAvatar(message);
//                                showToast("Image uploaded successfully. Avatar URL: " + message);

                                // Update the user with the new image URL
                                updateCurrentUserInFirestore();
                            }

                            @Override
                            public void onTaskFailure(String message) {
                                showToast("Upload Image Failure: " + message);
                                hideLoadingDialog();
                            }
                        });
                    } else {
                        // If no avatar change, update user without uploading image
                        updateCurrentUserInFirestore();
                        Log.d("Set Change final 2", currentUser.getPhoneNumber());
                    }
                } else {
                    showToast("You haven't changed anything");
                    hideLoadingDialog();
                }
            }
        });
        return root;
    }

    private void updateCurrentUserInFirestore() {
        Log.d("Set Change final", currentUser.getPhoneNumber());
        firebaseManager.updateUser(userID, currentUser, new FirebaseManager.OnTaskCompleteListener() {
            @Override
            public void onTaskSuccess(String message) {
                showToast("Update Successfully");
                hideLoadingDialog();
                updateUI(currentUser);
            }

            @Override
            public void onTaskFailure(String message) {
                showToast("Error updating user: " + message);
                hideLoadingDialog();
            }
        });
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll("\\s", "");

        // Check if the phone number matches the pattern "0\d{9}" or "84\d{9}"
        return phoneNumber.matches("0\\d{9}") || phoneNumber.matches("84\\d{9}");
    }

    private void launchImageCropper(Uri uri) {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = false;
        cropImageOptions.imageSourceIncludeCamera = true;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(uri, cropImageOptions);
        cropImage.launch(cropImageContractOptions);
    }

    private void selectImage() {
        getImageFile();
    }

    private void getImageFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        getImage.launch(intent);
    }

    private String generateUniquePath() {
        return String.valueOf(System.currentTimeMillis());
    }

    private void updateAvatar(Bitmap bitmap){
        avatar.setImageBitmap(bitmap);
    }

    private void updateUI(User user) {
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            firebaseManager.retrieveImage(user.getAvatar(), new FirebaseManager.OnRetrieveImageListener() {
                @Override
                public void onRetrieveImageSuccess(Bitmap bitmap) {
                    updateAvatar(bitmap);
                    hideLoadingDialog();
                }

                @Override
                public void onRetrieveImageFailure(String message) {
                    showToast(message);
                    hideLoadingDialog();
                }
            });
        } else {
            hideLoadingDialog();
        }


        nameEditText.setText(user.getName());
        emailTextView.setText(user.getEmail());
        setGenderFromRadiusButton(user);
        phoneEditText.setText(user.getPhoneNumber());
        addressTextView.setText(user.getHomes().get(0).getAddress());
        vehicleTextView.setText(user.getVehicles().get(0).getNumberPlate());
        if (!user.getEmergencyContacts().isEmpty()) {
            sosTextView.setText(user.getEmergencyContacts().get(0).getName());
        }

        if (!user.getEmergencyContacts().isEmpty()) {
            sosTextView.setText(user.getEmergencyContacts().get(0).getName());
        }
    }

    private void setGenderFromRadiusButton(User user){
        if (user.getGender() == Gender.MALE) {
            maleRadioButton.setChecked(true);
        } else if (user.getGender() == Gender.FEMALE) {
            femaleRadiusButton.setChecked(true);
        }
    }

    private Gender getGenderFromRadiusButton(){
        if (maleRadioButton.isChecked()) {
            return Gender.MALE;
        } else if (femaleRadiusButton.isChecked()) {
            return Gender.FEMALE;
        }
        return null;
    }

    // Inside the isProfileChanged method
    private boolean isProfileChanged(String name, String phone, Gender gender, boolean avatarChanged) {
        Log.d("ProfileEditFragment", "phone " + phone);
        Log.d("ProfileEditFragment", "user phone " + currentUser.getPhoneNumber());

        // Check if the phone number has changed
        boolean isPhoneNumberChanged = !phone.equals(currentUser.getPhoneNumber());

        return isPhoneNumberChanged || !name.equals(currentUser.getName()) || gender != currentUser.getGender() || avatarChanged;
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
        new Handler(Looper.getMainLooper()).post(() -> {
            if (isAdded() && getActivity() != null && !getActivity().isFinishing() && progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        });
    }




    private void showToast(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}