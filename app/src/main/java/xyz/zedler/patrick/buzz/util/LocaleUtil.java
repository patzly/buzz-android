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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import androidx.preference.PreferenceManager;
import java.util.Locale;
import xyz.zedler.patrick.buzz.Constants;

public class LocaleUtil {

  public static Locale getDeviceLocale() {
    Locale device;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      device = Resources.getSystem().getConfiguration().getLocales().get(0);
    } else {
      device = Resources.getSystem().getConfiguration().locale;
    }
    return device;
  }

  public static Locale getUserLocale(Context context) {
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    String code = sharedPrefs.getString(Constants.PREF.LANGUAGE, null);
    if (code == null) {
      return getLocaleFromCode(getDeviceLocale().getLanguage());
    }
    try {
      return LocaleUtil.getLocaleFromCode(code);
    } catch (Exception e) {
      return getLocaleFromCode(getDeviceLocale().getLanguage());
    }
  }

  public static Locale getLocaleFromCode(String languageCode) {
    String[] codeParts = languageCode.split("_");
    if (codeParts.length > 1) {
      return new Locale(codeParts[0], codeParts[1]);
    } else {
      return new Locale(languageCode);
    }
  }
}
