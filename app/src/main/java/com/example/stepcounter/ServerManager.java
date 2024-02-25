package com.example.stepcounter;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// This class is responsible for sending data to the server.
public class ServerManager {

    // This method prepares the JSON body to be sent to the server.
    public static String prepareJsonBody(Context context, List<String> lines, String username) {
        
        // Return null if there's no data to process
        if (lines == null || lines.isEmpty()) {
            LoggerManager.writeToLogFile(context, "prepareJsonBody: No lines provided");
            return null; 
        }

        // Create a JSON object
        JSONObject jsonObject = new JSONObject();

        // Add the username and the data to the JSON object
        try {
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
            e.printStackTrace();
        }

        // Return the JSON object as a string
        return jsonObject.toString();
    }


    // This method sends a POST request to the server.
    public static void sendPostRequest(Context context, String url, String jsonBody) {

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
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {

                    // Log the success of the request
                    String responseData = Objects.requireNonNull(response.body()).string();
                    LoggerManager.writeToLogFile(context, "POST request to " + url + " successful. Response: " + responseData);
                } else {

                    // Log the failure of the request
                    LoggerManager.writeToLogFile(context, "POST request to " + url + " failed with code: " + response.code());
                }
            }
        });
    }
}
