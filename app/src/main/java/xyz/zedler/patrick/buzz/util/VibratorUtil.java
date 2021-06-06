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

package xyz.zedler.patrick.buzz.util;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class VibratorUtil {

  private final Vibrator vibrator;
  private final boolean hasVibrator;

  public static final long CLICK = 20;
  public static final long HEAVY = 50;

  public VibratorUtil(Context context) {
    vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    hasVibrator = hasVibrator();
  }

  public void vibrate(long duration) {
    if (!hasVibrator) {
      return;
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
    } else {
      vibrator.vibrate(duration);
    }
  }

  private void vibrate(int effectId) {
    if (hasVibrator && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      vibrator.vibrate(VibrationEffect.createPredefined(effectId));
    }
  }

  public void click() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      vibrate(VibrationEffect.EFFECT_CLICK);
    } else {
      vibrate(CLICK);
    }
  }

  public void doubleClick() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      vibrate(VibrationEffect.EFFECT_DOUBLE_CLICK);
    } else {
      vibrate(CLICK);
    }
  }

  public void heavyClick() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      vibrate(VibrationEffect.EFFECT_HEAVY_CLICK);
    } else {
      vibrate(HEAVY);
    }
  }

  public boolean hasVibrator() {
    return vibrator.hasVibrator();
  }
}
