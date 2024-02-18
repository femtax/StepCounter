package com.example.stepcounter;
import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FilesManager {
    public static void writeToFile(Context context, String fileName, String message) {
        try {
            File file = new File(context.getExternalFilesDir(null), fileName);
            if (!file.exists()) {
                boolean created = file.createNewFile();
                LoggerManager.writeToLogFile(context, fileName + " created.");
                if (!created) {
                    return;
                }
            }

            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            buf.append(message);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String readFile(Context context, String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return "";
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            reader.close();
            LoggerManager.writeToLogFile(context, "File read successfully: " + filePath);
            LoggerManager.writeToLogFile(context, "File text: " + contentBuilder.toString());

        } catch (IOException e) {
//            e.printStackTrace();
            LoggerManager.writeToLogFile(context, "Error reading file " + filePath + ": " + e.getMessage());
        }
        return contentBuilder.toString();
    }

    public static void clearFile(Context context, String filePath) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(filePath, false);
            fw.write("");
            LoggerManager.writeToLogFile(context, "File cleared successfully: " + filePath);
        } catch (IOException e) {
//            e.printStackTrace();
            LoggerManager.writeToLogFile(context, "Error clearing file " + filePath + ": " + e.getMessage());
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
