package xyz.zedler.patrick.spelling.util;

import android.os.SystemClock;
import android.view.View;

public class ClickUtil {

    private long idle = 500;
    private long lastClick;

    public ClickUtil() {
        lastClick = 0;
    }

    public ClickUtil(long idle) {
        lastClick = 0;
        this.idle = idle;
    }

    public void update() {
        lastClick = SystemClock.elapsedRealtime();
    }

    public boolean isDisabled() {
        if (SystemClock.elapsedRealtime() - lastClick < idle) return true;
        update();
        return false;
    }

    public static void setOnClickListeners(View.OnClickListener listener, View... views) {
        for (View view : views) view.setOnClickListener(listener);
    }
}
