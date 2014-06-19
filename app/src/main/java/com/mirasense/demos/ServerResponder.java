package com.mirasense.demos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.os.Handler;
import android.util.Log;
import com.google.gson.Gson;

public class ServerResponder {

    // SERVER VARIABLES
    //private static final String URL = "http://gpop-server.com/find-a-product/search.php?q=1234"; // Server URL
    private static final String URL = "http://gpop-server.com/find-a-product/search.php?"; // Server URL
    private static final Gson gson = new Gson(); // Gson parser object.
    public static final Handler uiHandler = new Handler(); // UI handler?

    // ???? VARIABLES
    private static final String TAG = ServerResponder.class.getSimpleName();

    /** SERVER FUNCTIONALITY ___________________________________________________________________ **/

    // Updates the server?
    public static void updateServer(
            final String title,
            final String link,
            final String description,
            final Double price,
            final String image,
            final String store,
            final ScanditSDKGlassActivity activity) {
        Log.d(TAG, "updateServer"); // Just a log.

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                updateServerOnSecondThread(title, link, description,
                        price, image, store, activity);
            }
        });
        thread.start();
    }

    // Updates the server? ALTERNATE FUNCTION?
    public static void updateServer(
            final String barcode, // CHANGE PARAMETER
            final List<String> jsonList, // JSON LIST
            final ScanditSDKGlassActivity activity) {
        Log.d(TAG, "updateServer");

        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                updateServerOnSecondThread(barcode, jsonList, activity);
            }
        });
        thread.start();
    }

    private static void updateServerOnSecondThread(
            final String title, // CHANGE PARAMETER
            final List<String> jsonList, // JSON LIST
            final ScanditSDKGlassActivity activity) {
        Log.d(TAG, "updateServerOnSecondThread");

        HttpClient httpclient = new DefaultHttpClient();

        /*
        HttpPost httpMethod = new HttpPost(URL
                + "?title=" + encodeValue(title)
        );
        */

        // NEW POST METHOD
        HttpPost httpMethod = new HttpPost(URL
                + "?=" + encodeValue(title)
        );

        HttpResponse response;
        try {

            final String json = gson.toJson(jsonList);
            final StringEntity jsonEntity = new StringEntity(json);
            httpMethod.setEntity(jsonEntity);
            httpMethod.setHeader("Content-Type", "application/json");

            Log.d(TAG, "json = " + json);


            response = httpclient.execute(httpMethod);
            Log.d(TAG, "response status: " + response.getStatusLine().toString());
            HttpEntity entity = response.getEntity();

            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    activity.onServerUpdated(); // USED FOR LOGGING IN SERVER. NOT NEEDED.
                }
            });


        } catch (Exception e) {
            Log.e(TAG, "Error downloading items", e);

            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    activity.onServerUpdated(); // USED FOR LOGGING IN SERVER. NOT NEEDED.
                }
            });
        }
    }

    private static void updateServerOnSecondThread(
            final String title,
            final String link,
            final String description,
            final Double price,
            final String image,
            final String store,
            final ScanditSDKGlassActivity activity) {
        Log.d(TAG, "updateServerOnSecondThread");

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpMethod = new HttpPost(URL
                + "?title=" + encodeValue(title)
                + "&link=" + encodeValue(link)
                + "&description=" + encodeValue(description)
                + "&price=" + encodeValue(Double.toString(price))
                + "&image=" + encodeValue(image)
                + "&store=" + encodeValue(store)
        );

        HttpResponse response;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("title", title));
            nameValuePairs.add(new BasicNameValuePair("link", link));
            nameValuePairs.add(new BasicNameValuePair("description", description));
            nameValuePairs.add(new BasicNameValuePair("price", Double.toString(price)));
            nameValuePairs.add(new BasicNameValuePair("image", image));
            nameValuePairs.add(new BasicNameValuePair("store", store));
            httpMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            response = httpclient.execute(httpMethod);
            Log.d(TAG, "response status: " + response.getStatusLine().toString());
            HttpEntity entity = response.getEntity();


            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    activity.onServerUpdated(); // USED FOR LOGGING IN SERVER. NOT NEEDED.
                }
            });


        } catch (Exception e) {
            Log.e(TAG, "Error downloading items", e);

            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    activity.onServerUpdated(); // USED FOR LOGGING IN SERVER. NOT NEEDED.
                }
            });
        }
    }

    public static String encodeValue(final Double unescaped) {
        if (null == unescaped) {
            return "";
        }
        return encodeValue(Double.toString(unescaped));
    }

    public static String encodeValue(final String unescaped) {
        if (null == unescaped) {
            return "";
        }
        // return URIUtil.encodeWithinQuery(unescaped, "UTF-8");
        try {
            return URLEncoder.encode(unescaped, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Couldn't encode query value", e);
        }
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }





}
