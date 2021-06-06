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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import xyz.zedler.patrick.buzz.databinding.FragmentBottomsheetChangelogBinding;
import xyz.zedler.patrick.buzz.util.BulletUtil;
import xyz.zedler.patrick.buzz.util.ResUtil;

public class ChangelogBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {

  private final static String TAG = "ChangelogBottomSheet";

  private FragmentBottomsheetChangelogBinding binding;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState
  ) {
    binding = FragmentBottomsheetChangelogBinding.inflate(
        inflater, container, false
    );

    Context context = getContext();
    assert context != null;

    binding.textChangelog.setText(
        BulletUtil.makeBulletList(
            getContext(),
            6,
            2,
            "- ",
            ResUtil.readFromFile(getContext(), "changelog.txt"),
            "New:", "Improved:", "Fixed:"),
        TextView.BufferType.SPANNABLE
    );

    return binding.getRoot();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  @NonNull
  @Override
  public String toString() {
    return TAG;
  }
}
