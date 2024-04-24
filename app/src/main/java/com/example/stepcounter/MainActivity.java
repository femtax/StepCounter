package com.example.stepcounter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


// This class is responsible for the main activity of the application.
public class MainActivity extends Activity {

    // Request codes for permissions
    private static final int REQUEST_ACTIVITY_RECOGNITION = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_MULTIPLE_PERMISSIONS = 10;
    int packerNumber = 0;
    private final Handler handler = new Handler();
    private EditText editTextForActivity;
    private EditText editTextForConfidence;

    private EditText editTextForCountSteps;
    int stepsCount;
    String nickName;
    // Initialize activity and layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    
        // Start the background service
        super.onCreate(savedInstanceState);
        Intent serviceIntent = new Intent(this, BackSensorsManager.class);
        startForegroundService(serviceIntent);

        // Set the layout
        setContentView(R.layout.activity_main);

        editTextForActivity = findViewById(R.id.activityDataEditText);

        editTextForConfidence = findViewById(R.id.confidenceDataEditText);

        editTextForCountSteps = findViewById(R.id.stepsDataEditText);

        stepsCount = 0;

        nickName = generateUniqueNickname(8);
        // Not working =(
        // Request permission to write to external storage
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
//        }
        // Request permission to access activity recognition
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_ACTIVITY_RECOGNITION);
//        }


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

        Button startButton = findViewById(R.id.button1);
        Button stopButton = findViewById(R.id.button2);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSendData();
                startService(serviceIntent);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDataSending();
                stopService(serviceIntent);
            }
        });
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

    private void checkAndSendData() {
        // Сode for creating a backup and sending data to the server
        // Schedule the next call of this method in 2 seconds
        Runnable sendDataRunnable = new Runnable() {
            @Override
            public void run() {
                // Your code for creating a backup and sending data to the server
                String serverUrl = "http://217.76.54.178:5000/submit-data";
                FilesManager.createBackUp(getApplicationContext(), "SensorData.txt");

                List<String> lines = FilesManager.readLinesAndDelete(getApplicationContext(), "SensorDataBackup.txt", 200);
                while (!lines.isEmpty()) {
                    packerNumber++;
                    String jsonBody = ServerManager.prepareJsonBody(getApplicationContext(), lines, nickName, packerNumber);
                    ServerManager.sendPostRequest(getApplicationContext(), serverUrl, jsonBody, editTextForActivity, editTextForCountSteps, editTextForConfidence, stepsCount);
                    lines = FilesManager.readLinesAndDelete(getApplicationContext(), "SensorDataBackup.txt", 200);
                }
                LoggerManager.writeToLogFile(getApplicationContext(), "Data sent to server");

                // Schedule the next call of this method in 2 seconds
                handler.postDelayed(this, 2000);
            }
        };

        // Start the data sending loop
        handler.post(sendDataRunnable);
    }

    public static String generateUniqueNickname(int length) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedTime = currentTime.format(formatter);

        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder randomPart = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomPart.append(characters.charAt(random.nextInt(characters.length())));
        }

        String uniqueNickname = formattedTime + randomPart.toString();
        return uniqueNickname;
    }

    private void stopDataSending() {
        handler.removeCallbacksAndMessages(null);
    }

}