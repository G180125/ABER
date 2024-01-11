package com.example.aber.Activities.Main.Fragment.Profile.Edit;

import static com.example.aber.Utils.AndroidUtil.hideLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.replaceFragment;
import static com.example.aber.Utils.AndroidUtil.showToast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.InflateException;
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
import com.example.aber.Adapters.UserHomeAdapter;
import com.example.aber.FirebaseManager;
import com.example.aber.Models.User.Home;
import com.example.aber.Models.User.User;
import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HomeListFragment extends Fragment implements UserHomeAdapter.RecyclerViewClickListener, OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String STORAGE_PATH = "home/";
    private ImageView buttonBack;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private String id, previous, name, address, imagePath, newAddress;
    private User user;
    private List<Home> homeList;
    private UserHomeAdapter adapter;
    private PopupWindow popupWindow1, popupWindow2;
    private Button addButton;
    private View root;
    private TextView addressTextView,  addressTextView2;
    private Bitmap cropped;
    private ImageView homeImageView;
    private Marker searchedLocation;
    private GoogleMap mMap;
    private double latitude, longitude;
    private Home selectedHome;
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
        root = inflater.inflate(R.layout.fragment_home_list, container, false);
        firebaseManager = new FirebaseManager();

        Bundle args = getArguments();
        if (args != null) {
            previous = args.getString("previous","");
            name = args.getString("name","");
            address = args.getString("address","");
        }

        id = Objects.requireNonNull(firebaseManager.mAuth.getCurrentUser()).getUid();
        firebaseManager.getUserByID(id, new FirebaseManager.OnFetchListener<User>() {
            @Override
            public void onFetchSuccess(User object) {
                user = object;
                homeList = user.getHomes();
                updateUI(homeList);
            }

            @Override
            public void onFetchFailure(String message) {

            }
        });

        buttonBack = root.findViewById(R.id.buttonBack);
        addButton = root.findViewById(R.id.add_button);

        RecyclerView recyclerView = root.findViewById(R.id.addressRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserHomeAdapter(new ArrayList<>(),this);
        recyclerView.setAdapter(adapter);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if(previous.equals("Profile Edit")) {
                    replaceFragment(new ProfileEditFragment(), fragmentManager, fragmentTransaction, R.id.fragment_main_container);
                } else if (previous.equals("Confirm Booking")){
                    ConfirmBookingFragment fragment = new ConfirmBookingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("address", address);
                    fragment.setArguments(bundle);

                    replaceFragment(fragment, fragmentManager, fragmentTransaction, R.id.fragment_main_container);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow(null, "Enter Additional Home", 0);
                popupWindow1.showAsDropDown(root, 0, 0);
            }
        });

        return root;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI(List<Home> homeList){
        AndroidUtil.showLoadingDialog(progressDialog);
        adapter.setHomeList(homeList);
        adapter.notifyDataSetChanged();
        AndroidUtil.hideLoadingDialog(progressDialog);
    }

    @Override
    public void onSetDefaultButtonClick(int position) {
        if (position > 0 && position < homeList.size()) {
            AndroidUtil.showLoadingDialog(progressDialog);
            Home selectedHome = homeList.get(position);
            homeList.remove(position);
            homeList.add(0, selectedHome);
            updateList(user, homeList,"This Home is set to default." );
        } else {
            AndroidUtil.showToast(getContext(), "Error! Please Try Again.");
        }

    }

    @Override
    public void onEditButtonClicked(int position) {
        Home home = homeList.get(position);
        initPopupWindow(home, "Edit Home", position);
        popupWindow1.showAsDropDown(root, 0, 0);
        updateUI(homeList);
    }

    @Override
    public void onDeleteButtonClicked(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this home?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        homeList.remove(position);
                        updateList(user, homeList, "Delete Home Successful");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked No, do nothing
                    }
                })
                .show();
    }



    public void initPopupWindow(Home home, String title, int position) {
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_address_form, null);

        popupWindow1 = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow1.setTouchable(true);
        // Set the background color with alpha transparency
        popupView.setBackgroundColor(getResources().getColor(R.color.popup_background, null));

        homeImageView = popupView.findViewById(R.id.home_image);
        TextView titleTextView = popupView.findViewById(R.id.title);
        addressTextView = popupView.findViewById(R.id.address_text_view);
        Button submitButton = popupView.findViewById(R.id.submitNewAddressBtn);
        ImageView cancelBtn = popupView.findViewById(R.id.cancelBtn);

        titleTextView.setText(title);

        addressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedHome = home;
                initAddressPopupWindow(selectedHome);
                popupWindow2.showAsDropDown(root, 0, 0);
            }
        });

        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        if (home != null) {
            addressTextView.setText(home.getAddress());

            if(home.getImage() != null) {
                firebaseManager.retrieveImage(home.getImage(), new FirebaseManager.OnRetrieveImageListener() {
                    @Override
                    public void onRetrieveImageSuccess(Bitmap bitmap) {
                        homeImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onRetrieveImageFailure(String message) {

                    }
                });
            }
        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow1.dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.showLoadingDialog(progressDialog);

                String address = addressTextView.getText().toString();

                if(title.equals("Edit Home")){
                    handeEDit(address, cropped, home, position);
                }

                if(title.equals("Enter Additional Home")){
                    handleAdd(address, cropped);
                }
            }
        });

        popupWindow1.showAsDropDown(root, 0, 0);
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

    private void updateList(User user, List<Home> homeList, String successMessage){
        user.setHomes(homeList);
        firebaseManager.updateUser(id, user, new FirebaseManager.OnTaskCompleteListener() {
            @Override
            public void onTaskSuccess(String message) {
                AndroidUtil.showToast(getContext(), successMessage);
                AndroidUtil.hideLoadingDialog(progressDialog);
                if (popupWindow1 != null) {
                    popupWindow1.dismiss();
                }
                updateUI(homeList);
            }

            @Override
            public void onTaskFailure(String message) {
                AndroidUtil.showToast(getContext(), message);
                AndroidUtil.hideLoadingDialog(progressDialog);
            }
        });
    }

    private boolean isDataSelected(String address, Bitmap cropped){
        if(address == null || address.isEmpty()){
            showToast(requireContext(), "You haven't select an address");
        }
        if(cropped == null){
            showToast(requireContext(), "You haven't select an image");
        }
       return true;
    }

    private boolean isDataChanged(String address, Bitmap cropped, Home home){
        return !address.equals(home.getAddress()) || cropped != null;
    }

    private void handleAdd(String address, Bitmap cropped){
        //TODO: Check if thw address and image is selected
        if(isDataSelected(address, cropped)){
            imagePath = STORAGE_PATH + generateUniquePath() + ".jpg";
            firebaseManager.uploadImage(cropped, imagePath, new FirebaseManager.OnTaskCompleteListener() {
                @Override
                public void onTaskSuccess(String message) {
                    Home home = new Home(address, imagePath, latitude, longitude);
                    homeList.add(0, home);
                    updateList(user, homeList, "Add new home successful");
                }

                @Override
                public void onTaskFailure(String message) {
                    AndroidUtil.hideLoadingDialog(progressDialog);
                    showToast(requireContext(), "Upload Image failed");
                }
            });
        }
        AndroidUtil.hideLoadingDialog(progressDialog);
    }

    private void handeEDit(String address, Bitmap cropped, Home home, int position){
        if(address.isEmpty()){
            showToast(requireContext(),"Address can not be empty");
            AndroidUtil.hideLoadingDialog(progressDialog);
            return;
        }

        if(!isDataChanged(address, cropped, home)){
            showToast(requireContext(),"You haven't changed anything");
            AndroidUtil.hideLoadingDialog(progressDialog);
            return;
        }

        if(cropped != null){
            imagePath = STORAGE_PATH + generateUniquePath() + ".jpg";
            firebaseManager.uploadImage(cropped, imagePath, new FirebaseManager.OnTaskCompleteListener() {
                @Override
                public void onTaskSuccess(String message) {
                    home.setImage(imagePath);
                    home.setAddress(address);
                    home.setLatitude(latitude);
                    home.setLongitude(longitude);
                    homeList.set(position, home);
                    updateList(user, homeList, "Update Successful");
                }

                @Override
                public void onTaskFailure(String message) {
                    AndroidUtil.hideLoadingDialog(progressDialog);
                    showToast(requireContext(), "Upload Image failed");
                }
            });
        } else {
            home.setAddress(address);
            home.setLatitude(latitude);
            home.setLongitude(longitude);
            homeList.set(position, home);
            updateList(user, homeList, "Update Successful");
        }
    }

    public void initAddressPopupWindow(Home home) {
        try {
            LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.pop_up_map, null);

            popupWindow2 = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            popupWindow2.setTouchable(true);

            popupView.setBackgroundColor(getResources().getColor(R.color.popup_background, null));

            SupportMapFragment mapFragment = (SupportMapFragment) requireActivity().getSupportFragmentManager()
                    .findFragmentById(R.id.map1);

            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            addressTextView2 = popupView.findViewById(R.id.address_edit_text);
            Button selectButton = popupView.findViewById(R.id.select_button);
            ImageView cancelBtn = popupView.findViewById(R.id.cancelBtn);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow2.dismiss();
                }
            });

            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(address != null){
                        addressTextView.setText(address);
                        popupWindow2.dismiss();
                    } else {
                        showToast(requireContext(), "You haven't select an address");
                    }
                }
            });
        } catch (InflateException e) {

        }
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

        clearMarkerAndAddress();

        if (selectedHome != null && selectedHome.getLatitude() != 0.0 && selectedHome.getLongitude() != 0.0) {
            // If home location is available, add a marker for the home
            LatLng homeLatLng = new LatLng(selectedHome.getLatitude(), selectedHome.getLongitude());
            MarkerOptions homeMarkerOptions = new MarkerOptions()
                    .position(homeLatLng)
                    .title("Home");
            searchedLocation = mMap.addMarker(homeMarkerOptions);

            // Move the camera to the home's location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(homeLatLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(homeLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            // Set the addressTextView2 with the home's address
            addressTextView2.setText(selectedHome.getAddress());
        }

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
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

                latitude = latLng.latitude;
                longitude = latLng.longitude;
                if (latLng != null) {
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        String dis = addresses.get(0).getSubAdminArea();

                        addressTextView2.setText(address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void clearMarkerAndAddress() {
        if (searchedLocation != null) {
            searchedLocation.remove();
            searchedLocation = null; // Set searchedLocation to null after removing
        }
        addressTextView2.setText(""); // Clear the address text
        address = null; // Set the address to null
        latitude = 0.0; // Reset latitude
        longitude = 0.0; // Reset longitude
    }
}
