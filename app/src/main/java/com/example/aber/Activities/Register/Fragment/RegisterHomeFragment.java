package com.example.aber.Activities.Register.Fragment;

import static com.example.aber.Utils.AndroidUtil.showToast;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.example.aber.Activities.Main.Fragment.Home.ConfirmBookingFragment;
import com.example.aber.FirebaseManager;
import com.example.aber.Models.User.Home;
import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class RegisterHomeFragment extends Fragment implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String STORAGE_PATH = "home/";
    private Button doneButton;
    private String name, phoneNumber, gender;
    private TextView addressTextView;
    private ImageView homeImageView;
    private Bitmap cropped;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private PopupWindow popupWindow;
    private GoogleMap mMap;
    private Marker searchedLocation;
    private View root;

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
        progressDialog = new ProgressDialog(requireContext());
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_register_home, container, false);
        firebaseManager = new FirebaseManager();

        Bundle args = getArguments();
        if (args != null) {
            name = args.getString("name", "");
            phoneNumber = args.getString("phoneNumber", "");
            gender = args.getString("gender", "");
        }

        doneButton = root.findViewById(R.id.done_button);
        addressTextView = root.findViewById(R.id.address_edit_text);
        homeImageView = root.findViewById(R.id.home_image);

        addressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow();
                popupWindow.showAsDropDown(root, 0, 0);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addressTextView.getText().toString();
                if (validateInputs(address) && cropped != null) {
                    AndroidUtil.showLoadingDialog(progressDialog);
                    // Handle the case when only the avatar is changed
                    String imagePath = STORAGE_PATH + generateUniquePath() + ".jpg";
                    firebaseManager.uploadImage(cropped, imagePath, new FirebaseManager.OnTaskCompleteListener() {
                        @Override
                        public void onTaskSuccess(String message) {
                            AndroidUtil.hideLoadingDialog(progressDialog);
                            showToast(requireContext(),"Finish Step 2/5");
                            toRegisterVehicleFragment(address, imagePath);
                        }

                        @Override
                        public void onTaskFailure(String message) {
                            AndroidUtil.hideLoadingDialog(progressDialog);
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
            addressTextView.setError("Address cannot be empty");
            showToast(requireContext(),"Address can not be empty");
            return false;
        }
        return true;
    }

    public void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_map, null);

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);

        popupView.setBackgroundColor(getResources().getColor(R.color.popup_background, null));

        SupportMapFragment mapFragment = (SupportMapFragment) requireActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        TextView addressTextView2 = popupView.findViewById(R.id.address_edit_text);
        Button selectButton = popupView.findViewById(R.id.select_button);
        ImageView cancelBtn = popupView.findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAsDropDown(root, 0, 0);
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Enable the "My Location" button and display the blue dot on the map
            mMap.setMyLocationEnabled(true);

            // Set the "My Location" button to be visible
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Your existing code for setting up the map and markers...
        } else {
            // If permissions are not granted, request them
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng).title("Home");

                // Remove the previous selected location
                if (searchedLocation != null) {
                    searchedLocation.remove();
                }
                searchedLocation = mMap.addMarker(markerOptions);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                searchedLocation.showInfoWindow();
            }
        });
    }
}