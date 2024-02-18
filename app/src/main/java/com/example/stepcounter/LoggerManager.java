package com.example.stepcounter;
import android.content.Context;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoggerManager {
    public static void writeToLogFile(Context context, String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date());
        String logMessage = timestamp + " - " + message;
        try {
            File logFile = new File(context.getExternalFilesDir(null), "log.txt");
            if (!logFile.exists()) {
                boolean created = logFile.createNewFile();
//                writeToLogFile(context, "Log file created.");
                if (!created) {
                    return;
                }
            }
//            is it ok?
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(logMessage);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
