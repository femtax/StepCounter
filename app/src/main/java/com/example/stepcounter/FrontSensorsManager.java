package com.example.stepcounter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

// This class is responsible for managing the sensors.
public class FrontSensorsManager {
    // The sensor manager
    private final SensorManager sensorManager;

    // Constructor
    public FrontSensorsManager(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    // This method returns the list of available sensors.
    public static String getSensorName(Context context, int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                return "ACCELEROMETER";
            case Sensor.TYPE_GYROSCOPE:
                return "GYROSCOPE";
            case Sensor.TYPE_MAGNETIC_FIELD:
                return "MAGNETIC_FIELD";
            case Sensor.TYPE_STEP_COUNTER:
                return "STEP_COUNTER";
            case Sensor.TYPE_LIGHT:
                return "LIGHT";
            case Sensor.TYPE_PRESSURE:
                return "PRESSURE";
            case Sensor.TYPE_PROXIMITY:
                return "PROXIMITY";
            case Sensor.TYPE_GRAVITY:
                return "GRAVITY";
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "AMBIENT_TEMPERATURE";
            default:
                LoggerManager.writeToLogFile(context, "Unknown sensor type: " + sensorType);
                return "UNKNOWN_SENSOR";
        }
    }

    // This method writes the details of the sensors to a file.
    @SuppressWarnings("unused")
    public void writeSensorDetailsToFile(Context context, String fileName) {
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        StringBuilder sensorDetails = new StringBuilder();
        for (Sensor sensor : sensorList) {
            String sensorInfo = "Name: " + sensor.getName() +
                    ", Type: " + getSensorName(context, sensor.getType()) +
                    ", Vendor: " + sensor.getVendor() +
                    ", Version: " + sensor.getVersion() +
                    ", Power: " + sensor.getPower() + "mA" +
                    ", Resolution: " + sensor.getResolution() +
                    ", Max Range: " + sensor.getMaximumRange() +
                    ", Min Delay: " + sensor.getMinDelay() +
                    ", Wakeup: " + sensor.isWakeUpSensor() + "\n";
            sensorDetails.append(sensorInfo);
        }
        FilesManager.writeToFile(context, fileName, sensorDetails.toString());
    }


}
