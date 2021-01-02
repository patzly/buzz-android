package xyz.zedler.patrick.spelling.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibratorUtil {

    private final static long TICK = 15;

    private Vibrator vibrator;

    public VibratorUtil(Activity activity) {
        if(activity != null) {
            vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    public void tick() {
        vibrate(TICK);
    }

    public void vibrate(long duration) {
        if(vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                        VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
                );
            } else {
                vibrator.vibrate(duration);
            }
        }
    }
}
