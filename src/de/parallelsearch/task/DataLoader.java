package de.parallelsearch.task;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataLoader {
    public List<String> loadStringsInMemory() {
        Gson gson = new Gson();
        List<String> strings = new ArrayList<>();
        String FILE_PATH = "data/strings.json";
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            Type type = new TypeToken<StringWrapper>(){}.getType();
            StringWrapper wrapper = gson.fromJson(reader, type);
            strings = wrapper.strings;
        } catch (IOException e) {
            System.err.println("Error loading JSON: " + e.getMessage());
        }
        Collections.shuffle(strings);
        return strings;
    }

    private static class StringWrapper {
        List<String> strings;
    }
}
