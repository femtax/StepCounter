package com.example.stepcounter;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity implements SensorEventListener {

    // Sensor and UI elements declaration
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor magnetometer;
    private Sensor stepSensor;
    private TextView textViewAccelerometer;
    private TextView textViewGyroscope;
    private TextView textViewMagnetometer;
    private TextView textViewStep;
    private static final int REQUEST_CODE = 1; // Any integer

    // Initialize activity and layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewAccelerometer = findViewById(R.id.textViewAccelerometer);
        textViewGyroscope = findViewById(R.id.textViewGyroscope);
        textViewMagnetometer = findViewById(R.id.textViewMagnetometer);
        textViewStep = findViewById(R.id.textViewStep);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE);
        }


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            textViewAccelerometer.setText("Accelerometer not supported on this device.");
        }

        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            textViewGyroscope.setText("Gyroscope not supported on this device.");
        }

        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            textViewMagnetometer.setText("Magnetometer not supported on this device.");
        }

        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            textViewStep.setText("Step sensor not supported on this device.");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { // Process accelerometer sensor data
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            String accelerometerData = String.format("X: %.2f\nY: %.2f\nZ: %.2f", x, y, z);
            textViewAccelerometer.setText(accelerometerData);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) { // Process gyroscope sensor data
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            String gyroscopeData = String.format("X: %.2f\nY: %.2f\nZ: %.2f", x, y, z);
            textViewGyroscope.setText(gyroscopeData);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) { // Process magnetometer sensor data
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            String magnetometerData = String.format("X: %.2f\nY: %.2f\nZ: %.2f", x, y, z);
            textViewMagnetometer.setText(magnetometerData);
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) { // Process step counter sensor data
            int steps = (int) event.values[0];

            String stepCounterData = String.format("%d", steps);
            textViewStep.setText(stepCounterData);
        } else {
            textViewStep.setText(event.toString());
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO: Add accuracy
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO: Handle the case where permission is granted
            } else {
                // TODO: Handle the case where permission is denied
            }
        }
    }

    // Unregister sensor listeners when activity is not visible
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
