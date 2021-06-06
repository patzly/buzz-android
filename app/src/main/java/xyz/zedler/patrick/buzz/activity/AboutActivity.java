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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import xyz.zedler.patrick.buzz.Constants;
import xyz.zedler.patrick.buzz.R;
import xyz.zedler.patrick.buzz.behavior.ScrollBehavior;
import xyz.zedler.patrick.buzz.behavior.SystemBarBehavior;
import xyz.zedler.patrick.buzz.databinding.ActivityAboutBinding;
import xyz.zedler.patrick.buzz.fragment.ChangelogBottomSheetDialogFragment;
import xyz.zedler.patrick.buzz.fragment.LicenseBottomSheetDialogFragment;
import xyz.zedler.patrick.buzz.util.ClickUtil;
import xyz.zedler.patrick.buzz.util.IconUtil;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

  private final static String TAG = AboutActivity.class.getSimpleName();

  private ActivityAboutBinding binding;
  private ClickUtil clickUtil;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityAboutBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    clickUtil = new ClickUtil();

    SystemBarBehavior systemBarBehavior = new SystemBarBehavior(this);
    systemBarBehavior.setAppBar(binding.appBarAbout);
    systemBarBehavior.setScroll(binding.scrollAbout, binding.linearAboutContainer);
    systemBarBehavior.setUp();

    new ScrollBehavior(this).setUpScroll(
        binding.appBarAbout, binding.scrollAbout, true
    );

    ClickUtil.setOnClickListeners(
        this,
        binding.frameAboutClose,
        binding.linearChangelog,
        binding.linearDeveloper,
        binding.linearLicenseJost,
        binding.linearLicenseMaterialComponents,
        binding.linearLicenseMaterialIcons
    );
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  @Override
  public void onClick(View v) {
    if (clickUtil.isDisabled()) {
      return;
    }

    int id = v.getId();
    if (id == R.id.frame_about_close) {
      performHapticClick();
      finish();
    } else if (id == R.id.linear_changelog) {
      IconUtil.start(binding.imageChangelog);
      showBottomSheet(new ChangelogBottomSheetDialogFragment());
      performHapticClick();
    } else if (id == R.id.linear_developer) {
      IconUtil.start(binding.imageDeveloper);
      new Handler(Looper.getMainLooper()).postDelayed(() -> startActivity(
          new Intent(Intent.ACTION_VIEW, Uri.parse(
              "http://play.google.com/store/apps/dev?id=8122479227040208191"
          ))
      ), 300);
      performHapticClick();
    } else if (id == R.id.linear_license_jost) {
      IconUtil.start(binding.imageLicenseJost);
      showLicense(
          R.string.license_jost,
          "licenses/ofl",
          R.string.license_jost_link
      );
      performHapticClick();
    } else if (id == R.id.linear_license_material_components) {
      IconUtil.start(binding.imageLicenseMaterialComponents);
      showLicense(
          R.string.license_material_components,
          "licenses/apache",
          R.string.license_material_components_link
      );
      performHapticClick();
    } else if (id == R.id.linear_license_material_icons) {
      IconUtil.start(binding.imageLicenseMaterialIcons);
      showLicense(
          R.string.license_material_icons,
          "licenses/apache",
          R.string.license_material_icons_link
      );
      performHapticClick();
    }
  }

  private void showLicense(@StringRes int title, @NonNull String file, @StringRes int link) {
    Bundle bundle = new Bundle();
    bundle.putString(Constants.EXTRA.TITLE, getString(title));
    bundle.putString(Constants.EXTRA.FILE, file);
    if (link != -1) {
      bundle.putString(Constants.EXTRA.LINK, getString(link));
    }
    showBottomSheet(new LicenseBottomSheetDialogFragment(), bundle);
  }
}
