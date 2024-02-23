package com.example.stepcounter;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class MainActivity extends Activity implements SensorEventListener {

    // Sensor and UI elements declaration
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor accelerometerFastest;
    private Sensor gyroscope;
    private Sensor magnetometer;
    private Sensor stepSensor;
    private static final int REQUEST_CODE = 1; // Any integer
    long startTime;
    private final ReentrantLock lock = new ReentrantLock();

    // Initialize activity and layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startTime = System.currentTimeMillis();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE);
        }


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometerFastest = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);



        if (accelerometer != null) {
            // accelerometer, SensorManager.SENSOR_DELAY_FASTEST app is dead
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        if (accelerometerFastest != null) {
            // accelerometer, SensorManager.SENSOR_DELAY_FASTEST app is dead
            sensorManager.registerListener(this, accelerometerFastest, SensorManager.SENSOR_DELAY_GAME);
        }

        if (gyroscope != null) {
            // gyroscope, SensorManager.SENSOR_DELAY_FASTEST app is dead
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
        }

        if (magnetometer != null) {
            // magnetometer, SensorManager.SENSOR_DELAY_FASTEST app is dead
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        }

        if (stepSensor != null) {
            // stepSensor, SensorManager.SENSOR_DELAY_FASTEST app is ok
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        EditText editText = findViewById(R.id.editText);
        Button buttonForComments = findViewById(R.id.buttonForComments);


        buttonForComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                if (!text.isEmpty()) {
                    FilesManager.writeToFile(getApplicationContext(), "SensorData.txt", "// " + text);
                    LoggerManager.writeToLogFile(getApplicationContext(), "Added comment: " + text);
                    editText.setText("");
                }
            }
        });

        Button buttonForSendServer = findViewById(R.id.buttonForSendServer);

        buttonForSendServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lock.lock();
                try {
                    String serverUrl = "http://217.76.54.178:5000/submit-data";
                    List<String> lines = FilesManager.readLinesAndDelete(getApplicationContext(), "SensorData.txt", 300);
                    String jsonBody = ServerManager.prepareJsonBody(lines);
                    ServerManager.sendPostRequest(getApplicationContext(), serverUrl, jsonBody);
                } finally {
                    lock.unlock();
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        lock.lock();
        try {
                String data = (System.currentTimeMillis() - startTime) + ", " + SensorsManager.getSensorName(event.sensor.getType()) + ", " + Arrays.toString(event.values);
                FilesManager.writeToFile(getApplicationContext(), "SensorData.txt", data);
        } finally {
            lock.unlock();
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