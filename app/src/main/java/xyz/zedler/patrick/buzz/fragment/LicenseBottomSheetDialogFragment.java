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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import xyz.zedler.patrick.buzz.Constants;
import xyz.zedler.patrick.buzz.activity.BaseActivity;
import xyz.zedler.patrick.buzz.databinding.FragmentBottomsheetLicenseBinding;
import xyz.zedler.patrick.buzz.util.IconUtil;
import xyz.zedler.patrick.buzz.util.ResUtil;

public class LicenseBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {

  private final static String TAG = "LicenseBottomSheet";

  private FragmentBottomsheetLicenseBinding binding;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState
  ) {
    binding = FragmentBottomsheetLicenseBinding.inflate(inflater, container, false);

    BaseActivity activity = (BaseActivity) getActivity();
    Bundle bundle = getArguments();
    assert activity != null && bundle != null;

    String file = bundle.getString(Constants.EXTRA.FILE) + ".txt";

    binding.textLicenseTitle.setText(bundle.getString(Constants.EXTRA.TITLE));

    String link = bundle.getString(Constants.EXTRA.LINK);
    if (link != null) {
      binding.frameLicenseOpenLink.setOnClickListener(v -> {
        activity.performHapticClick();
        IconUtil.start(binding.imageLicenseOpenLink);
        new Handler(Looper.getMainLooper()).postDelayed(
            () -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link))), 500
        );
      });
    } else {
      binding.frameLicenseOpenLink.setVisibility(View.GONE);
    }

    binding.textLicense.setText(ResUtil.readFromFile(activity, file));

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
