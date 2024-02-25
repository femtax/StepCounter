package com.example.stepcounter;
import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

// This class is responsible for reading and writing files.
public class FilesManager {
    
    // Lock to prevent concurrent file access
    private static final ReentrantLock lock = new ReentrantLock();

    //  This method writes a message to a file.
    public static void writeToFile(Context context, String fileName, String message) {
        
        // Lock the file to prevent concurrent access
        lock.lock();
        try {

            // getExternalFilesDir (null) - needs to be replaced with Scoped Storage
            File file = new File(context.getExternalFilesDir(null), fileName);
            if (!file.exists()) {
                LoggerManager.writeToLogFile(context, "File does not exist: " + file.getAbsolutePath());
                if (!file.createNewFile()) {
                    LoggerManager.writeToLogFile(context, "Failed to create file: " + file.getAbsolutePath());
                    return;
                }
                LoggerManager.writeToLogFile(context, "File created successfully: " + file.getAbsolutePath());
            }

            try (BufferedWriter buf = new BufferedWriter(new FileWriter(file, true))) {
                buf.append(message);
                buf.newLine();
            }
        } catch (IOException e) {
            LoggerManager.writeToLogFile(context, "Error writing to file: " + e.getMessage());
            e.printStackTrace();
        } finally {
            //  Unlock the file
            lock.unlock();
        }
    }


    // This method reads a file and returns its content.
    @SuppressWarnings("unused")
    public static String readFile(Context context, String fileName) {
        StringBuilder contentBuilder = new StringBuilder();

        // getExternalFilesDir (null) - needs to be replaced with Scoped Storage
        File file = new File(context.getExternalFilesDir(null), fileName);

        LoggerManager.writeToLogFile(context, "Attempting to read file: " + file.getAbsolutePath());

        if (!file.exists()) {
            LoggerManager.writeToLogFile(context, "File does not exist: " + file.getAbsolutePath());
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            LoggerManager.writeToLogFile(context, "File read successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            LoggerManager.writeToLogFile(context, "Error reading file: " + file.getAbsolutePath() + ": " + e.getMessage());
        }

        return contentBuilder.toString();
    }

    // This method clears a file.
    @SuppressWarnings("unused")
    public static void clearFile(Context context, String filePath) {
        FileWriter fw = null;
        try {
            // getExternalFilesDir(null)
            fw = new FileWriter(context.getExternalFilesDir(null) + filePath, false);
            fw.write("");
            LoggerManager.writeToLogFile(context, "File cleared successfully: " + context.getFilesDir() + filePath);
        } catch (IOException e) {
            LoggerManager.writeToLogFile(context, "Error clearing file " + filePath + ": " + e.getMessage());
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    LoggerManager.writeToLogFile(context, "Error closing file: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

    }

    //  This method lists the files in a directory.
    @SuppressWarnings("unused")
    public static String[] listFilesInDirectory(Context context) {
        File directory = context.getFilesDir();
        return directory.list();
    }

    // This method reads the first line of a file and deletes it.
    @SuppressWarnings("unused")
    public static String readAndDeleteFirstLine(Context context, String fileName) {

        // getExternalFilesDir (null) - needs to be replaced with Scoped Storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        StringBuilder stringBuilder = new StringBuilder();
        String firstLine = null;

        // Read the file and store the first line
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            firstLine = reader.readLine();

            // Store the remaining lines
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Write the remaining lines back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(stringBuilder.toString());
        } catch (IOException e) {
            LoggerManager.writeToLogFile(context, "Error writing to file: " + e.getMessage());
            e.printStackTrace();
        }

        return firstLine;
    }

    // This method reads a specified number of lines from a file and deletes them.
    public static List<String> readLinesAndDelete(Context context, String fileName, int linesCount) {
        
        // Lock the file to prevent concurrent access
        lock.lock();
        try {

            // getExternalFilesDir (null) - needs to be replaced with Scoped Storage
            File file = new File(context.getExternalFilesDir(null), fileName);
            
            // linesToSend will store the lines to be sent
            List<String> linesToSend = new ArrayList<>();

            // remainingLines will store the lines that were not sent
            List<String> remainingLines = new ArrayList<>();

            // Read the file and store the lines to be sent
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (linesToSend.size() < linesCount) {
                        linesToSend.add(line);
                    } else {
                        remainingLines.add(line);
                    }
                }
            } catch (IOException e) {
                LoggerManager.writeToLogFile(context, "Error reading file: " + e.getMessage());
                e.printStackTrace();
            }

            // Write the remaining lines back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String remainingLine : remainingLines) {
                    writer.write(remainingLine + "\n");
                }
            } catch (IOException e) {
                LoggerManager.writeToLogFile(context, "Error writing to file: " + e.getMessage());
                e.printStackTrace();
            }

            return linesToSend;
        } finally {

            // Unlock the file
            lock.unlock();
        }
    }
}
