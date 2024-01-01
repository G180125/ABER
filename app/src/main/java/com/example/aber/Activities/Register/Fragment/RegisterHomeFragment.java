package com.example.aber.Activities.Register.Fragment;

import static com.example.aber.Utils.AndroidUtil.showToast;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
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

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.example.aber.FirebaseManager;
import com.example.aber.R;

public class RegisterHomeFragment extends Fragment {
    private static final String STORAGE_PATH = "home/";
    private Button doneButton;
    private String userID, email, password, name, phoneNumber, gender;
    private EditText addressEditText;
    private ImageView homeImageView;
    private Bitmap cropped;
    private FirebaseManager firebaseManager;

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
            updateHomeImage(cropped);
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_register_home, container, false);
        firebaseManager = new FirebaseManager();

        Bundle args = getArguments();
        if (args != null) {
            userID = args.getString("userID", "");
            email = args.getString("email", "");
            password = args.getString("password","");
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

                if (cropped != null && validateInputs(address)) {
                    // Handle the case when only the avatar is changed
                    String imagePath = STORAGE_PATH + generateUniquePath() + ".jpg";
                    firebaseManager.uploadImage(cropped, imagePath, new FirebaseManager.OnTaskCompleteListener() {
                        @Override
                        public void onTaskSuccess(String message) {
                            showToast(requireContext(),"Finish Step 3/5");
                            toRegisterVehicleFragment(address, imagePath);
                        }

                        @Override
                        public void onTaskFailure(String message) {
                            showToast(requireContext(), "Upload Image failed");
                        }
                    });
                }
            }
        });

        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
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

    private void updateHomeImage(Bitmap bitmap){
        homeImageView.setImageBitmap(bitmap);
    }

    private boolean validateInputs(String address){
        if(address.isEmpty()){
            showToast(requireContext(),"Address can not be empty");
            return false;
        }
        return true;
    }

    private void toRegisterVehicleFragment(String address, String homeImage){
        RegisterVehicleFragment fragment = new RegisterVehicleFragment();

        Bundle bundle = new Bundle();
        bundle.putString("userID", userID);
        bundle.putString("email", email);
        bundle.putString("password", password);
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
}