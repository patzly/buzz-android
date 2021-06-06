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

package xyz.zedler.patrick.buzz.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.Collections;
import xyz.zedler.patrick.buzz.Constants;
import xyz.zedler.patrick.buzz.R;
import xyz.zedler.patrick.buzz.activity.MainActivity;
import xyz.zedler.patrick.buzz.databinding.FragmentBottomsheetNewGameBinding;
import xyz.zedler.patrick.buzz.task.NewGameTask;

public class NewGameBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {

  private final static String TAG = "NewGameBottomSheet";

  private FragmentBottomsheetNewGameBinding binding;

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new BottomSheetDialog(requireContext(), R.style.Theme_Buzz_BottomSheetDialog);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState
  ) {
    binding = FragmentBottomsheetNewGameBinding.inflate(
        inflater, container, false
    );

    setCancelable(false);

    Bundle bundle = getArguments();
    assert bundle != null;

    binding.textNewGameHints.setText(
        getString(
            R.string.msg_hints_used,
            bundle.getInt(Constants.BOTTOM_SHEET.HINTS_USED, 0)
        )
    );

    String letters = bundle.getString(
        Constants.BOTTOM_SHEET.LETTERS, Constants.DEFAULT.LETTERS
    );
    String center = bundle.getString(Constants.BOTTOM_SHEET.CENTER, Constants.DEFAULT.CENTER);
    String all = "<font color='#deb853'>" + center + "</font>" + letters;
    binding.textNewGameLetters.setText(
        Html.fromHtml(all.toUpperCase()), TextView.BufferType.SPANNABLE
    );

    binding.buttonNewGame.setOnClickListener(v -> {
      new NewGameTask((MainActivity) getActivity()).execute();
      dismiss();
    });

    ArrayList<String> missed = bundle.getStringArrayList(Constants.BOTTOM_SHEET.MISSED_WORDS);
    if (missed != null) {
      Collections.sort(missed);
      for (int i = 0; i < missed.size(); i++) {
        missed.set(i, missed.get(i).toUpperCase());
      }
      binding.textNewGameMissed.setText(TextUtils.join("\n", missed));
    }

    return binding.getRoot();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  @NonNull
  @Override
  public String toString() {
    return TAG;
  }
}
