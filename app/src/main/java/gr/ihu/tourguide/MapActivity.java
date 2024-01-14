package gr.ihu.tourguide;

import android.graphics.Color;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;

import android.Manifest;

import org.json.JSONException;
import org.json.JSONObject;

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

    private TextView textViewDistance;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        textViewDistance = findViewById(R.id.textViewDistance);
        mSearchText = (EditText) findViewById(R.id.input_search);
        mGps = (ImageView)  findViewById(R.id.ic_gps);
        getLocationPermission();
        Places.initialize(getApplicationContext(), "AIzaSyCl6nj0F5etwgSVzSHvo8WTO0aClG3b9XE");
        profileButton = findViewById(R.id.button_profile);
        detailsButton = findViewById(R.id.ic_information);
        mAuth = FirebaseAuth.getInstance();
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);

            }
        });
        init();
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
                modifiedText =place.getName();
                geoLocate();
                LatLng selectedPlaceLatLng = place.getLatLng();
                if (selectedPlaceLatLng != null) {
                    moveCamera(selectedPlaceLatLng, DEFAULT_ZOOM, place.getName());
                    Log.i(TAG, "Place details: " + place.getName() + ", " + selectedPlaceLatLng);
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


    private void geoLocate() {
        String searchString = modifiedText;
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException:" + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            LatLng destinationLatLng = new LatLng(address.getLatitude(), address.getLongitude());
            LatLng originLatLng = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());

            String directionsUrl = getDirectionsUrl(originLatLng, destinationLatLng);
            FetchDirectionsTask fetchDirectionsTask = new FetchDirectionsTask();
            fetchDirectionsTask.execute(directionsUrl);

            moveCamera(destinationLatLng, DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }
    private class FetchDirectionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            try {
                FetchUrlHelper fetchUrlHelper = new FetchUrlHelper();
                return fetchUrlHelper.downloadUrl(url[0]);
            } catch (Exception e) {
                return "Exception: " + e.getMessage();
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!result.startsWith("Exception")) {
                DirectionsJSONParser directionsParser = new DirectionsJSONParser();
                List<List<HashMap<String, String>>> routes = null;
                try {
                    routes = directionsParser.parse(new JSONObject(result));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                for (List<HashMap<String, String>> path : routes) {
                    PolylineOptions lineOptions = new PolylineOptions();
                    double totalDistance = 0.0;

                    for (int i = 0; i < path.size(); i++) {
                        HashMap<String, String> point = path.get(i);

                        if (i == 0) {
                            // Start marker
                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(position).title("Start"));
                        } else if (i == path.size() - 1) {
                            // End marker
                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(position).title("End"));
                        }

                        if (i > 0) {
                            // Calculate distance between consecutive points
                            HashMap<String, String> prevPoint = path.get(i - 1);
                            totalDistance += distance(
                                    Double.parseDouble(prevPoint.get("lat")),
                                    Double.parseDouble(prevPoint.get("lng")),
                                    Double.parseDouble(point.get("lat")),
                                    Double.parseDouble(point.get("lng"))
                            );
                        }

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        lineOptions.add(position);
                    }

                    lineOptions.width(10);
                    lineOptions.color(Color.BLUE);
                    mMap.addPolyline(lineOptions);

                    // Display the total distance in kilometers
                    String distanceText = "Total Distance: " + String.format("%.2f", totalDistance) + " km";
                    textViewDistance.setText(distanceText);
                    Toast.makeText(MapActivity.this, distanceText, Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Error downloading directions data: " + result);
            }
        }


        // Helper method to calculate distance between two points in kilometers
        private double distance(double lat1, double lon1, double lat2, double lon2) {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344; // Convert distance from miles to kilometers
            return dist;
        }
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        return "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + dest.latitude + "," + dest.longitude +
                "&key=AIzaSyCl6nj0F5etwgSVzSHvo8WTO0aClG3b9XE";
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
