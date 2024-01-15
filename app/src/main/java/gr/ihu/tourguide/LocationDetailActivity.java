package gr.ihu.tourguide;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;



public class LocationDetailActivity extends AppCompatActivity {

    private TextView locationDetailTextView;
    public LocationDetailActivity() {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        locationDetailTextView = findViewById(R.id.locationDetailTextView);

        // Receive the keyword from the calling activity
        String locationKeyword = getIntent().getStringExtra("locationKeyword");

        // Fetch and display information based on the keyword
        fetchLocationInformation(locationKeyword);
    }

    private void fetchLocationInformation(final String locationKeyword) {
        // Wikipedia API URL for extracting an extract (summary) based on a search term
        String apiUrl = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&titles=" + locationKeyword.replace(" ", "%20");

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse the JSON response to extract the extract (summary)
                        String summary = parseWikipediaResponse(response);

                        // Display the summary
                        if (summary.isEmpty() || summary.equals("No information available")) {
                            locationDetailTextView.setText("No information available for " + locationKeyword);
                        } else {
                            // Use Html.fromHtml to parse and display HTML content
                            locationDetailTextView.setText(HtmlCompat.fromHtml(summary, HtmlCompat.FROM_HTML_MODE_LEGACY));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors here
                        Log.e("VolleyError", "Error fetching information: " + error.getMessage());
                        locationDetailTextView.setText("Error fetching information");
                    }
                });

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }

    private String parseWikipediaResponse(String response) {
        try {
            // Print the raw response for inspection
            Log.d("WikipediaResponse", response);

            // Parse the JSON response to get the extract (summary)
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject query = jsonResponse.getJSONObject("query");
            JSONObject pages = query.getJSONObject("pages");

            // Assuming there is only one page in the response
            String firstPageKey = pages.keys().next();
            JSONObject page = pages.getJSONObject(firstPageKey);

            String extract = page.optString("extract", "No information available");

            return extract.trim(); // Trim to remove leading/trailing whitespaces
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error parsing response: " + e.getMessage();
        }
    }
    public void goBack(View view) {
        // Handle the button click event to go back to the parent activity
        finish(); // This will close the current activity and return to the parent activity
    }

}