package de.parallelsearch.task;

import java.util.ArrayList;
import java.util.List;

public class StringGenerator {
    private static final String ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public List<String> generateStrings() {
        List<String> result = new ArrayList<>();
        for (char c1 : ALPHABETS.toCharArray()) {
            for (char c2 : ALPHABETS.toCharArray()) {
                for (char c3 : ALPHABETS.toCharArray()) {
                    for (char c4 : ALPHABETS.toCharArray()) {
                        result.add("" + c1 + c2 + c3 + c4);
                    }
                }
            }
        }
        return result;
    }
}
