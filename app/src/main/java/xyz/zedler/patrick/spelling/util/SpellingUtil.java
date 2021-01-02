package xyz.zedler.patrick.spelling.util;

import java.util.ArrayList;

public class SpellingUtil {

    public static boolean containsExactly7Different(String input) {
        ArrayList<String> usedLetters = new ArrayList<>();
        for(int i = 0; i < input.length(); i++) {
            String character = input.substring(i, i + 1).toUpperCase();
            if(!usedLetters.contains(character)) usedLetters.add(character);
            if(usedLetters.size() > 7) return false;
        }
        return usedLetters.size() == 7;
    }

    public static boolean containsNoOtherLetter(String input, String availableChars) {
        for(int i = 0; i < input.length(); i++) {
            String character = input.substring(i, i + 1).toUpperCase();
            if(!availableChars.toUpperCase().contains(character)) return false;
        }
        return true;
    }
}
