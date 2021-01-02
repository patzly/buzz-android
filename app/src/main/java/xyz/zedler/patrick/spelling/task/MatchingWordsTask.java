package xyz.zedler.patrick.spelling.task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import xyz.zedler.patrick.spelling.MainActivity;
import xyz.zedler.patrick.spelling.util.SpellingUtil;

public class MatchingWordsTask extends AsyncTask<String[], Integer, ArrayList<String>> {

    private final static String TAG = MatchingWordsTask.class.getSimpleName();
    private final static boolean DEBUG = false;

    private final WeakReference<MainActivity> activityRef;

    public MatchingWordsTask(MainActivity activity) {
        activityRef = new WeakReference<>(activity);
    }

    @Override
    protected final ArrayList<String> doInBackground(String[]... params) {
        MainActivity activity = activityRef.get();
        if(activity == null) return null;

        ArrayList<String> matches = new ArrayList<>();
        try {
            InputStream inputStream = activity.getAssets().open("filtered.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            for (String line; (line = bufferedReader.readLine()) != null;) {
                if (line.toUpperCase().contains(params[0][1]) // center
                        && SpellingUtil.containsNoOtherLetter(line, params[0][0]) // pangram
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

        return matches;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        MainActivity activity = activityRef.get();
        if(activity == null) return;
        activity.processMatches(strings, false);
    }
}
