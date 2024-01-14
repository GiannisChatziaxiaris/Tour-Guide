package gr.ihu.tourguide;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchUrlHelper {
    public String downloadUrl(String strUrl) throws Exception {
        String data = "";
        InputStream iStream;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            throw new Exception("Error downloading data. Please check your network connection.");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }
}