package com.example.aber.Activities.Main.Fragment;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import com.example.aber.FirebaseManager;
import android.Manifest;

import android.widget.Toast;
import android.widget.PopupMenu;

import com.example.aber.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainHomeFragment extends Fragment implements OnMapReadyCallback {
    private static final String API_KEY = "AIzaSyCYwy04EO7319zgEWLcfu7mxItQdPZM8Dw";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private SearchView searchView;
    private LatLng currentLocation;
    private Marker searchedLocation;

    private FloatingActionButton mapTypeButton,currentLocationButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        showLoadingDialog();
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main_home, container, false);
        firebaseManager = new FirebaseManager();

        // Initialize the SDK
        if(!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(requireContext(), API_KEY);
        }

        currentLocationButton = root.findViewById(R.id.current_location_button);

        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusOnLocation(currentLocation);
            }
        });

        mapTypeButton = root.findViewById(R.id.map_type_button);
        mapTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(requireContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.map_type_menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.buttonNormal){
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        Log.d("TAG", "Map Type : " + mMap.getMapType());
                        mMap.setIndoorEnabled(true);
                    } else if (item.getItemId() == R.id.buttonSatellite) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        Log.d("TAG", "Map Type : " + mMap.getMapType());
                        
                    } else if (item.getItemId() == R.id.buttonHybrid) {
                        mMap.setIndoorEnabled(false);
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        Log.d("TAG", "Map Type : " + mMap.getMapType());
                    }

                    return false;
                });
                popupMenu.show();
            }

        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocompleteSupportFragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    showLoadingDialog();
                    String id = place.getId();

                    //Remove the previous searched location
                    if (searchedLocation != null) {
                        searchedLocation.remove();
                    }

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(place.getLatLng()).title(place.getAddress());
                    searchedLocation = mMap.addMarker(markerOptions);

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                    searchedLocation.showInfoWindow();

                    hideLoadingDialog();

//                    requestManager.getPlaceDetails(id, API_KEY, new RequestManager.OnFetchDataListener() {
//                        @Override
//                        public void onFetchData(String response) {
//                            hideLoadingDialog();
//                        }
//
//                        @Override
//                        public void onError(String message) {
//                            hideLoadingDialog();
//                        }
//                    });
                }

                @Override
                public void onError(@NonNull Status status) {
                    showToast("Error: " + status);
                    Log.d("error", "Error: " + status);
                }
            });
        } else {
            showToast("AutocompleteFragment is null");
        }


        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        getCurrentLocation();
        hideLoadingDialog();
    }

    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    if (locationResult.getLastLocation() != null) {
                        currentLocation = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());

                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                        focusOnLocation(currentLocation);

                        fusedLocationClient.removeLocationUpdates(this);
                    }
                }
            }, null);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void focusOnLocation(LatLng location){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
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