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

package xyz.zedler.patrick.buzz.task;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import xyz.zedler.patrick.buzz.MainActivity;
import xyz.zedler.patrick.buzz.util.SpellingUtil;

public class MatchingWordsTask extends AsyncTask<String[], Integer, ArrayList<String>> {

  private final static String TAG = MatchingWordsTask.class.getSimpleName();

  private final WeakReference<MainActivity> activityRef;

  /**
   * That's only a shorter version of the NewGameTask, just without the pangram search because it is
   * already selected at this moment
   */
  public MatchingWordsTask(MainActivity activity) {
    activityRef = new WeakReference<>(activity);
  }

  @Override
  protected final ArrayList<String> doInBackground(String[]... params) {
    MainActivity activity = activityRef.get();
    if (activity == null) {
      return null;
    }
    ArrayList<String> matches = new ArrayList<>();
    try {
      InputStream inputStream = activity.getAssets().open("words_german_valid.txt");
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      for (String line; (line = bufferedReader.readLine()) != null; ) {
        if (line.contains(params[0][1]) // center
            && SpellingUtil.containsNoOtherLetter(line, params[0][0]) // pangram
            && !matches.contains(line)
        ) {
          matches.add(line);
        }
      }
      inputStream.close();
    } catch (Exception e) {
      Log.e(TAG, "readFromFile: ", e);
    }
    return matches;
  }

  @Override
  protected void onPostExecute(ArrayList<String> strings) {
    MainActivity activity = activityRef.get();
    if (activity == null) {
      return;
    }
    activity.processMatches(strings, false);
  }
}
