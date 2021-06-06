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

package xyz.zedler.patrick.buzz.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.Locale;
import xyz.zedler.patrick.buzz.Constants;
import xyz.zedler.patrick.buzz.util.LocaleUtil;
import xyz.zedler.patrick.buzz.util.VibratorUtil;

public class BaseActivity extends AppCompatActivity {

  private SharedPreferences sharedPrefs;
  private VibratorUtil vibratorUtil;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    vibratorUtil = new VibratorUtil(this);

    // DARK MODE

    AppCompatDelegate.setDefaultNightMode(
        sharedPrefs.getBoolean(
            Constants.PREF.DARK_MODE,
            Constants.DEF.DARK_MODE
        ) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    );

    // LANGUAGE

    Locale userLocale = LocaleUtil.getUserLocale(this);
    Locale.setDefault(userLocale);
    // base
    Resources resBase = getBaseContext().getResources();
    Configuration configBase = resBase.getConfiguration();
    configBase.setLocale(userLocale);
    resBase.updateConfiguration(configBase, resBase.getDisplayMetrics());
    // app
    Resources resApp = getApplicationContext().getResources();
    Configuration configApp = resApp.getConfiguration();
    configApp.setLocale(userLocale);
    resApp.updateConfiguration(configApp, getResources().getDisplayMetrics());

    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onResume() {
    super.onResume();
    vibratorUtil.onResume();
  }

  @Override
  protected void attachBaseContext(Context base) {
    Locale userLocale = LocaleUtil.getUserLocale(base);
    Locale.setDefault(userLocale);
    Resources resources = base.getResources();
    Configuration configuration = resources.getConfiguration();
    configuration.setLocale(userLocale);
    resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    super.attachBaseContext(base.createConfigurationContext(configuration));
  }

  @Override
  public void applyOverrideConfiguration(Configuration overrideConfiguration) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
      overrideConfiguration.setLocale(LocaleUtil.getUserLocale(this));
    }
    super.applyOverrideConfiguration(overrideConfiguration);
  }

  public void performHapticClick() {
    vibratorUtil.click();
  }

  public void performHapticDoubleClick() {
    vibratorUtil.doubleClick();
  }

  public void performHapticHeavyClick() {
    vibratorUtil.heavyClick();
  }

  public boolean hasVibrator() {
    return vibratorUtil.hasVibrator();
  }

  public void setVibratorEnabled(boolean enabled) {
    vibratorUtil.setEnabled(enabled);
  }

  public SharedPreferences getSharedPrefs() {
    return sharedPrefs;
  }

  public void showBottomSheet(BottomSheetDialogFragment bottomSheetDialog) {
    showBottomSheet(bottomSheetDialog, null);
  }

  public void showBottomSheet(BottomSheetDialogFragment bottomSheetDialog, Bundle bundle) {
    if (bundle != null) {
      bottomSheetDialog.setArguments(bundle);
    }
    bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.toString());
  }

  public boolean isEnglish() {
    return LocaleUtil.getUserLocale(this).getLanguage().equals("en");
  }
}
