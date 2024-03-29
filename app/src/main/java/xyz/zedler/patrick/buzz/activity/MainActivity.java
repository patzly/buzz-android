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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import xyz.zedler.patrick.buzz.Constants;
import xyz.zedler.patrick.buzz.Constants.DEF;
import xyz.zedler.patrick.buzz.Constants.EXTRA;
import xyz.zedler.patrick.buzz.Constants.PREF;
import xyz.zedler.patrick.buzz.R;
import xyz.zedler.patrick.buzz.behavior.ScrollBehavior;
import xyz.zedler.patrick.buzz.behavior.SystemBarBehavior;
import xyz.zedler.patrick.buzz.databinding.ActivityMainBinding;
import xyz.zedler.patrick.buzz.fragment.FoundBottomSheetDialogFragment;
import xyz.zedler.patrick.buzz.fragment.NewGameBottomSheetDialogFragment;
import xyz.zedler.patrick.buzz.task.MatchingWordsTask;
import xyz.zedler.patrick.buzz.util.ClickUtil;
import xyz.zedler.patrick.buzz.util.IconUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  private ActivityMainBinding binding;
  private SharedPreferences sharedPrefs;
  private ArrayList<String> letters;
  private ArrayList<String> matches;
  private ArrayList<String> found;
  private String center;
  private ClickUtil clickUtil;
  private int hints = 0;
  private long lastInvalid = 0;
  private String[] reactions;
  private boolean isEnglish;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    getWindow().setStatusBarColor(
        ContextCompat.getColor(
            this,
            Build.VERSION.SDK_INT >= 23 ? R.color.primary : R.color.status_bar_lollipop
        )
    );

    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    clickUtil = new ClickUtil();

    SystemBarBehavior systemBarBehavior = new SystemBarBehavior(this);
    systemBarBehavior.setAppBar(binding.appBarMain);
    systemBarBehavior.setContainer(binding.linearMainContainer);
    systemBarBehavior.setUp();

    new ScrollBehavior(this).setUpScroll(binding.appBarMain, null, false);

    isEnglish = isEnglish();

    binding.toolbarMain.setOnMenuItemClickListener(item -> {
      performHapticClick();
      int id = item.getItemId();
      if (id == R.id.action_hint && clickUtil.isEnabled()) {
        IconUtil.start(item.getIcon());
        newHint();
      } else if (id == R.id.action_settings && clickUtil.isEnabled()) {
        startActivity(new Intent(this, SettingsActivity.class));
      } else if (id == R.id.action_about && clickUtil.isEnabled()) {
        startActivity(new Intent(this, AboutActivity.class));
      } /*else if (id == R.id.action_help) {
        DialogFragment fragment = new RulesBottomSheetDialogFragment();
        fragment.show(getSupportFragmentManager(), fragment.toString());
      }*/
      return true;
    });

    ClickUtil.setOnClickListeners(
        this,
        binding.buttonMainFound,
        binding.buttonMainNewGame,
        binding.frameMainHexagons.frameHex1,
        binding.frameMainHexagons.frameHex2,
        binding.frameMainHexagons.frameHex3,
        binding.frameMainHexagons.frameHex4,
        binding.frameMainHexagons.frameHex5,
        binding.frameMainHexagons.frameHex6,
        binding.frameMainHexagons.frameHexCenter,
        binding.cardMainClear,
        binding.cardMainShuffle,
        binding.cardMainEnter
    );

    binding.cardMainClear.setOnLongClickListener(v -> {
      performHapticClick();
      IconUtil.start(binding.imageMainClear);
      clearLetters();
      return true;
    });

    reactions = getResources().getStringArray(R.array.reactions);

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
          .remove(isEnglish ? PREF.EN.FOUND : PREF.DE.FOUND)
          .putInt(isEnglish ? PREF.EN.HINTS : PREF.DE.HINTS, hints)
          .apply();
    } else {
      sharedPrefs.edit()
          .putString(isEnglish ? PREF.EN.FOUND : PREF.DE.FOUND, TextUtils.join("\n", found))
          .putInt(isEnglish ? PREF.EN.HINTS : PREF.DE.HINTS, hints)
          .apply();
    }
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putString(EXTRA.INPUT, binding.textMainInput.getText().toString());
  }

  @Override
  protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

    String input = savedInstanceState.getString(EXTRA.INPUT);
    if (input != null) {
      input = input.toLowerCase().replaceAll(center, getStyledCenter());
      binding.textMainInput.setText(
          Html.fromHtml(input.toUpperCase()), TextView.BufferType.SPANNABLE
      );
    }
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.button_main_found && clickUtil.isEnabled()) {
      if (found.size() > 0) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.BOTTOM_SHEET.FOUND_WORDS, found);
        DialogFragment fragment = new FoundBottomSheetDialogFragment();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), fragment.toString());
      } else {
        showMessage(R.string.msg_nothing_found);
      }
    } else if (id == R.id.button_main_new_game && clickUtil.isEnabled()) {
      Snackbar.make(
          binding.getRoot(),
          getString(R.string.msg_new_game),
          Snackbar.LENGTH_SHORT
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
      IconUtil.start(binding.imageMainClear);
      removeLastLetter();
    } else if (id == R.id.card_main_shuffle) {
      IconUtil.start(binding.imageMainShuffle);
      performHapticClick();
      shuffle();
    } else if (id == R.id.card_main_enter) {
      IconUtil.start(binding.imageMainEnter);
      processInput();
    }
  }

  private void addLetter(String letter) {
    performHapticClick();
    String input = binding.textMainInput.getText().toString().toLowerCase() + letter;
    input = input.replaceAll(center, getStyledCenter());
    if (binding.textMainInput.getText().length() <= 24) {
      // The longest German word in the list has 24 letters
      // 17 for English
      binding.textMainInput.setText(
          Html.fromHtml(input.toUpperCase()), TextView.BufferType.SPANNABLE
      );
    } else if (!wasInvalidCalledAlready()) {
      showMessage(R.string.msg_too_long);
      invalidInput();
    }
  }

  private void removeLastLetter() {
    performHapticClick();
    String input = binding.textMainInput.getText().toString().toLowerCase();
    if (input.length() > 0) {
      input = input.substring(0, input.length() - 1);
    }
    input = input.replaceAll(center, getStyledCenter());
    binding.textMainInput.setText(
        Html.fromHtml(input.toUpperCase()), TextView.BufferType.SPANNABLE
    );
  }

  private void clearLetters() {
    binding.textMainInput.animate().alpha(0).withEndAction(() -> {
      binding.textMainInput.setText(null);
      binding.textMainInput.setAlpha(1);
    }).setDuration(Constants.ANIMATION).start();
  }

  public void newGame(String letters, String center, ArrayList<String> matches) {
    sharedPrefs.edit()
        .putString(isEnglish ? PREF.EN.LETTERS : PREF.DE.LETTERS, letters)
        .putString(isEnglish ? PREF.EN.CENTER : PREF.DE.CENTER, center)
        .putString(isEnglish ? PREF.EN.FOUND : PREF.DE.FOUND, null)
        .putInt(isEnglish ? PREF.EN.HINTS : PREF.DE.HINTS, 0)
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
      binding.textMainInput.setText(Html.fromHtml(hint), TextView.BufferType.SPANNABLE);
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

    String foundString = sharedPrefs.getString(
        isEnglish ? PREF.EN.FOUND : PREF.DE.FOUND, null
    );
    if (foundString == null || foundString.isEmpty()) {
      found = new ArrayList<>();
    } else {
      found = new ArrayList<>(Arrays.asList(TextUtils.split(foundString, "\n")));
    }

    if (animated) {
      changeAllCount(matches.size());
      changeFoundCount(found.size());
    } else {
      binding.progressMain.setMax(matches.size());
      binding.progressMain.setProgress(found.size());
      /*binding.textProgressAll.setText(String.valueOf(matches.size()));
      binding.textProgressFound.setText(String.valueOf(found.size()));*/
    }
  }

  private void fillWithLetters(boolean animated, boolean matchesNeed) {
    letters = getLetters();
    center = getCenter();

    hints = sharedPrefs.getInt(isEnglish ? PREF.EN.HINTS : PREF.DE.HINTS, 0);

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
      binding.frameMainHexagons.textHex1.setText(letters.get(0).toUpperCase());
      binding.frameMainHexagons.textHex2.setText(letters.get(1).toUpperCase());
      binding.frameMainHexagons.textHex3.setText(letters.get(2).toUpperCase());
      binding.frameMainHexagons.textHex4.setText(letters.get(3).toUpperCase());
      binding.frameMainHexagons.textHex5.setText(letters.get(4).toUpperCase());
      binding.frameMainHexagons.textHex6.setText(letters.get(5).toUpperCase());
      if (animated) {
        animateLetters(1, true);
      }
    }, animated ? Constants.ANIMATION : 0);
    new Handler(Looper.getMainLooper()).postDelayed(
        () -> binding.frameMainHexagons.textHexCenter.setText(center.toUpperCase()),
        animated ? Constants.ANIMATION : 0
    );
  }

  private void shuffle() {
    letters = getLetters();
    animateLetters(0, false);
    new Handler(Looper.getMainLooper()).postDelayed(() -> {
      binding.frameMainHexagons.textHex1.setText(letters.get(0).toUpperCase());
      binding.frameMainHexagons.textHex2.setText(letters.get(1).toUpperCase());
      binding.frameMainHexagons.textHex3.setText(letters.get(2).toUpperCase());
      binding.frameMainHexagons.textHex4.setText(letters.get(3).toUpperCase());
      binding.frameMainHexagons.textHex5.setText(letters.get(4).toUpperCase());
      binding.frameMainHexagons.textHex6.setText(letters.get(5).toUpperCase());
      animateLetters(1, false);
    }, Constants.ANIMATION);
  }

  private void animateLetters(float alpha, boolean animateCenter) {
    int duration = Constants.ANIMATION;
    binding.frameMainHexagons.textHex1.animate().alpha(alpha).setDuration(duration).start();
    binding.frameMainHexagons.textHex2.animate().alpha(alpha).setDuration(duration).start();
    binding.frameMainHexagons.textHex3.animate().alpha(alpha).setDuration(duration).start();
    binding.frameMainHexagons.textHex4.animate().alpha(alpha).setDuration(duration).start();
    binding.frameMainHexagons.textHex5.animate().alpha(alpha).setDuration(duration).start();
    binding.frameMainHexagons.textHex6.animate().alpha(alpha).setDuration(duration).start();
    if (animateCenter) {
      binding.frameMainHexagons.textHexCenter.animate().alpha(alpha).setDuration(duration).start();
    }
  }

  private ArrayList<String> getLetters() {
    String string = sharedPrefs.getString(
        isEnglish ? PREF.EN.LETTERS : PREF.DE.LETTERS, isEnglish ? DEF.EN.LETTERS : DEF.DE.LETTERS
    );
    Log.i(TAG, "getLetters: " + isEnglish + ", " + PREF.EN.LETTERS);
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
    String center = sharedPrefs.getString(
        isEnglish ? PREF.EN.CENTER : PREF.DE.CENTER, isEnglish ? DEF.EN.CENTER : DEF.DE.CENTER
    );
    assert center != null;
    return center;
  }

  private void changeFoundCount(int count) {
    binding.progressMain.setProgressCompat(count, true);
    /*binding.textProgressFound.animate().alpha(0).withEndAction(() -> {
      binding.textProgressFound.setText(String.valueOf(count));
      binding.textProgressFound.animate().alpha(1).setDuration(Constants.ANIMATION).start();
    }).setDuration(Constants.ANIMATION).start();*/
  }

  private void changeAllCount(int count) {
    binding.progressMain.setMax(count);
    /*binding.textProgressAll.animate().alpha(0).withEndAction(() -> {
      binding.textProgressAll.setText(String.valueOf(count));
      binding.textProgressAll.animate().alpha(1).setDuration(Constants.ANIMATION).start();
    }).setDuration(Constants.ANIMATION).start();*/
  }

  public void processInput() {
    String input = binding.textMainInput.getText().toString().toLowerCase();
    if (input.equals("")) {
      performHapticDoubleClick();
    } else if (input.length() < 4 && wasInvalidCalledAlready()) {
      showMessage(R.string.msg_too_short);
      invalidInput();
    } else if (!input.contains(center) && !wasInvalidCalledAlready()) {
      showMessage(R.string.msg_missing_center_letter);
      invalidInput();
    } else if (matches.contains(input) && !found.contains(input)) {
      found.add(input);
      changeFoundCount(found.size());
      showReaction(input.length());
      performHapticClick();
      IconUtil.start(binding.imageMainLogo);
      new Handler(Looper.getMainLooper()).postDelayed(this::clearLetters, 250);
    } else if (found.contains(input)) {
      showMessage(R.string.msg_already_found);
      invalidInput();
    } else if (!matches.contains(input)) {
      showMessage(R.string.msg_not_on_list);
      invalidInput();
    }
  }

  private void showReaction(int length) {
    int reaction = 0;
    if (length > 6 && length < 9) {
      reaction = 1;
    } else if (length > 9 && length < 12) {
      reaction = 2;
    } else if (length > 12) {
      reaction = 3;
    }
    Snackbar.make(binding.getRoot(), reactions[reaction], 750).show();
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
    performHapticDoubleClick();
    binding.textMainInput.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    new Handler(Looper.getMainLooper()).postDelayed(this::clearLetters, 750);
  }

  private boolean wasInvalidCalledAlready() {
    if (SystemClock.elapsedRealtime() - lastInvalid < 800) {
      return true;
    }
    lastInvalid = SystemClock.elapsedRealtime();
    return false;
  }

  @SuppressLint("ShowToast")
  private void showMessage(@StringRes int resId) {
    Snackbar.make(binding.getRoot(), getString(resId), 750).show();
  }

  private String getStyledCenter() {
    return "<font color='#deb853'>" + center + "</font>";
  }
}
