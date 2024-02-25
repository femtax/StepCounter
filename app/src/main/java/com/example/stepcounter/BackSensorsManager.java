package com.example.stepcounter;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.Arrays;

// This class is responsible for managing the back sensors.
public class BackSensorsManager extends Service implements SensorEventListener {

    // Sensor manager
    private SensorManager sensorManager;
    long startTime;
    
    // This method is called when the service is created.
    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        startTime = System.currentTimeMillis();
        super.onCreate();

        // Create a notification channel
        String NOTIFICATION_CHANNEL_ID = "com.example.stepcounter";

        // Set the name of the notification channel
        String channelName = "My Background Service";

        // Set the importance level of the notification
        int importance = NotificationManager.IMPORTANCE_LOW;

        // Create the channel
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance);

        // Set the description of the notification channel
        channel.setDescription("Used for background service");

        // Get the notification manager
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        // Create the notification channel
        notificationManager.createNotificationChannel(channel);

        // Create a notification to indicate that the service is running in the background
        Notification notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setContentTitle("App is running in background")
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);

        // Register the step counter sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Register the sensors and set the sampling rate
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Register the accelerometer sensor
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        // Register the gyroscope sensor
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }

        // Register the magnetometer sensor
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }


    //  This method is called when the service is started.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    // This method is called when the service is destroyed.
    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    // This method is called when the sensor value changes.
    @Override
    public void onSensorChanged(SensorEvent event) {
        String data = (System.currentTimeMillis() - startTime) + ", " + FrontSensorsManager.getSensorName(getApplicationContext(), event.sensor.getType()) + ", " + Arrays.toString(event.values);
        FilesManager.writeToFile(getApplicationContext(), "SensorData.txt", data);
    }

    // This method is called when the accuracy of the sensor has changed.
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // This method is called when the service is started.
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
