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

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import xyz.zedler.patrick.buzz.Constants;
import xyz.zedler.patrick.buzz.databinding.FragmentBottomsheetFoundBinding;

public class FoundBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {

  private final static String TAG = "FoundBottomSheet";

  private FragmentBottomsheetFoundBinding binding;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState
  ) {
    binding = FragmentBottomsheetFoundBinding.inflate(inflater, container, false);

    Context context = getContext();
    Bundle bundle = getArguments();
    assert context != null && bundle != null;

    ArrayList<String> found = bundle.getStringArrayList(Constants.BOTTOM_SHEET.FOUND_WORDS);
    if (found != null) {
      Collections.sort(found);
      for (int i = 0; i < found.size(); i++) {
        found.set(i, found.get(i).toUpperCase());
      }
      binding.textFound.setText(TextUtils.join("\n", found));
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
