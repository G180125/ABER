package com.example.aber.Activities.Main.Fragment.Home;

import static com.example.aber.Utils.AndroidUtil.hideLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.showLoadingDialog;
import static com.example.aber.Utils.AndroidUtil.showToast;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.aber.Adapters.InfoWindowViewHolder;
import com.example.aber.FirebaseManager;

import android.Manifest;

import android.widget.Toast;
import android.widget.PopupMenu;

import com.example.aber.R;
import com.example.aber.Utils.AndroidUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
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

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainHomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, RoutingListener {

    private static final String API_KEY = "AIzaSyAk79eOlfksqlm74wCmRbY_yddK75iZ4dM";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseManager firebaseManager;
    private ProgressDialog progressDialog;
    private SearchView searchView;
    private LatLng currentLocation;
    private Marker searchedLocation;
    private Place searchedPlace;
    private LocationRequest mLocationRequest;

    private FloatingActionButton mapTypeButton, currentLocationButton;
    private LatLng start;
    private LatLng end;
    private List<Polyline> polylines;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(requireContext());
        showLoadingDialog(progressDialog);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main_home, container, false);
        firebaseManager = new FirebaseManager();

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

                    // TODO: Display the custom info window on top of the marker
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
                        String address = addresses.get(0).getAddressLine(0);
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
                bundle.putString("address", "Testing address");
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AndroidUtil.replaceFragment(fragment, fragmentManager, fragmentTransaction, R.id.fragment_main_container);
            }
        });

        getCurrentLocation();
        hideLoadingDialog(progressDialog);

        startLocationUpdate();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng).title("Test");

                // Remove the previous selected location
                if (searchedLocation != null) {
                    searchedLocation.remove();
                }
                searchedLocation = mMap.addMarker(markerOptions);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                searchedLocation.showInfoWindow();

                end=searchedLocation.getPosition();
                startLocationUpdate();
                Findroutes(start, end);
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
                start = latLng;
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

    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End) {
        if(Start==null || End==null) {
            Toast.makeText(getActivity(),"Unable to get location",Toast.LENGTH_LONG).show();
        }
        else
        {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener((RoutingListener) MainHomeFragment.this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(getString(R.string.GOOGLE_MAP_API))  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = getActivity().findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(getActivity(),"Finding Route...",Toast.LENGTH_LONG).show();
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            for (Polyline polyline : polylines) {
                polyline.remove();
            }
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.pale_blue));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);

            }

        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(start,end);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(start,end);
    }
}
