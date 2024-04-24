package com.example.stepcounter;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


// This class is responsible for managing the back sensors.
public class BackSensorsManager extends Service implements SensorEventListener {

    // Sensor manager
    private SensorManager sensorManager;
    SimpleDateFormat sdf;

    // This method is called when the service is created.
    @SuppressLint({"ForegroundServiceType", "SimpleDateFormat"})
    @Override
    public void onCreate() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

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

        // Register the sensors and set the sampling rate
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Register the accelerometer sensor
        // SENSOR_DELAY_GAME
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
        // Converting float[] to String[]
        String[] stringValues = new String[event.values.length];
        for (int i = 0; i < event.values.length; i++) {
            stringValues[i] = String.valueOf(event.values[i]);
        }

        // Using TextUtils.join() to create a comma-separated values string
        String data = sdf.format(new Date()) + ", " + FrontSensorsManager.getSensorName(getApplicationContext(), event.sensor.getType()) + ", " + TextUtils.join(", ", stringValues);
        FilesManager.writeToFile(getApplicationContext(), "SensorData.txt", data);
        FilesManager.writeToFile(getApplicationContext(), "SensorTrainingData.txt", data);
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
