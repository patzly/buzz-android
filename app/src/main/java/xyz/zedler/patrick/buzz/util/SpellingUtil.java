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

import java.util.ArrayList;

public class SpellingUtil {

  public static boolean containsExactly7Different(String input) {
    ArrayList<String> usedLetters = new ArrayList<>();
    for (int i = 0; i < input.length(); i++) {
      String character = input.substring(i, i + 1).toUpperCase();
      if (!usedLetters.contains(character)) {
        usedLetters.add(character);
      }
      if (usedLetters.size() > 7) {
        return false;
      }
    }
    return usedLetters.size() == 7;
  }

  public static boolean containsNoOtherLetter(String input, String availableChars) {
    for (int i = 0; i < input.length(); i++) {
      String character = input.substring(i, i + 1);
      if (!availableChars.contains(character)) {
        return false;
      }
    }
    return true;
  }
}
