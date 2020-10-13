package com.stepapp.step;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.annotations.NotNull;
import com.stepapp.step.databinding.FragmentMapBinding;
import com.stepapp.step.dialogs.PlaceViewDialog;
import com.stepapp.step.interfaces.IMapFragment;

import java.util.Arrays;

public class MapFragment extends Fragment implements IMapFragment, OnMapReadyCallback {
    public static final int PERMISSIONS_CODE = 2;
    private String TAG = getClass().getName();
    private FragmentMapBinding mBinding;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private GoogleMap map;
    private PlacesClient placesClient;
    private boolean firstTimeLocFound = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(this.TAG, "onCreateView...");
        FragmentMapBinding inflate = FragmentMapBinding.inflate(inflater);
        this.mBinding = inflate;

        placesClient = Places.createClient(getContext());
        initAutoCompleteFrag();

        return inflate.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapView);

        if (mapFragment != null)
            mapFragment.getMapAsync(this);

    }

    void initAutoCompleteFrag(){
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setCountry("za");

        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                goToPlacesFrag(place.getName(),
                        place.getAddress(), place.getId(), place.getLatLng());
            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    void initLocationServices(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    void goToPlacesFrag(String title, String address, String id, LatLng latLng){
        Bundle bundle =  new Bundle();

        bundle.putString("title", title);
        bundle.putString("address", address);
        bundle.putString("id", id);
//        bundle.putDouble("lat", latLng.latitude);
//        bundle.putDouble("lng", latLng.longitude);

        NavHostFragment.findNavController(this).navigate(R.id.action_mapFragment_to_placeViewDialog, bundle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    void accessLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

            initLocationServices();

            getLocationUpdates();

            if (map != null) {
                map.setMyLocationEnabled(true);
            }

        } else  {
            requestPermission();
        }
    }

    @SuppressLint("MissingPermission")
    void getLocationUpdates(){
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.i(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());
                    lastKnownLocation = location;

                    // get location updates
                    LocationRequest locationReq = new LocationRequest();
                    locationReq.setExpirationDuration(1000 * 60 * 60 * 30); //30 minuets
                    locationReq.setInterval(1000 * 60 * 3); // 3 minuets

                    LocationCallback locationCallBack = new LocationCallback(){
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult == null) return;

                            for (Location l : locationResult.getLocations()){
                                if (map != null && !firstTimeLocFound){
                                    map.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(new LatLng(l.getLatitude(), l.getLongitude()), 13));

                                    firstTimeLocFound = true;
                                }
                            }
                        }
                    };

                    fusedLocationProviderClient.requestLocationUpdates(locationReq,
                            locationCallBack,
                            Looper.myLooper());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location is probably disabled
                    try {

                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(), 5);

                    } catch (IntentSender.SendIntentException sendEx) {
                        Log.e(TAG, sendEx.getMessage());
                    }
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 5){
            // settings changed
        }
    }

    void requestPermission(){
        String[] permissions = new String[1];
        permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;

        requestPermissions(permissions, PERMISSIONS_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
            accessLocation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        addSavedMarkers();

        accessLocation();
        map.getUiSettings().setCompassEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // add place
            }
        });

    }

    void addSavedMarkers(){
        LatLng sydney = new LatLng(-33.852, 151.211);
        map.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));
    }
}
