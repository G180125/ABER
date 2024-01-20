package com.example.aber.Activities.Main.Fragment.Home;

import static com.example.aber.Utils.AndroidUtil.hideLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.showLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.showToast;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.aber.Activities.Main.Fragment.Booking.BookingDetailFragment;
import com.example.aber.Activities.Main.Fragment.Booking.MainBookingFragment;
import com.example.aber.Adapters.InfoWindowViewHolder;


import android.Manifest;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.PopupMenu;

import com.example.aber.NotificationListFragment;
import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;
import com.example.aber.Utils.FirebaseUtil;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainHomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String API_KEY = "AIzaSyAk79eOlfksqlm74wCmRbY_yddK75iZ4dM";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public PopupWindow popupWindow;
    private GoogleMap mMap;

    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseUtil firebaseManager;
    private ProgressDialog progressDialog;
    private SearchView searchView;
    private LatLng currentLocation;
    private Marker searchedLocation;
    private Place searchedPlace;
    private LocationRequest mLocationRequest;
    private String address;

    private FloatingActionButton mapTypeButton, currentLocationButton, notificationButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        showLoadingDialog(progressDialog);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main_home, container, false);
        firebaseManager = new FirebaseUtil();

        // Initialize the SDK
        if (!Places.isInitialized()) {
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
                popupMenu.getMenuInflater().inflate(R.menu.map_type_menu, popupMenu.getMenu());
                popupMenu.setGravity(Gravity.END);

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.buttonNormal) {
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

        notificationButton = root.findViewById(R.id.notification_button);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AndroidUtil.replaceFragment(new NotificationListFragment(), fragmentManager, fragmentTransaction, R.id.fragment_main_container);
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
            View autocompleteView = autocompleteFragment.getView();
            if (autocompleteView != null) {
                autocompleteView.setBackgroundColor(getResources().getColor(android.R.color.white));
            }
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    searchedPlace = place;
                    showLoadingDialog(progressDialog);
                    String id = place.getId();

                    // Remove the previous searched location
                    if (searchedLocation != null) {
                        searchedLocation.remove();
                    }

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(place.getLatLng()).title(place.getAddress());
                    searchedLocation = mMap.addMarker(markerOptions);

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                    searchedLocation.showInfoWindow();

                    hideLoadingDialog(progressDialog);

                }

                @Override
                public void onError(@NonNull Status status) {
                    showToast(requireContext(), "Error: " + status);
                    Log.d("error", "Error: " + status);
                }
            });
        } else {
            showToast(requireContext(), "AutocompleteFragment is null");
        }

        return root;
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
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoContents(Marker marker) {
                return null; // Use default info window
            }

            @SuppressLint("RestrictedApi")
            @Override
            public View getInfoWindow(Marker marker) {
                // Create a custom info window layout
                View infoView = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                InfoWindowViewHolder viewHolder = new InfoWindowViewHolder(infoView);
                infoView.setClickable(true);
                Double lat = marker.getPosition().latitude;
                Double lng = marker.getPosition().longitude;
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                Log.d("Placeselected", lat + ", " + lng);
                if (lat != 0) {
                    try {
                        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                        address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        String dis = addresses.get(0).getSubAdminArea();

                        String searchedAddress = address;
                        assert searchedAddress != null;
                        viewHolder.bind(marker.getTitle(), searchedAddress);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return infoView;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                ConfirmBookingFragment fragment = new ConfirmBookingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", marker.getTitle());
                bundle.putString("address", address);

                LatLng markerPosition = marker.getPosition();
                bundle.putDouble("latitude", markerPosition.latitude);
                bundle.putDouble("longitude", markerPosition.longitude);
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AndroidUtil.replaceFragment(fragment, fragmentManager, fragmentTransaction, R.id.fragment_main_container);
            }
        });

        getCurrentLocation();
        hideLoadingDialog(progressDialog);

//        startLocationUpdate();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng).title("Unknown");

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

//                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
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


    private void focusOnLocation(LatLng location) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        String id = firebaseManager.mAuth.getCurrentUser().getUid();

        if (isLocationChanged(location, id)) {
            firebaseManager.updateCurrentLocation(location, getCurrentDateTime(), id);
        }
    }

    private void startLocationUpdate() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000); // 30s
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());

                focusOnLocation(latLng);
            }
        }, null);
    }

    public static String getCurrentDateTime() {
        Date currentDate = new Date();

        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());

        return simpleDateFormat.format(currentDate);
    }

    private boolean isLocationChanged(LatLng newLocation, String userId) {
        LatLng latestLocation = firebaseManager.getLatestLocation(userId);

        return latestLocation == null || !latestLocation.equals(newLocation);
    }

    public void initPopUpBookingSuccess() {
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_up_booking_success, null);

        // Initialize the PopupWindow
        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        popupWindow.setTouchable(true);
        // Set focusable to true to receive touch events outside the PopupWindow
        popupWindow.setFocusable(true);

        // Set background drawable to allow touch events outside the PopupWindow
        popupView.setBackgroundColor(getResources().getColor(R.color.popup_background, null));

        // Set up UI elements and event listeners within the popupView
        Button viewBookingButton = popupView.findViewById(R.id.view_booking_button);
        ImageView cancelBtn = popupView.findViewById(R.id.cancelBtn);
        viewBookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                AndroidUtil.replaceFragment(new MainBookingFragment(), fragmentManager, fragmentTransaction, R.id.fragment_main_container);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });


        // Show the PopupWindow
        popupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0);

    }
}
