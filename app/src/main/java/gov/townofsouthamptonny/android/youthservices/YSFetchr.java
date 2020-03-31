package gov.townofsouthamptonny.android.youthservices;

import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JDaly on 2/18/2016.
 */
public class YSFetchr {

    private static final String TAG = "YSFetchr";
    private static final String API_KEY = "apikey";
    private LocationManager mLocationManager;


    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException  {
        return new String(getUrlBytes(urlSpec));
    }

    public List<ServicesItem> fetchItems() {

        List<ServicesItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://gis.southamptontownny.gov/youthservices/getyouthservicesjson.ashx").toString();

            String jsonString = getUrlString(url);

            //Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items,jsonBody);
        } catch(JSONException je) {
            //Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe)  {
            //Log.e(TAG, "Failed to fetch items", ioe);
        }
        return items;
    }

    private void parseItems(List<ServicesItem> items, JSONObject jsonBody) throws IOException, JSONException {

        JSONObject servicesJsonObject = jsonBody.getJSONObject("services");
        JSONArray servicesJsonArray = servicesJsonObject.getJSONArray("service");

        for (int i = 0; i < servicesJsonArray.length(); i++)  {
            JSONObject servicesObject = servicesJsonArray.getJSONObject(i);

            ServicesItem item = new Gson().fromJson(servicesObject.toString(), ServicesItem.class);
            items.add(item);
        }

    }
}






