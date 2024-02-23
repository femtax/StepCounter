package com.example.stepcounter;
import android.content.Context;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import java.util.Arrays;
import java.util.List;

public class ServerManager {


    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();

    public static String prepareJsonBody(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return null;
        }

        JSONArray jsonArray = new JSONArray();
        for (String line : lines) {
            jsonArray.put(line);
        }

        return "{\"data\": " + jsonArray.toString() + "}";
    }


    public static void sendPostRequest(Context context, String url, String jsonBody) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        LoggerManager.writeToLogFile(context, "Sending POST request to " + url);

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                LoggerManager.writeToLogFile(context, "POST request to " + url + " failed: " + e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    LoggerManager.writeToLogFile(context, "POST request to " + url + " successful. Response: " + responseData);
                } else {
                    LoggerManager.writeToLogFile(context, "POST request to " + url + " failed with code: " + response.code());
                }
            }
        });
    }


}
