package xyz.zedler.patrick.spelling;

public class Constants {

    public static final int ANIMATION = 300;
    public static final int HINTS_MAX = 10;

    public static final int PERM_REQUEST_WRITE_STORAGE = 1;
    public static final int PERM_REQUEST_READ_STORAGE = 2;

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
        public final static String HINTS_USED = "found_words";
    }
}
