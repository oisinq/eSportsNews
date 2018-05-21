package io.oisin.esportsnews;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Oisin Quinn (@oisin1001) on 21/05/2018.
 */
public class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    public static ArrayList<Entry> fetchEntries(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        String jsonResponse = getQuery(url);

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        ArrayList<Entry> entries = extractEntries(jsonResponse);

        // Return the list of {@link Earthquake}s
        return entries;
    }

    private static ArrayList<Entry> extractEntries(String query) {
        ArrayList<Entry> entries = new ArrayList<>();

        try {
            JSONObject mainObject = new JSONObject(query);
            JSONObject detailsObject = mainObject.getJSONObject("response");
            JSONArray responses = detailsObject.getJSONArray("results");

            for (int i = 0; i < responses.length(); i++) {
                JSONObject entry = responses.getJSONObject(i);
                String url = entry.getString("webUrl");
                String title = entry.getString("webTitle");
                String section = entry.getString("sectionName");
                String date = entry.getString("webPublicationDate");
                JSONArray tags = entry.getJSONArray("tags");
                JSONObject authorObject = tags.getJSONObject(0);
                String authorName = authorObject.getString("webTitle");

                entries.add(new Entry(title, section, authorName, date, url));
            }
        } catch (JSONException e) {
            Log.v(LOG_TAG, "Error extracting JSON data");
        }

        return entries;
    }

    private static URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating URL object");
        }

        return url;
    }

    private static String getQuery(URL url) {
        String jsonResponse = "";
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);
            connection.connect();
            inputStream = connection.getInputStream();
            jsonResponse = readFromStream(inputStream);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting query from URL.");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Unable to close input stream from URL.");
                }
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
