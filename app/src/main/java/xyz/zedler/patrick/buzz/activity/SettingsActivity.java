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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import androidx.appcompat.app.AppCompatDelegate;
import xyz.zedler.patrick.buzz.Constants;
import xyz.zedler.patrick.buzz.Constants.DEF;
import xyz.zedler.patrick.buzz.Constants.PREF;
import xyz.zedler.patrick.buzz.R;
import xyz.zedler.patrick.buzz.behavior.ScrollBehavior;
import xyz.zedler.patrick.buzz.behavior.SystemBarBehavior;
import xyz.zedler.patrick.buzz.databinding.ActivitySettingsBinding;
import xyz.zedler.patrick.buzz.fragment.FeedbackBottomSheetDialogFragment;
import xyz.zedler.patrick.buzz.util.ClickUtil;
import xyz.zedler.patrick.buzz.util.IconUtil;
import xyz.zedler.patrick.buzz.util.RestartUtil;

public class SettingsActivity extends BaseActivity
    implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

  private final static String TAG = SettingsActivity.class.getSimpleName();
  private final static boolean DEBUG = false;

  private ActivitySettingsBinding binding;
  private SharedPreferences sharedPrefs;
  private ClickUtil clickUtil;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivitySettingsBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    sharedPrefs = getSharedPrefs();
    clickUtil = new ClickUtil();

    SystemBarBehavior systemBarBehavior = new SystemBarBehavior(this);
    systemBarBehavior.setAppBar(binding.appBarSettings);
    systemBarBehavior.setScroll(binding.scrollSettings, binding.linearSettingsContainer);
    systemBarBehavior.setUp();

    new ScrollBehavior(this).setUpScroll(
        binding.appBarSettings, binding.scrollSettings, true
    );

    binding.toolbarSettings.setOnMenuItemClickListener((MenuItem item) -> {
      int itemId = item.getItemId();
      if (itemId == R.id.action_about && clickUtil.isEnabled()) {
        startActivity(new Intent(this, AboutActivity.class));
      } else if (itemId == R.id.action_feedback && clickUtil.isEnabled()) {
        showBottomSheet(new FeedbackBottomSheetDialogFragment());
      }
      performHapticClick();
      return true;
    });

    binding.radioGroupSettingsLanguage.check(
        isEnglish() ? R.id.radio_settings_en : R.id.radio_settings_de
    );
    binding.radioGroupSettingsLanguage.setOnCheckedChangeListener((group, checkedId) -> {
      performHapticClick();
      if (checkedId == R.id.radio_settings_en) {
        setLanguage("en");
      } else if (checkedId == R.id.radio_settings_de) {
        setLanguage("de");
      }
    });

    binding.switchSettingDarkMode.setChecked(
        sharedPrefs.getBoolean(Constants.PREF.DARK_MODE, false)
    );
    binding.imageSettingDarkMode.setImageResource(
        sharedPrefs.getBoolean(Constants.PREF.DARK_MODE, false)
            ? R.drawable.ic_round_dark_mode_to_light_mode
            : R.drawable.ic_round_light_mode_to_dark_mode_anim
    );

    binding.switchSettingHaptic.setChecked(sharedPrefs.getBoolean(PREF.HAPTIC, DEF.HAPTIC));
    binding.linearSettingHaptic.setVisibility(
        hasVibrator() ? View.VISIBLE : View.GONE
    );

    ClickUtil.setOnClickListeners(
        this,
        binding.frameSettingsBack,
        binding.linearSettingDarkMode,
        binding.linearSettingHaptic
    );

    ClickUtil.setOnCheckedChangeListeners(
        this,
        binding.switchSettingDarkMode,
        binding.switchSettingHaptic
    );
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.frame_settings_back && clickUtil.isEnabled()) {
      performHapticClick();
      finish();
    } else if (id == R.id.linear_setting_dark_mode && clickUtil.isEnabled()) {
      binding.switchSettingDarkMode.setChecked(!binding.switchSettingDarkMode.isChecked());
    } else if (id == R.id.linear_setting_haptic) {
      binding.switchSettingHaptic.setChecked(!binding.switchSettingHaptic.isChecked());
    }
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    int id = buttonView.getId();
    if (id == R.id.switch_setting_dark_mode) {
      IconUtil.start(binding.imageSettingDarkMode);
      sharedPrefs.edit().putBoolean(Constants.PREF.DARK_MODE, isChecked).apply();
      new Handler(Looper.getMainLooper()).postDelayed(() -> {
        binding.imageSettingDarkMode.setImageResource(
            isChecked
                ? R.drawable.ic_round_dark_mode_to_light_mode
                : R.drawable.ic_round_light_mode_to_dark_mode_anim

        );
        AppCompatDelegate.setDefaultNightMode(
            isChecked
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        );
        onStart();
      }, 300);
    } else if (id == R.id.switch_setting_haptic) {
      IconUtil.start(binding.imageSettingHaptic);
      sharedPrefs.edit().putBoolean(PREF.HAPTIC, isChecked).apply();
      setVibratorEnabled(isChecked);
    }
    performHapticClick();
  }

  private void setLanguage(String code) {
    IconUtil.start(binding.imageSettingsLanguage);
    sharedPrefs.edit().putString(PREF.LANGUAGE, code).apply();
    new Handler().postDelayed(() -> RestartUtil.restartApp(this), 100);
  }
}
