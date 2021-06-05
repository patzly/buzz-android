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
import java.util.Random;
import xyz.zedler.patrick.buzz.MainActivity;
import xyz.zedler.patrick.buzz.util.SpellingUtil;

public class NewGameTask extends AsyncTask<Void, Integer, String[]> {

  private final static String TAG = NewGameTask.class.getSimpleName();

  private final WeakReference<MainActivity> activityRef;
  private final ArrayList<String> matches = new ArrayList<>();

  public NewGameTask(MainActivity activity) {
    activityRef = new WeakReference<>(activity);
  }

  @Override
  protected final String[] doInBackground(Void... params) {
    MainActivity activity = activityRef.get();
    if (activity == null) {
      return null;
    }
    ArrayList<String> pangrams = new ArrayList<>();
    try {
      InputStream inputStream = activity.getAssets().open("words_german_pangrams.txt");
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      for (String line; (line = bufferedReader.readLine()) != null; ) {
        pangrams.add(line);
      }
      inputStream.close();
    } catch (Exception e) {
      Log.e(TAG, "readFromFile: ", e);
    }

    Random random = new Random();
    String pangram = pangrams.get(random.nextInt(pangrams.size()));

    int randomLetter = random.nextInt(pangram.length());
    String center = pangram.substring(randomLetter, randomLetter + 1);

    try {
      InputStream inputStream = activity.getAssets().open("words_german_valid.txt");
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      for (String line; (line = bufferedReader.readLine()) != null; ) {
        if (line.contains(center)
            && SpellingUtil.containsNoOtherLetter(line, pangram)
            && !matches.contains(line)
        ) {
          matches.add(line);
        }
      }
      inputStream.close();
    } catch (Exception e) {
      Log.e(TAG, "readFromFile: ", e);
    }

    StringBuilder letters = new StringBuilder();
    for (int i = 0; i < pangram.length(); i++) {
      String character = pangram.substring(i, i + 1);
      if (!letters.toString().contains(character) && !character.equals(center)) {
        letters.append(character);
      }
    }

    return new String[]{letters.toString(), center};
  }

  @Override
  protected void onPostExecute(String[] strings) {
    MainActivity activity = activityRef.get();
    if (activity == null) {
      return;
    }
    activity.newGame(strings[0], strings[1], matches);
  }
}
