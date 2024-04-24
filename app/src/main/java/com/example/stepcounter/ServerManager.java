package com.example.stepcounter;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// This class is responsible for sending data to the server.
public class ServerManager {

    //
    private static final int MAX_RETRY_ATTEMPTS = 0;

    // if retryCount == 0 response don't work
    private static int retryCount = 0;
    //
    private static final Logger LOGGER = Logger.getLogger(FilesManager.class.getName());


    // This method prepares the JSON body to be sent to the server.
    public static String prepareJsonBody(Context context, List<String> lines, String username, int packetNumber) {
        
        // Return null if there's no data to process
        if (lines == null || lines.isEmpty()) {
            LoggerManager.writeToLogFile(context, "prepareJsonBody: No lines provided");
            return null; 
        }

        // Create a JSON object
        JSONObject jsonObject = new JSONObject();

        // Add the username and the data to the JSON object
        try {
            //
            jsonObject.put("packet_number", packetNumber);
            jsonObject.put("username", username);
            JSONArray jsonArray = new JSONArray();

            for (String line : lines) {

                // Add each line to the JSON array
                jsonArray.put(line);
            }
            jsonObject.put("data", jsonArray);

            // Log the successful creation of the JSON object
            LoggerManager.writeToLogFile(context, "prepareJsonBody: JSON created successfully for user " + username);
        } catch (JSONException e) {

            // Log any exceptions that occur
            LoggerManager.writeToLogFile(context, "prepareJsonBody: JSONException - " + e.getMessage());
        }

        // Return the JSON object as a string
        return jsonObject.toString();
    }

    // Very bad working, still don't know why
    // This method sends a POST request to the server.
    @SuppressWarnings("unused")
    public static void sendPostRequestWithC0nfirmation(Context context, String url, String jsonBody, int packetNumber) {

        // Set the media type to JSON
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  //
                .readTimeout(60, TimeUnit.SECONDS)     //
                .build();
        // Create the request
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // Log the request
        LoggerManager.writeToLogFile(context, "Sending POST " + packetNumber + " request to " + url);

        // Send the POST request
        client.newCall(request).enqueue(new okhttp3.Callback() {

            // Log the success or failure of the request
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                LoggerManager.writeToLogFile(context, "POST " + packetNumber + " request to " + url + " failed: " + e.getMessage());
            }

            // Log the success of the request
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {

                    //
                    retryCount = 0;

                    // Log the success of the request
                    String responseData = Objects.requireNonNull(response.body()).string();
                    LoggerManager.writeToLogFile(context, "POST " + packetNumber + " request to " + url + " successful. Response: " + responseData);

                } else {
                    // Log the failure of the request
                    LoggerManager.writeToLogFile(context, "POST " + packetNumber + " request to " + url + " failed with code: " + response.code());

                    if (retryCount < MAX_RETRY_ATTEMPTS) {
                        //
                        retryCount++;

                        //
                        long delayMillis = 60000;

                        new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                            //
                            sendPostRequestWithC0nfirmation(context, url, jsonBody, packetNumber);

                        }, delayMillis);
                    } else {
                        //
                        LoggerManager.writeToLogFile(context, "POST " + packetNumber + " request to " + url + " exceeded max retry attempts");

                    }
                }
            }
        });
    }


    // This method sends a POST request to the server.
    public static void sendPostRequest(Context context, String url, String jsonBody, EditText editTextForActivity, EditText editTextForCountSteps, EditText editTestForConfidence, int stepsCount) {

        // Set the media type to JSON
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        // Create the request
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // Log the request
        LoggerManager.writeToLogFile(context, "Sending POST request to " + url);

        // Send the POST request
        client.newCall(request).enqueue(new okhttp3.Callback() {

            // Log the success or failure of the request
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                LoggerManager.writeToLogFile(context, "POST request to " + url + " failed: " + e.getMessage());
            }

            // Log the success of the request
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response)  throws IOException {
                if (response.isSuccessful()) {

                    String responseData = Objects.requireNonNull(response.body()).string();
                    String activityPrediction;
                    String stepsCountCurrent;
                    String confidencePrediction;

                    try {
                        // Assuming the use of org.json (can be adapted for Gson similarly)
                        JSONObject jsonObj = new JSONObject(responseData);
                        activityPrediction = jsonObj.getString("prediction");
                        stepsCountCurrent = jsonObj.getString("steps");
                        confidencePrediction = jsonObj.getString("confidence");

                        // Update editText on the main thread
                        new Handler(Looper.getMainLooper()).post(() -> editTextForActivity.setText(activityPrediction));
                        new Handler(Looper.getMainLooper()).post(() -> editTextForCountSteps.setText(stepsCountCurrent));
                        new Handler(Looper.getMainLooper()).post(() -> editTestForConfidence.setText(confidencePrediction));


                        if (activityPrediction.equals("stepping")){
                            editTextForActivity.setTextColor(Color.RED);
                        } else if (activityPrediction.equals("standing")) {
                            editTextForActivity.setTextColor(Color.GREEN);
                        } else if(activityPrediction.equals("sitting")) {
                            editTextForActivity.setTextColor(Color.BLUE);
                        }


                        // Log the successful retrieval and setting of the value
                        LoggerManager.writeToLogFile(context, "POST request to " + url + " successful. Response: " + responseData);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        LoggerManager.writeToLogFile(context, "Error parsing response data: " + e.getMessage());
                    }

                } else {

                    // Log the failure of the request
                    LoggerManager.writeToLogFile(context, "POST request to " + url + " failed with code: " + response.code());
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public static boolean isConnectedToWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            }
        }

        return false;
    }
}
