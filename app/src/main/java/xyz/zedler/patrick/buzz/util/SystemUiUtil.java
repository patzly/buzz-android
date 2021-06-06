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
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.Window;

public class SystemUiUtil {

  public final static int COLOR_SCRIM = 0x55000000;
  public final static int COLOR_SCRIM_OPAQUE = 0xFFAAAAAA;
  public final static int COLOR_SCRIM_DARK = 0xB31E1F22;
  public final static int COLOR_SCRIM_LIGHT = 0xB3FFFFFF;

  public static void layoutEdgeToEdge(Window window) {
    if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
      window.setDecorFitsSystemWindows(false);
    } else {
      final int decorFitsFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
      final View decorView = window.getDecorView();
      decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | decorFitsFlags);
    }
  }

  public static void setLightNavigationBar(Window window) {
    // TODO: SDK 30 method doesn't work
    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      window.getInsetsController().setSystemBarsAppearance(
          WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
          WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
      );
    } else*/
    if (Build.VERSION.SDK_INT >= VERSION_CODES.O_MR1) {
      final View decorView = window.getDecorView();
      decorView.setSystemUiVisibility(
          decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
      );
    }
  }

  public static void setLightStatusBar(Window window) {
    // TODO: SDK 30 method doesn't work
    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      window.getInsetsController().setSystemBarsAppearance(
          WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
          WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
      );
    } else*/
    if (Build.VERSION.SDK_INT >= VERSION_CODES.O_MR1) {
      final View decorView = window.getDecorView();
      decorView.setSystemUiVisibility(
          decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
      );
    }
  }

  public static boolean isDarkModeActive(Context context) {
    int uiMode = context.getResources().getConfiguration().uiMode;
    return (uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
  }

  public static boolean isNavigationModeGesture(Context context) {
    final int NAV_GESTURE = 2;
    Resources resources = context.getResources();
    int resourceId = resources.getIdentifier(
        "config_navBarInteractionMode", "integer", "android"
    );
    int mode = resourceId > 0 ? resources.getInteger(resourceId) : 0;
    return mode == NAV_GESTURE;
  }

  public static boolean isOrientationPortrait(Context context) {
    int orientation = context.getResources().getConfiguration().orientation;
    return orientation == Configuration.ORIENTATION_PORTRAIT;
  }
}
