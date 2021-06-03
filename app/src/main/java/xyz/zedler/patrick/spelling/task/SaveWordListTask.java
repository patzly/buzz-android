/*
 * This file is part of Spelling Bee Android.
 *
 * Spelling Bee Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spelling Bee Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Spelling Bee Android. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2020-2021 by Patrick Zedler
 */

package xyz.zedler.patrick.spelling.task;

import android.os.AsyncTask;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SaveWordListTask extends AsyncTask<ArrayList<String>, Integer, String> {

  @SafeVarargs
  @Override
  protected final String doInBackground(ArrayList<String>... params) {
    File appDirectory = new File(
        Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents"
    );
    //noinspection ResultOfMethodCallIgnored
    appDirectory.mkdirs();
    File saveFilePath = new File(appDirectory, "processed.txt");
    try {
      FileOutputStream outputStream = new FileOutputStream(saveFilePath);
      OutputStreamWriter writer = new OutputStreamWriter(outputStream);
      for (int i = 0; i < params[0].size(); i++) {
        writer.append(params[0].get(i)).append("\n");
      }
      writer.close();
      outputStream.flush();
      outputStream.close();
    } catch (Exception ignored) {
    }
    return null;
  }
}
