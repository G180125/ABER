package com.example.aber.Activities.Main.Fragment.Profile;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.example.aber.FirebaseManager;
import com.example.aber.Models.Gender;
import com.example.aber.Models.Home;
import com.example.aber.Models.SOS;
import com.example.aber.Models.User;
import com.example.aber.Models.Vehicle;
import com.example.aber.R;

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
    private EditText nameEditText, emailEditText, phoneEditText, addressEditText, plateEditText, sosEditText;
    private ImageView backImageView, homeImageView, vehicleImageView, sosImageView;
    private RadioButton maleRadioButton, femaleRadiusButton;
    private CircleImageView avatar;
    private Button uploadButton, editButton;
    private Bitmap cropped;
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
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_profile_edit, container, false);
        firebaseManager = new FirebaseManager();

        userID = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(userID, new FirebaseManager.OnFetchUserListener() {
            @Override
            public void onFetchUserSuccess(User user) {
                originalUser = user;
                currentUser = user;
                updateUI(currentUser);
            }

            @Override
            public void onFetchUserFailure(String message) {
                hideLoadingDialog();
                showToast(message);
            }
        });

        backImageView = root.findViewById(R.id.back);
        avatar = root.findViewById(R.id.avatar);
        uploadButton = root.findViewById(R.id.upload_button);
        nameEditText = root.findViewById(R.id.name);
        emailEditText = root.findViewById(R.id.email);
        maleRadioButton = root.findViewById(R.id.radioButtonMale);
        femaleRadiusButton = root.findViewById(R.id.radioButtonFemale);
        phoneEditText = root.findViewById(R.id.phone);
        addressEditText = root.findViewById(R.id.address);
        plateEditText = root.findViewById(R.id.plate);
        sosEditText = root.findViewById(R.id.sos_name);
        editButton = root.findViewById(R.id.edit_button);

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

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                if(!isProfileChanged(currentUser, originalUser)){
                    if (cropped != null) {
                        String imagePath = STORAGE_PATH + generateUniquePath() + ".jpg";
                        firebaseManager.uploadImage(cropped, imagePath, new FirebaseManager.OnTaskCompleteListener() {
                            @Override
                            public void onTaskSuccess(String message) {
                                currentUser.setAvatar(message);
                                firebaseManager.updateUser(userID, currentUser, new FirebaseManager.OnTaskCompleteListener() {
                                    @Override
                                    public void onTaskSuccess(String message) {
                                        showToast(message);
                                        updateUI(currentUser);
                                    }

                                    @Override
                                    public void onTaskFailure(String message) {
                                        showToast(message);
                                        hideLoadingDialog();
                                    }
                                });
                            }

                            @Override
                            public void onTaskFailure(String message) {
                                showToast(message);
                                hideLoadingDialog();
                            }
                        });
                    } else {
                        firebaseManager.updateUser(userID, currentUser, new FirebaseManager.OnTaskCompleteListener() {
                            @Override
                            public void onTaskSuccess(String message) {
                                showToast(message);
                                updateUI(currentUser);
                                hideLoadingDialog();
                            }

                            @Override
                            public void onTaskFailure(String message) {
                                showToast(message);
                                hideLoadingDialog();
                            }
                        });
                    }
                } else {
                    hideLoadingDialog();
                    showToast("No Data Changed.");
                }
            }
        });

        return root;
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

    private void updateUI(User user){
        if(!user.getAvatar().isEmpty()){
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
        emailEditText.setText(user.getEmail());
        setGenderFromRadiusButton(user);
        phoneEditText.setText(user.getPhoneNumber());
        addressEditText.setText(user.getHome().getAddress());
        plateEditText.setText(user.getVehicle().getNumberPlate());
        if(!user.getEmergencyContacts().isEmpty()){
            sosEditText.setText(user.getEmergencyContacts().get(0).getName());
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

    private boolean isProfileChanged(User currentUser, User originalUser) {
        currentUser.setName(nameEditText.getText().toString());
        currentUser.setEmail(emailEditText.getText().toString());
        currentUser.setGender(getGenderFromRadiusButton());
        currentUser.setPhoneNumber(phoneEditText.getText().toString());

        Home newHome = currentUser.getHome();
        newHome.setAddress(addressEditText.getText().toString());
        currentUser.setHome(newHome);

        Vehicle newVehicle = currentUser.getVehicle();
        newVehicle.setNumberPlate(plateEditText.getText().toString());
        currentUser.setVehicle(newVehicle);

        List<SOS> newList = new ArrayList<>();
        if(!sosEditText.getText().toString().isEmpty()){
            SOS newSOS = new SOS(sosEditText.getText().toString(), "");
            newList.add(newSOS);
        }
        currentUser.setEmergencyContacts(newList);
        if(cropped != null){
            return false;
        }

        return originalUser.equals(currentUser);
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