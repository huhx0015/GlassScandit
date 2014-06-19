package com.mirasense.demos;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mirasense.scanditsdkdemo.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Created by Michael Yoon Huh on 6/13/2014.
 */
public class JsonParser extends Activity {

    /** CLASS VARIABLES _______________________________________________________**/

    // SERVER VARIABLES
    private static final String URL = "http://gpop-server.com/find-a-product/search.php?q=1234"; // Server URL

    /** ACTIVITY LIFECYCLE FUNCTIONALITY _____________________________________ **/

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // INITIALIZATION
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // SET LAYOUT FILE

        // INPUT URL
        InputStream source = retrieveStream(URL);

        // NEW GSON OBJECT
        Gson gson = new Gson();

        // NEW INPUT READER OBJECT THAT READS URL STREAM
        Reader reader = new InputStreamReader(source);

        // SEARCH RESPONSE
        SearchResponse response = gson.fromJson(reader, SearchResponse.class);

        // DISPLAYS TOAST OF THE RESPONSE
        Toast.makeText(this, response.query, Toast.LENGTH_SHORT).show();

        // SETS THE LIST OF RESULTS FROM THE RESPONSE
        List<Result> results = response.results;

        // FOR EACH RESULT, SHOW A TOAST FOR EACH STORE
        for (Result result : results) {
            Toast.makeText(this, result.storeName, Toast.LENGTH_SHORT).show();
        }
    }

    private InputStream retrieveStream(String URL) {

        DefaultHttpClient client = new DefaultHttpClient();

        HttpGet getRequest = new HttpGet(URL);

        try {

            HttpResponse getResponse = client.execute(getRequest);
            final int statusCode = getResponse.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                Log.w(getClass().getSimpleName(),
                        "Error " + statusCode + " for URL " + URL);
                return null;
            }

            HttpEntity getResponseEntity = getResponse.getEntity();
            return getResponseEntity.getContent();

        }
        catch (IOException e) {
            getRequest.abort();
            Log.w(getClass().getSimpleName(), "Error for URL " + URL, e);
        }

        return null;

    }

}
