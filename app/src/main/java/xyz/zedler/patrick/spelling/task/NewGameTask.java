package xyz.zedler.patrick.spelling.task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

import xyz.zedler.patrick.spelling.MainActivity;
import xyz.zedler.patrick.spelling.util.SpellingUtil;

public class NewGameTask extends AsyncTask<Void, Integer, String[]> {

    private final static String TAG = NewGameTask.class.getSimpleName();
    private final static boolean DEBUG = false;

    private final WeakReference<MainActivity> activityRef;
    private final ArrayList<String> matches = new ArrayList<>();
    private final ArrayList<String> allWords = new ArrayList<>();

    public NewGameTask(MainActivity activity) {
        activityRef = new WeakReference<>(activity);
    }

    @Override
    protected final String[] doInBackground(Void... params) {
        ArrayList<String> pangrams = new ArrayList<>();
        MainActivity activity = activityRef.get();
        if(activity == null) return null;
        try {
            InputStream inputStream = activity.getAssets().open("filtered.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            for (String line; (line = bufferedReader.readLine()) != null;) {
                if (SpellingUtil.containsExactly7Different(line.toUpperCase())
                        && !pangrams.contains(line.toUpperCase())
                ) {
                    pangrams.add(line.toUpperCase());
                }
                allWords.add(line);
                if(allWords.size() % 100 == 0) publishProgress(allWords.size() / 100);
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            if (DEBUG) Log.e(TAG, "readFromFile: file not found!");
        } catch (Exception e) {
            if (DEBUG) Log.e(TAG, "readFromFile: " + e.toString());
        }

        Random random = new Random();
        String pangram = pangrams.get(random.nextInt(pangrams.size())).toUpperCase();

        int randomLetter = random.nextInt(pangram.length());
        String center = pangram.substring(randomLetter, randomLetter + 1).toUpperCase();

        try {
            InputStream inputStream = activity.getAssets().open("filtered.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            for (String line; (line = bufferedReader.readLine()) != null;) {
                if (line.toUpperCase().contains(center)
                        && SpellingUtil.containsNoOtherLetter(line, pangram)
                        && !matches.contains(line.toUpperCase())
                ) {
                    matches.add(line.toUpperCase());
                }
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            if (DEBUG) Log.e(TAG, "readFromFile: file not found!");
        } catch (Exception e) {
            if (DEBUG) Log.e(TAG, "readFromFile: " + e.toString());
        }

        StringBuilder letters = new StringBuilder();
        for(int i = 0; i < pangram.length(); i++) {
            String character = pangram.substring(i, i + 1).toUpperCase();
            if(!letters.toString().contains(character) && !character.equals(center)) {
                letters.append(character);
            }
        }

        return new String[]{letters.toString(), center};
    }

    @Override
    protected void onPostExecute(String[] strings) {
        MainActivity activity = activityRef.get();
        if(activity == null) return;
        activity.newGame(strings[0], strings[1], matches);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        MainActivity activity = activityRef.get();
        if(activity == null) return;
        activity.setRiddleProgress(values[0]);
    }
}
