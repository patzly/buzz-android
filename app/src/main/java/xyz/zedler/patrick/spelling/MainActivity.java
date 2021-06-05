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

package xyz.zedler.patrick.spelling;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import xyz.zedler.patrick.spelling.databinding.ActivityMainBinding;
import xyz.zedler.patrick.spelling.fragment.FoundBottomSheetDialogFragment;
import xyz.zedler.patrick.spelling.fragment.NewGameBottomSheetDialogFragment;
import xyz.zedler.patrick.spelling.fragment.RulesBottomSheetDialogFragment;
import xyz.zedler.patrick.spelling.task.MatchingWordsTask;
import xyz.zedler.patrick.spelling.util.ClickUtil;
import xyz.zedler.patrick.spelling.util.ConfettiUtil;
import xyz.zedler.patrick.spelling.util.IconUtil;
import xyz.zedler.patrick.spelling.util.VibratorUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  private ActivityMainBinding binding;
  private SharedPreferences sharedPrefs;
  private ArrayList<String> letters;
  private ArrayList<String> matches;
  private ArrayList<String> found;
  private String center;
  private VibratorUtil vibrator;
  private ClickUtil clickUtil, clickUtilLogo;
  private int hints = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    getWindow().setStatusBarColor(
        ContextCompat.getColor(
            this,
            Build.VERSION.SDK_INT >= 23 ? R.color.primary : R.color.status_bar_lollipop
        )
    );

    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    vibrator = new VibratorUtil(this);
    clickUtil = new ClickUtil();
    clickUtilLogo = new ClickUtil(600);

    binding.toolbar.setOnMenuItemClickListener(item -> {
      if (clickUtil.isDisabled()) {
        return false;
      }
      int id = item.getItemId();
      if (id == R.id.action_hint) {
        IconUtil.start(item.getIcon());
        vibrator.tick();
        newHint();
      } else if (id == R.id.action_help) {
        DialogFragment fragment = new RulesBottomSheetDialogFragment();
        fragment.show(getSupportFragmentManager(), fragment.toString());
      }
      return true;
    });

    ClickUtil.setOnClickListeners(
        this,
        binding.frameAppBar,
        binding.buttonFound,
        binding.buttonNewGame,
        binding.frameHexagons.frameHex1,
        binding.frameHexagons.frameHex2,
        binding.frameHexagons.frameHex3,
        binding.frameHexagons.frameHex4,
        binding.frameHexagons.frameHex5,
        binding.frameHexagons.frameHex6,
        binding.frameHexagons.frameHexCenter,
        binding.cardMainClear,
        binding.cardMainShuffle,
        binding.cardMainEnter
    );

    binding.cardMainClear.setOnLongClickListener(v -> {
      vibrator.tick();
      IconUtil.start(binding.imageClear);
      clearLetters();
      return true;
    });

    fillWithLetters(false, true);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (found == null || found.isEmpty()) {
      sharedPrefs.edit()
          .remove(Constants.PREF.FOUND)
          .putInt(Constants.PREF.HINTS, hints)
          .apply();
    } else {
      sharedPrefs.edit()
          .putString(Constants.PREF.FOUND, TextUtils.join("\n", found))
          .putInt(Constants.PREF.HINTS, hints)
          .apply();
    }
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.button_found) {
      if (clickUtil.isDisabled()) {
        return;
      }
      if (found.size() > 0) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.BOTTOM_SHEET.FOUND_WORDS, found);
        DialogFragment fragment = new FoundBottomSheetDialogFragment();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), fragment.toString());
      } else {
        showMessage(R.string.msg_not_found_any);
      }
    } else if (id == R.id.button_new_game) {
      if (clickUtil.isDisabled()) {
        return;
      }
      Snackbar.make(
          binding.getRoot(),
          getString(R.string.msg_new_game),
          Snackbar.LENGTH_SHORT
      ).setActionTextColor(
          ContextCompat.getColor(this, R.color.retro_green_fg_invert)
      ).setAction(getString(R.string.action_new_game), view -> {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BOTTOM_SHEET.LETTERS, TextUtils.join("", letters));
        bundle.putString(Constants.BOTTOM_SHEET.CENTER, center);
        bundle.putStringArrayList(Constants.BOTTOM_SHEET.MISSED_WORDS, getMissedWords());
        bundle.putInt(Constants.BOTTOM_SHEET.HINTS_USED, hints);
        DialogFragment fragment = new NewGameBottomSheetDialogFragment();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), fragment.toString());
      }).show();
    } else if (id == R.id.frame_hex_1) {
      addLetter(letters.get(0));
    } else if (id == R.id.frame_hex_2) {
      addLetter(letters.get(1));
    } else if (id == R.id.frame_hex_3) {
      addLetter(letters.get(2));
    } else if (id == R.id.frame_hex_4) {
      addLetter(letters.get(3));
    } else if (id == R.id.frame_hex_5) {
      addLetter(letters.get(4));
    } else if (id == R.id.frame_hex_6) {
      addLetter(letters.get(5));
    } else if (id == R.id.frame_hex_center) {
      addLetter(center);
    } else if (id == R.id.card_main_clear) {
      IconUtil.start(binding.imageClear);
      removeLastLetter();
    } else if (id == R.id.card_main_shuffle) {
      IconUtil.start(binding.imageShuffle);
      vibrator.tick();
      shuffle();
    } else if (id == R.id.card_main_enter) {
      IconUtil.start(binding.imageEnter);
      vibrator.tick();
      if (isValid()) {
        processInput();
      }
    } else if (id == R.id.frame_app_bar) {
      if (clickUtilLogo.isDisabled()) {
        return;
      }
      IconUtil.start(binding.imageLogo);
    }
  }

  private void addLetter(String letter) {
    vibrator.tick();
    String input = binding.textInput.getText().toString().toLowerCase() + letter;
    input = input.replaceAll(center, getStyledCenter());
    if (binding.textInput.getText().length() < 20) {
      binding.textInput.setText(Html.fromHtml(input.toUpperCase()), TextView.BufferType.SPANNABLE);
    } else {
      showMessage(R.string.msg_too_long);
      invalidInput();
    }
  }

  private void removeLastLetter() {
    vibrator.tick();
    String input = binding.textInput.getText().toString().toLowerCase();
    if (input.length() > 0) {
      input = input.substring(0, input.length() - 1);
    }
    input = input.replaceAll(center, getStyledCenter());
    binding.textInput.setText(Html.fromHtml(input.toUpperCase()), TextView.BufferType.SPANNABLE);
  }

  private void clearLetters() {
    binding.textInput.animate().alpha(0).withEndAction(() -> {
      binding.textInput.setText(null);
      binding.textInput.setAlpha(1);
    }).setDuration(Constants.ANIMATION).start();
  }

  public void newGame(String letters, String center, ArrayList<String> matches) {
    sharedPrefs.edit()
        .putString(Constants.PREF.LETTERS, letters)
        .putString(Constants.PREF.CENTER, center)
        .putString(Constants.PREF.FOUND, null)
        .putInt(Constants.PREF.HINTS, 0)
        .apply();
    fillWithLetters(true, false);
    clearLetters();
    processMatches(matches, true);
  }

  private void newHint() {
    if (hints < Constants.HINTS_MAX) {
      ArrayList<String> notFound = getMissedWords();
      String hint = notFound.get(new Random().nextInt(notFound.size())).substring(0, 3);
      hint = hint.replaceAll(center, getStyledCenter());
      binding.textInput.setText(Html.fromHtml(hint), TextView.BufferType.SPANNABLE);
      hints++;
      if (hints < Constants.HINTS_MAX) {
        Snackbar.make(
            binding.getRoot(),
            getString(R.string.msg_hints, Constants.HINTS_MAX - hints),
            Snackbar.LENGTH_SHORT
        ).show();
      } else {
        showMessage(R.string.msg_all_hints_used);
      }
    } else {
      showMessage(R.string.msg_all_hints_used);
    }
  }

  public void processMatches(ArrayList<String> matches, boolean animated) {
    this.matches = matches;

    String foundString = sharedPrefs.getString(Constants.PREF.FOUND, null);
    if (foundString == null || foundString.isEmpty()) {
      found = new ArrayList<>();
    } else {
      found = new ArrayList<>(Arrays.asList(TextUtils.split(foundString, "\n")));
    }

    if (animated) {
      changeAllCount(matches.size());
      changeFoundCount(found.size());
    } else {
      binding.progress.setMax(matches.size());
      binding.progress.setProgress(found.size());
      binding.textProgressAll.setText(String.valueOf(matches.size()));
      binding.textProgressFound.setText(String.valueOf(found.size()));
    }
  }

  private void fillWithLetters(boolean animated, boolean matchesNeed) {
    letters = getLetters();
    center = getCenter();

    hints = sharedPrefs.getInt(Constants.PREF.HINTS, 0);

    // LOAD MATCHES
    if (matchesNeed) {
      new MatchingWordsTask(this).execute(
          new String[]{TextUtils.join("", letters) + center, center}
      );
    }

    if (animated) {
      animateLetters(0, true);
    }
    new Handler(Looper.getMainLooper()).postDelayed(() -> {
      binding.frameHexagons.textHex1.setText(letters.get(0).toUpperCase());
      binding.frameHexagons.textHex2.setText(letters.get(1).toUpperCase());
      binding.frameHexagons.textHex3.setText(letters.get(2).toUpperCase());
      binding.frameHexagons.textHex4.setText(letters.get(3).toUpperCase());
      binding.frameHexagons.textHex5.setText(letters.get(4).toUpperCase());
      binding.frameHexagons.textHex6.setText(letters.get(5).toUpperCase());
      if (animated) {
        animateLetters(1, true);
      }
    }, animated ? Constants.ANIMATION : 0);
    new Handler(Looper.getMainLooper()).postDelayed(
        () -> binding.frameHexagons.textHexCenter.setText(center.toUpperCase()),
        animated ? Constants.ANIMATION : 0
    );
  }

  private void shuffle() {
    letters = getLetters();
    animateLetters(0, false);
    new Handler(Looper.getMainLooper()).postDelayed(() -> {
      binding.frameHexagons.textHex1.setText(letters.get(0).toUpperCase());
      binding.frameHexagons.textHex2.setText(letters.get(1).toUpperCase());
      binding.frameHexagons.textHex3.setText(letters.get(2).toUpperCase());
      binding.frameHexagons.textHex4.setText(letters.get(3).toUpperCase());
      binding.frameHexagons.textHex5.setText(letters.get(4).toUpperCase());
      binding.frameHexagons.textHex6.setText(letters.get(5).toUpperCase());
      animateLetters(1, false);
    }, Constants.ANIMATION);
  }

  private void animateLetters(float alpha, boolean animateCenter) {
    int duration = Constants.ANIMATION;
    binding.frameHexagons.textHex1.animate().alpha(alpha).setDuration(duration).start();
    binding.frameHexagons.textHex2.animate().alpha(alpha).setDuration(duration).start();
    binding.frameHexagons.textHex3.animate().alpha(alpha).setDuration(duration).start();
    binding.frameHexagons.textHex4.animate().alpha(alpha).setDuration(duration).start();
    binding.frameHexagons.textHex5.animate().alpha(alpha).setDuration(duration).start();
    binding.frameHexagons.textHex6.animate().alpha(alpha).setDuration(duration).start();
    if (animateCenter) {
      binding.frameHexagons.textHexCenter.animate().alpha(alpha).setDuration(duration).start();
    }
  }

  private ArrayList<String> getLetters() {
    String string = sharedPrefs.getString(Constants.PREF.LETTERS, Constants.DEFAULT.LETTERS);
    assert string != null;
    List<String> letters = new ArrayList<>(Arrays.asList(string.split("")));
    if (letters.get(0).isEmpty()) {
      letters.remove(0);
    }
    Collections.shuffle(letters);
    return new ArrayList<>(letters);
  }

  @NonNull
  private String getCenter() {
    String center = sharedPrefs.getString(Constants.PREF.CENTER, Constants.DEFAULT.CENTER);
    assert center != null;
    return center;
  }

  private void changeFoundCount(int count) {
    binding.progress.setProgressCompat(count, true);
    binding.textProgressFound.animate().alpha(0).withEndAction(() -> {
      binding.textProgressFound.setText(String.valueOf(count));
      binding.textProgressFound.animate().alpha(1).setDuration(Constants.ANIMATION).start();
    }).setDuration(Constants.ANIMATION).start();
  }

  private void changeAllCount(int count) {
    binding.progress.setMax(count);
    binding.textProgressAll.animate().alpha(0).withEndAction(() -> {
      binding.textProgressAll.setText(String.valueOf(count));
      binding.textProgressAll.animate().alpha(1).setDuration(Constants.ANIMATION).start();
    }).setDuration(Constants.ANIMATION).start();
  }

  private boolean isValid() {
    String input = binding.textInput.getText().toString().toLowerCase();
    if (input.equals("")) {
      return false;
    } else if (input.length() < 4) {
      showMessage(R.string.msg_too_short);
      invalidInput();
      return false;
    } else if (!input.contains(center)) {
      showMessage(R.string.msg_missing_center_letter);
      invalidInput();
      return false;
    }
    return true;
  }

  public void processInput() {
    String input = binding.textInput.getText().toString().toLowerCase();
    if (matches.contains(input) && !found.contains(input)) {
      found.add(input);
      changeFoundCount(found.size());
      ConfettiUtil.explode(binding.frameContainer, binding.cardMainShuffle);
      new Handler(Looper.getMainLooper()).postDelayed(this::clearLetters, 200);
    } else if (found.contains(input)) {
      showMessage(R.string.msg_already_found);
      invalidInput();
    } else if (!matches.contains(input)) {
      showMessage(R.string.msg_not_on_word_list);
      invalidInput();
    }
  }

  private ArrayList<String> getMissedWords() {
    ArrayList<String> missed = new ArrayList<>();
    for (int i = 0; i < matches.size(); i++) {
      if (!found.contains(matches.get(i))) {
        missed.add(matches.get(i));
      }
    }
    return missed;
  }

  private void invalidInput() {
    binding.textInput.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    new Handler(Looper.getMainLooper()).postDelayed(this::clearLetters, 700);
  }

  private void showMessage(@StringRes int resId) {
    Snackbar.make(binding.getRoot(), getString(resId), Snackbar.LENGTH_SHORT).show();
  }

  private String getStyledCenter() {
    return "<font color='#deb853'>" + center + "</font>";
  }
}
