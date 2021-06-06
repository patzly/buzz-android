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

    public final static String LANGUAGE = "language";
    public final static String DARK_MODE = "dark_mode";
    public final static String HAPTIC = "haptic";

    public final static class EN {

      public final static String LETTERS = "letters_en";
      public final static String CENTER = "center_en";
      public final static String FOUND = "found_en";
      public final static String HINTS = "hints_en";
    }

    public final static class DE {

      public final static String LETTERS = "letters_de";
      public final static String CENTER = "center_de";
      public final static String FOUND = "found_de";
      public final static String HINTS = "hints_de";
    }
  }

  public final static class DEF {

    public final static boolean DARK_MODE = false;
    public final static boolean HAPTIC = true;

    public final static class EN {

      public final static String PANGRAM = "pleasure";
      public final static String LETTERS = "leasur";
      public final static String CENTER = "p";
    }

    public final static class DE {

      public final static String PANGRAM = "tempolimit";
      public final static String LETTERS = "temoli";
      public final static String CENTER = "p";
    }
  }

  public final static class EXTRA {

    public final static String INPUT = "input";
    public final static String TITLE = "title";
    public final static String FILE = "file";
    public final static String LINK = "link";
  }

  public final static class BOTTOM_SHEET {

    public final static String MISSED_WORDS = "missed_words";
    public final static String FOUND_WORDS = "found_words";
    public final static String HINTS_USED = "hints_used";
    public final static String LETTERS = "letters";
    public final static String CENTER = "center";
  }
}
