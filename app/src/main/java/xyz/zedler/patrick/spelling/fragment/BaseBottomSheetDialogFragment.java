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

package xyz.zedler.patrick.spelling.fragment;

import android.app.Activity;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
      BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
      if (dialog == null) {
        return;
      }

      View sheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
      if (sheet == null) {
        return;
      }

      Activity activity = getActivity();
      if (activity == null) {
        return;
      }

      BottomSheetBehavior.from(sheet).setPeekHeight(getHalfHeight(activity));
    });
  }

  private static int getHalfHeight(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
      Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(
          WindowInsets.Type.systemBars()
      );
      return (windowMetrics.getBounds().height() - insets.top - insets.bottom) / 2;
    } else {
      DisplayMetrics displayMetrics = new DisplayMetrics();
      activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
      return displayMetrics.heightPixels / 2;
    }
  }
}
