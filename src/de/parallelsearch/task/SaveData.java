package de.parallelsearch.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SaveData {
    public void saveToJson(List<String> generatedStrings) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String FILE_PATH = "data/strings.json";
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(new StringWrapper(generatedStrings), writer);
            System.out.println("Successfully wrote to " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error saving JSON: " + e.getMessage());
        }
    }
    public void saveToTextFile(List<String> generatedStrings) {
        File outFile = new File("data/strings.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile))) {
            for (String str: generatedStrings) {
                writer.write(str);
                writer.newLine();
            }
            System.out.println("Successfully wrote to " + outFile.getPath());
        } catch (IOException e) {
            System.err.println("Error saving Text: " + e.getMessage());
        }
    }

    public static class StringWrapper {
        List<String> strings;
        StringWrapper(List<String> strings) {
            this.strings = strings;
        }
    }
}
