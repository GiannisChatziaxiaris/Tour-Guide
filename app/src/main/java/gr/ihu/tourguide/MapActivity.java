package gr.ihu.tourguide;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.Manifest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMaoReady: map is ready");
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

            init();

        }
    }
    Button profileButton;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;



    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static final float DEFAULT_ZOOM =15f;

    private static final  LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40,-168), new LatLng(71, 136));



    //widgets
    private EditText mSearchText;
    private ImageView mGps;
    private ImageView detailsButton;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private String modifiedText = "";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mSearchText = (EditText) findViewById(R.id.input_search);
        mGps = (ImageView)  findViewById(R.id.ic_gps);
        getLocationPermission();
        Places.initialize(getApplicationContext(), "AIzaSyCl6nj0F5etwgSVzSHvo8WTO0aClG3b9XE");
        profileButton = findViewById(R.id.button_profile);
        detailsButton = findViewById(R.id.ic_information);
        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance("https://project-database-42bd5-default-rtdb.europe-west1.firebasedatabase.app/");
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);

            }
        });

    }

    private void init(){
        Log.d(TAG,"init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                modifiedText=place.getName();
                LatLng selectedPlaceLatLng = place.getLatLng();
                if (selectedPlaceLatLng != null) {
                    moveCamera(selectedPlaceLatLng, DEFAULT_ZOOM, place.getName());
                    Log.i(TAG, "Place details: " + place.getName() + ", " + selectedPlaceLatLng);
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference searchHistoryRef = mDatabase.getReference().child("search_history").child(userID);
                    // Push the searched place to Firebase
                    searchHistoryRef.push().setValue(modifiedText);
                } else {
                    Log.e(TAG, "onPlaceSelected: LatLng object is null for place " + place.getName());
                    // Handle the case where LatLng is null (e.g., show a message to the user)
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });
        ImageView magnifyIcon = findViewById(R.id.ic_magnify);
        magnifyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from the EditText
                String searchText = mSearchText.getText().toString();

                // Replace spaces with underscores for wikipedia api
                modifiedText = searchText.replace(" ", "_");
                Log.d("searchTEXT", searchText);
                geoLocate();
            }
        });
        detailsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent(MapActivity.this, LocationDetailActivity.class);
                intent.putExtra("locationKeyword", modifiedText); // Replace with your keyword
                startActivity(intent);
            }

        });
        hideSoftKeyboard();
    }
    private void geoLocate(){
        Log.d(TAG," geolocate: geolocating");
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference searchHistoryRef = mDatabase.getReference().child("search_history").child(userID);
        // Push the searched place to Firebase
        searchHistoryRef.push().setValue(searchString);
        try {
            list = geocoder.getFromLocationName(searchString,1);

        }catch(IOException e){
            Log.e(TAG,"geoLocate: IOExpection:" + e.getMessage() );
        }
        if(list.size()>0){
            Address address = list.get(0);
            Log.d(TAG, "geolocate: found a location: " +address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM ,
                    address.getAddressLine(0));

        }
    }


    private void getDeviceLocation(){
        Log.d(TAG,"getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");


                        }else{
                            Log.d(TAG, " onComplete: current location is Null");
                            Toast.makeText(MapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }catch (SecurityException e){
            Log.e(TAG,"getDeviceLocation: SecurityException:" + e.getMessage());
        }
    }
    private void moveCamera(LatLng latLng,float zoom,String title) {
        if (latLng == null) {
            Log.e(TAG, "moveCamera: LatLng object is null");
            return;
        }
        if (latLng != null) {
            Log.d(TAG, "moveCameraL moving the camera to: lat:" + latLng.latitude + ",lng:" + latLng.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

            if(!title.equals("My Location")) {
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(title);
                mMap.addMarker(options);
            }
            hideSoftKeyboard();
        } else {
            Log.e(TAG, "moveCamera: LatLng object is null");
        }
    }

    private void initMap(){
        Log.d(TAG, "initMap: Initializing map ");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }


    private void getLocationPermission() {
        Log.d(TAG,"getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG,"onRequestPermissionsResult: called");
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG,"onRequestPermissionsResult: permission granted");
                            return;
                        }
                    }
                    Log.d(TAG,"onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }

            }
        }
    }
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


}

