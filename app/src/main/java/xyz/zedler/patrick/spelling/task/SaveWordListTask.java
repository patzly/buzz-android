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
        File saveFilePath = new File(appDirectory, "DeutschSpellingBee.txt");
        try {
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            for (int i = 0; i < params[0].size(); i++) {
                writer.append(params[0].get(i)).append("\n");
            }
            writer.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception ignored) { }
        return null;
    }
}
