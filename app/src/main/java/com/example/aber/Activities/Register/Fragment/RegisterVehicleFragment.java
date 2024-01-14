package com.example.aber.Activities.Register.Fragment;

import static com.example.aber.Utils.AndroidUtil.showToast;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.example.aber.Utils.FirebaseUtil;
import com.example.aber.R;

public class RegisterVehicleFragment extends Fragment {
    private static final String STORAGE_PATH = "vehicle/";
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
    private ImageView vehicleImageView;
    private LinearLayout imageUploadSuccessLayout;
    private Bitmap cropped;
    private FirebaseUtil firebaseManager;
    private String vehicleImage;
    private double latitude, longitude;

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
            uploadImage();
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_register_vehicle, container, false);
        firebaseManager = new FirebaseUtil();

        Bundle args = getArguments();
        if (args != null) {
            userID = args.getString("userID", "");
            email = args.getString("email", "");
            password = args.getString("password","");
            name = args.getString("name", "");
            phoneNumber = args.getString("phoneNumber", "");
            gender = args.getString("gender", "");
            address = args.getString("address", "");
            latitude = args.getDouble("latitude");
            longitude = args.getDouble("longitude");
            homeImage = args.getString("homeImage", "");
        }

        vehicleBrandEditText = root.findViewById(R.id.vehicle_brand_edit_text);
        vehicleNameEditText = root.findViewById(R.id.vehicle_name_edit_text);
        vehicleColorEditText = root.findViewById(R.id.vehicle_color_edit_text);
        vehiclePlateEditText = root.findViewById(R.id.vehicle_number_plate_edit_text);
        imageUploadSuccessLayout = root.findViewById(R.id.img_upload_success);

        seatCapacitySpinner = root.findViewById(R.id.seat_capacity_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.seat_capacity_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seatCapacitySpinner.setAdapter(adapter);

        vehicleImageView = root.findViewById(R.id.vehicle_image_view);
        vehicleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

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

    private void uploadImage(){
        if (cropped != null) {
            // Handle the case when only the avatar is changed
            String imagePath = STORAGE_PATH + generateUniquePath() + ".jpg";
            firebaseManager.uploadImage(cropped, imagePath, new FirebaseUtil.OnTaskCompleteListener() {
                @Override
                public void onTaskSuccess(String message) {
                    showToast(requireContext(), "Upload Image success");
                    vehicleImage = message;
                    updateUI(vehicleImage);
                }

                @Override
                public void onTaskFailure(String message) {
                    showToast(requireContext(), "Upload Image failed");
                }
            });
        }
    }

    private void updateUI(String imagePath){

    }

    private boolean validateInputs(String brand, String name, String color, String selectedSeatCapacity, String plate) {
        StringBuilder errorMessage = new StringBuilder();

        if (brand.isEmpty()) {
            errorMessage.append("Vehicle Brand can not be empty\n");
            vehicleBrandEditText.setError("Vehicle Brand can not be empty");
        }

        if (name.isEmpty()) {
            errorMessage.append("Vehicle Name can not be empty\n");
            vehicleNameEditText.setError("Vehicle Name can not be empty");
        }

        if (color.isEmpty()) {
            errorMessage.append("Vehicle Color can not be empty\n");
            vehicleColorEditText.setError("Vehicle Color can not be empty");
        }

        if (selectedSeatCapacity.isEmpty()) {
            errorMessage.append("Vehicle Seat Capacity can not be empty\n");
        } else {
            try {
                int seatCapacity = Integer.parseInt(selectedSeatCapacity);
                if (seatCapacity <= 0) {
                    errorMessage.append("Invalid Seat Capacity\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Invalid Seat Capacity\n");
            }
        }

        if (!validatePlate(plate)) {
            errorMessage.append("Invalid Plate\n");
        }

        // Display error messages for each field
        if (errorMessage.length() > 0) {
            showToast(requireContext(), errorMessage.toString().trim()); // Trim to remove trailing newline
            return false;
        }

        showToast(requireContext(), "Finish Step 4/5");
        return true;
    }



    private boolean validatePlate(String plate) {
        String plateRegex = "^[0-9]{2}-[A-Z]\\s[0-9]{5}$";

        if (plate.matches(plateRegex)) {
            String provinceNumber = plate.substring(0, 2);
            if (isValidProvinceNumber(provinceNumber)) {
                return true;
            } else {
                vehiclePlateEditText.setError("Invalid province number");
                showToast(requireContext(),"Invalid province number");
                return false;
            }
        } else {
            vehiclePlateEditText.setError("Invalid plate format");
            showToast(requireContext(),"Invalid plate format");
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
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        bundle.putString("homeImage", homeImage);
        bundle.putString("brand", brand);
        bundle.putString("vehicleName", vehicleName);
        bundle.putString("color", color);
        bundle.putString("seat", seat);
        bundle.putString("plate", plate);
        bundle.putString("vehicleImage", vehicleImage);
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