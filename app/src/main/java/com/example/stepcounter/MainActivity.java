package com.example.stepcounter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


// This class is responsible for the main activity of the application.
public class MainActivity extends Activity {

    // Request codes for permissions
    private static final int REQUEST_ACTIVITY_RECOGNITION = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 2;

    private static final int REQUEST_MULTIPLE_PERMISSIONS = 10;

    // Initialize activity and layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    
        // Start the background service
        super.onCreate(savedInstanceState);
        Intent serviceIntent = new Intent(this, BackSensorsManager.class);
        startForegroundService(serviceIntent);

        // Set the layout
        setContentView(R.layout.activity_main);

        // Not woeking =(
        /*
        // Request permission to write to external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }

        // Request permission to access activity recognition
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_ACTIVITY_RECOGNITION);
        }
        */


        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACTIVITY_RECOGNITION};
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_MULTIPLE_PERMISSIONS);
        }


        // Button to add comments
        EditText editText = findViewById(R.id.editText);
        Button buttonForComments = findViewById(R.id.buttonForComments);


        // Button to add comments
        buttonForComments.setOnClickListener(v -> {
            String text = editText.getText().toString();
            if (!text.isEmpty()) {
                FilesManager.writeToFile(getApplicationContext(), "SensorData.txt", "// " + text);
                LoggerManager.writeToLogFile(getApplicationContext(), "Added comment: " + text);
                editText.setText("");
            }
        });

        // Button to send data to the server
        Button buttonForSendServer = findViewById(R.id.buttonForSendServer);

        // Button to send data to the server
        buttonForSendServer.setOnClickListener(v -> new Thread(() -> {
            String serverUrl = "http://217.76.54.178:5000/submit-data";
            List<String> lines = FilesManager.readLinesAndDelete(getApplicationContext(), "SensorData.txt", 300);
            while (!lines.isEmpty()) {
                String jsonBody = ServerManager.prepareJsonBody(getApplicationContext(), lines, "andrusha");
                ServerManager.sendPostRequest(getApplicationContext(), serverUrl, jsonBody);
                lines = FilesManager.readLinesAndDelete(getApplicationContext(), "SensorData.txt", 200);
            }
            LoggerManager.writeToLogFile(getApplicationContext(), "Data sent to server");
        }).start());
    }



    // Handle the result of the request for permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_ACTIVITY_RECOGNITION:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO: Handle the case where permission is granted
                    LoggerManager.writeToLogFile(getApplicationContext(), "Activity recognition permission granted");
                } else {
                    // TODO: Handle the case where permission is denied
                    LoggerManager.writeToLogFile(getApplicationContext(), "Activity recognition permission denied");
                }
            }
            case REQUEST_EXTERNAL_STORAGE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO: Handle the case where permission is granted
                    LoggerManager.writeToLogFile(getApplicationContext(), "External storage permission granted");
                } else {
                    // TODO: Handle the case where permission is denied
                    LoggerManager.writeToLogFile(getApplicationContext(), "External storage permission denied");
                }
            }
        }
    }


}