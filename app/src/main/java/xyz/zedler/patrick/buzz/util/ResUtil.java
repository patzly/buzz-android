/*
 * This file is part of Buzz Android.
 *
 * Buzz Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Buzz Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Buzz Android. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2020-2021 by Patrick Zedler
 */

package xyz.zedler.patrick.buzz.util;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class ResUtil {

  private final static String TAG = ResUtil.class.getSimpleName();
  private final static boolean DEBUG = false;

  public static String readFromFile(Context context, String file) {
    StringBuilder text = new StringBuilder();
    try {
      InputStream inputStream = context.getAssets().open(file);
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      for (String line; (line = bufferedReader.readLine()) != null; ) {
        text.append(line).append('\n');
      }
      text.deleteCharAt(text.length() - 1);
      inputStream.close();
    } catch (FileNotFoundException e) {
      if (DEBUG) {
        Log.e(TAG, "readFromFile: \"" + file + "\" not found!");
      }
      return null;
    } catch (Exception e) {
      if (DEBUG) {
        Log.e(TAG, "readFromFile: " + e.toString());
      }
      return null;
    }
    return text.toString();
  }

  /**
   * @return All words from the word list with more than 3 and max 7 different letters
   */
  @NonNull
  public static ArrayList<String> getAllGoodWords(Context context) {
    ArrayList<String> words = new ArrayList<>();
    try {
      InputStream inputStream = context.getAssets().open("wikipedia_de_lowercase_replaced_äöüß.txt");
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      for (String line; (line = bufferedReader.readLine()) != null; ) {
        if (!words.contains(line) && isLongerThan3(line) && containsMax7Different(line)) {
          words.add(line);
        }
      }
      inputStream.close();
      Collections.sort(words);
    } catch (FileNotFoundException e) {
      if (DEBUG) {
        Log.e(TAG, "readFromFile: file not found!");
      }
    } catch (Exception e) {
      if (DEBUG) {
        Log.e(TAG, "readFromFile: " + e.toString());
      }
    }
    return words;
  }

  @NonNull
  public static ArrayList<String> getPangrams(Context context) {
    ArrayList<String> pangrams = new ArrayList<>();
    try {
      InputStream inputStream = context.getAssets().open("filtered.txt");
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      for (String line; (line = bufferedReader.readLine()) != null;) {
        if (!containsOnlyUppercase(line) && containsExactly7Different(line)) {
          pangrams.add(line.toLowerCase());
        }
      }
      inputStream.close();
    } catch (FileNotFoundException e) {
      if (DEBUG) Log.e(TAG, "readFromFile: file not found!");
    } catch (Exception e) {
      if (DEBUG) Log.e(TAG, "readFromFile: " + e.toString());
    }
    return pangrams;
  }

  @NonNull
  public static ArrayList<String> getMatchingWords(Context context, String word, String center) {
    ArrayList<String> words = new ArrayList<>();
    try {
      InputStream inputStream = context.getAssets().open("filtered.txt");
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      for (String line; (line = bufferedReader.readLine()) != null;) {
        if (line.toLowerCase().contains(center) && SpellingUtil.containsNoOtherLetter(line, word)) {
          if(!words.contains(line)) words.add(line);
        }
      }
      inputStream.close();
    } catch (FileNotFoundException e) {
      if (DEBUG) Log.e(TAG, "readFromFile: file not found!");
    } catch (Exception e) {
      if (DEBUG) Log.e(TAG, "readFromFile: " + e.toString());
    }
    return words;
  }

  private static boolean isLongerThan3(String input) {
    return input.length() > 3;
  }

  public static boolean containsMax7Different(String input) {
    ArrayList<String> usedLetters = new ArrayList<>();
    for(int i = 0; i < input.length(); i++) {
      String character = input.substring(i, i + 1).toLowerCase();
      if(!usedLetters.contains(character)) usedLetters.add(character);
      if(usedLetters.size() > 7) return false;
    }
    return true;
  }

  private static boolean containsExactly7Different(String input) {
    ArrayList<String> usedLetters = new ArrayList<>();
    for(int i = 0; i < input.length(); i++) {
      String character = input.substring(i, i + 1).toLowerCase();
      if(!usedLetters.contains(character)) usedLetters.add(character);
      if(usedLetters.size() > 7) return false;
    }
    return usedLetters.size() == 7;
  }

  private static boolean containsOnlyUppercase(String input) {
    return input.matches("^[A-Z]+$");
  }
}
