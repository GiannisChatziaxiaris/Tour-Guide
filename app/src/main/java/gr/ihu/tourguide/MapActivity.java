package gr.ihu.tourguide;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private PlacesClient placesClient;
    private LatLng lastClickedLatLng;
    private TextView placeDetailsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapContainer);
        mapFragment.getMapAsync(this);

        // Initialize PlacesClient in onCreate
        Places.initialize(getApplicationContext(), "AIzaSyCl6nj0F5etwgSVzSHvo8WTO0aClG3b9XE");
        placesClient = Places.createClient(this);

        // Initialize TextView
        placeDetailsTextView = findViewById(R.id.placeDetailsTextView);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;
        LatLng mapIndia = new LatLng(41.08639320519534, 23.547690129684703);
        this.gMap.addMarker(new MarkerOptions().position(mapIndia).title("Marker in Serres"));
        this.gMap.moveCamera(CameraUpdateFactory.newLatLng(mapIndia));

        // Set the map click listener inside onMapReady
        this.gMap.setOnMapClickListener(latLng -> {
            Log.d("MapActivity", "onMapClick called");
            lastClickedLatLng = latLng;
            getPlaceDetails(lastClickedLatLng);
        });
    }

    private void getPlaceDetails(LatLng latLng) {
        Log.d("MapActivity", "getPlaceDetails called");

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.PHOTO_METADATAS,
                Place.Field.TYPES,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL
        );

        // Use FindCurrentPlaceRequest to get place details
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
        placeResponse.addOnSuccessListener(response -> {
            // Handle the successful response


            Log.d("MapActivity", "Place API response: " + response);


            List<PlaceLikelihood> placeLikelihoods = response.getPlaceLikelihoods();
            if (!placeLikelihoods.isEmpty()) {
                // Get details of the most likely place

                Place place = placeLikelihoods.get(0).getPlace();

                // Update the TextView with place details
                String placeName = place.getName();
                String placeAddress = place.getAddress();
                String details = "Name: " + placeName + "\nAddress: " + placeAddress;
                placeDetailsTextView.setText(details);

                // Log place details for debugging
                Log.d("MapActivity", "Place details retrieved: " + place.getName());
                Log.d("MapActivity", "Place Address: " + place.getAddress());
                Log.d("MapActivity", "Place LatLng: " + place.getLatLng());
                Log.d("MapActivity", "Place Types: " + place.getTypes());
                Log.d("MapActivity", "Place Rating: " + place.getRating());
                Log.d("MapActivity", "Place User Ratings Total: " + place.getUserRatingsTotal());
            } else {
                Log.e("MapActivity", "No place details found.");
            }
        }).addOnFailureListener(exception -> {
            // Handle errors
            Log.e("MapActivity", "Error fetching place details: " + exception.getMessage());
            exception.printStackTrace(); // Print the stack trace
        });
    }
}
