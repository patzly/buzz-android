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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import xyz.zedler.patrick.buzz.R;
import xyz.zedler.patrick.buzz.activity.BaseActivity;
import xyz.zedler.patrick.buzz.databinding.FragmentBottomsheetFeedbackBinding;
import xyz.zedler.patrick.buzz.util.IconUtil;

public class FeedbackBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {

  private final static String TAG = "FeedbackBottomSheet";

  private FragmentBottomsheetFeedbackBinding binding;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState
  ) {
    BaseActivity activity = (BaseActivity) getActivity();
    assert activity != null;

    binding = FragmentBottomsheetFeedbackBinding.inflate(inflater, container, false);

    binding.linearFeedbackRate.setOnClickListener(v -> {
      activity.performHapticHeavyClick();
      IconUtil.start(binding.imageFeedbackRate);
      Uri uri = Uri.parse(
          "market://details?id=" + activity.getApplicationContext().getPackageName()
      );
      Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
      goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
          Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
          Intent.FLAG_ACTIVITY_MULTIPLE_TASK |
          Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
      new Handler(Looper.getMainLooper()).postDelayed(() -> {
        try {
          startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
          startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
              "http://play.google.com/store/apps/details?id="
                  + activity.getApplicationContext().getPackageName()
          )));
        }
        dismiss();
      }, 300);
    });

    binding.linearFeedbackEmail.setOnClickListener(v -> {
      activity.performHapticClick();
      Intent intent = new Intent(Intent.ACTION_SENDTO);
      intent.setData(
          Uri.parse(
              "mailto:"
                  + getString(R.string.app_mail)
                  + "?subject=" + Uri.encode("Feedback@Buzz")
          )
      );
      startActivity(Intent.createChooser(intent, getString(R.string.action_send_feedback)));
      dismiss();
    });

    binding.linearFeedbackShare.setOnClickListener(v -> {
      activity.performHapticClick();
      Intent sendIntent = new Intent(Intent.ACTION_SEND);
      sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_share));
      sendIntent.setType("text/plain");
      startActivity(Intent.createChooser(sendIntent, null));
      dismiss();
    });

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
