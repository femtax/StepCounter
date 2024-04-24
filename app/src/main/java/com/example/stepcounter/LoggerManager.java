package com.example.stepcounter;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

// This class is responsible for writing logs to a file.
public class LoggerManager {
    private static final Logger LOGGER = Logger.getLogger(LoggerManager.class.getName());


    // This method writes a message to a log file.
    // synchronized to prevent concurrent access
    public static synchronized void writeToLogFile(Context context, String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date());
        String logMessage = timestamp + " - " + message;
        
        try {
            File logFile = new File(context.getExternalFilesDir(null), "log.txt");
            if (!logFile.exists()) {
                boolean created = logFile.createNewFile();
                if (!created) {
                    return;
                }
            }

            // try-with-resources statement to automatically close the file
            try (BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true))) {
                buf.append(logMessage);
                buf.newLine();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}