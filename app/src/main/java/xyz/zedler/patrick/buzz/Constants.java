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

package xyz.zedler.patrick.buzz;

public class Constants {

  public static final int ANIMATION = 250;
  public static final int HINTS_MAX = 10;

  public final static class PREF {

    public final static String LETTERS = "letters";
    public final static String CENTER = "center";
    public final static String FOUND = "found";
    public final static String HINTS = "hints";
  }

  public final static class DEFAULT {

    public final static String LETTERS = "LIBSZN";
    public final static String CENTER = "E";
  }

  public final static class BOTTOM_SHEET {

    public final static String MISSED_WORDS = "missed_words";
    public final static String FOUND_WORDS = "found_words";
    public final static String HINTS_USED = "hints_used";
    public final static String LETTERS = "letters";
    public final static String CENTER = "center";
  }
}
