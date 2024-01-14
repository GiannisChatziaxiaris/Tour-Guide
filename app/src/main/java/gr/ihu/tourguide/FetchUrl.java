package gr.ihu.tourguide;

import android.content.Context;
import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;

public class FetchUrl extends AsyncTask<String, Void, String> {
    private Context context;
    private GoogleMap mMap;

    public FetchUrl(Context context, GoogleMap mMap) {
        this.context = context;
        this.mMap = mMap;
    }

    @Override
    protected String doInBackground(String... url) {
        // Fetch the data from the URL
        String data = "";
        try {
            FetchUrlHelper fetchUrlHelper = new FetchUrlHelper();
            data = fetchUrlHelper.downloadUrl(url[0]);
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Parse the data and draw the route on the map
        ParserTask parserTask = new ParserTask(context, mMap);
        parserTask.execute(result);
    }
}