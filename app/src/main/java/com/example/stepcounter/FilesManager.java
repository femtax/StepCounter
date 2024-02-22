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

public class FilesManager {
    public static void writeToFile(Context context, String fileName, String message) {
        try {
            // getExternalFilesDir(null)
            File file = new File(context.getExternalFilesDir(null), fileName);
            if (!file.exists()) {
                boolean created = file.createNewFile();
                LoggerManager.writeToLogFile(context, file.getAbsolutePath() + " created.");
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

    public static String readFile(Context context, String fileName) {
        StringBuilder contentBuilder = new StringBuilder();
        // getExternalFilesDir(null)
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
                    e.printStackTrace();
                }
            }
        }

    }

    public static String[] listFilesInDirectory(Context context) {
        File directory = context.getFilesDir();
        return directory.list();
    }


    public static String readAndDeleteFirstLine(Context context, String fileName) {
        File file = new File(context.getExternalFilesDir(null), fileName);
        StringBuilder stringBuilder = new StringBuilder();
        String firstLine = null;

        // Чтение файла и сохранение всех строк кроме первой
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            firstLine = reader.readLine(); // Чтение и сохранение первой строки

            String line;
            while ((line = reader.readLine()) != null) { // Чтение оставшихся строк
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Перезапись файла без первой строки
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return firstLine; // Возвращение первой строки
    }




    public static List<String> readLinesAndDelete(Context context, String fileName, int linesCount) {
        File file = new File(context.getExternalFilesDir(null), fileName);
        List<String> linesToSend = new ArrayList<>();
        List<String> remainingLines = new ArrayList<>();

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
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String remainingLine : remainingLines) {
                writer.write(remainingLine + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linesToSend;
    }



}
