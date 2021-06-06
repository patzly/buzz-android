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

package xyz.zedler.patrick.buzz.behavior;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.appbar.AppBarLayout;
import xyz.zedler.patrick.buzz.R;

public class ScrollBehavior {

  private final static String TAG = ScrollBehavior.class.getSimpleName();
  private final static boolean DEBUG = false;

  private static final int STATE_SCROLLED_DOWN = 1;
  private static final int STATE_SCROLLED_UP = 2;
  // distance gets divided to prevent cutoff of edge effect
  private final int pufferDivider = 2;
  private final Activity activity;
  private int currentState = STATE_SCROLLED_UP;
  // distance before top scroll when overScroll is turned off
  private int pufferSize = 0;
  private boolean isTopScroll = false;
  private boolean liftOnScroll = true;
  private boolean killObserver = true;
  private boolean noOverScroll = false;
  private AppBarLayout appBarLayout;
  private NestedScrollView scrollView;

  public ScrollBehavior(@NonNull Activity activity) {
    this.activity = activity;
  }

  public void setUpScroll(@NonNull AppBarLayout appBarLayout,
      NestedScrollView scrollView,
      boolean liftOnScroll,
      boolean noOverScroll,
      boolean killObserver) {
    this.appBarLayout = appBarLayout;
    this.scrollView = scrollView;
    this.liftOnScroll = liftOnScroll;
    this.noOverScroll = noOverScroll;
    this.killObserver = killObserver;

    currentState = STATE_SCROLLED_UP;

    measureScrollView();
    setLiftOnScroll(liftOnScroll);

    if (scrollView == null) {
      return;
    }
    scrollView.setOnScrollChangeListener((NestedScrollView v,
        int scrollX,
        int scrollY,
        int oldScrollX,
        int oldScrollY) -> {
      if (!isTopScroll && scrollY == 0) { // TOP
        onTopScroll();
      } else {
        if (scrollY < oldScrollY) { // UP
          if (currentState != STATE_SCROLLED_UP) {
            onScrollUp();
          }
          if (liftOnScroll) {
            if (scrollY < pufferSize) {
              new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (scrollY > 0) {
                  this.scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
                }
              }, 1);
            }
          }
        } else if (scrollY > oldScrollY) {
          if (currentState != STATE_SCROLLED_DOWN) { // DOWN
            onScrollDown();
          }
        }
      }
    });
  }

  public void setUpScroll(@NonNull AppBarLayout appBarLayout,
      NestedScrollView scrollView,
      boolean liftOnScroll) {
    setUpScroll(appBarLayout, scrollView, liftOnScroll, false, true);
  }

  private void onTopScroll() {
    isTopScroll = true;
    if (liftOnScroll) {
      tintAppBarLayout(R.color.background);
      appBarLayout.setLifted(false);
    }
    if (DEBUG) {
      Log.i(TAG, "onTopScroll: liftOnScroll = " + liftOnScroll);
    }
  }

  private void onScrollUp() {
    currentState = STATE_SCROLLED_UP;
    appBarLayout.setLifted(true);
    tintAppBarLayout(R.color.primary);
    if (DEBUG) {
      Log.i(TAG, "onScrollUp: UP");
    }
  }

  private void onScrollDown() {
    // second top scroll is unrealistic before down scroll
    isTopScroll = false;
    currentState = STATE_SCROLLED_DOWN;
    if (scrollView != null) {
      appBarLayout.setLifted(true);
      tintAppBarLayout(R.color.primary);
      scrollView.setOverScrollMode(
          noOverScroll ? View.OVER_SCROLL_NEVER : View.OVER_SCROLL_IF_CONTENT_SCROLLS
      );
    } else if (DEBUG) {
      Log.e(TAG, "onScrollDown: scrollView is null");
    }
    if (DEBUG) {
      Log.i(TAG, "onScrollDown: DOWN");
    }
  }

  /**
   * Sets the global boolean and changes the elevation manually if necessary. If scrollY of the
   * scrollView is 0, overScroll is turned off. Otherwise it's on if the view is scrollable.
   */
  public void setLiftOnScroll(boolean lift) {
    liftOnScroll = lift;
    // We'll make this manually
    appBarLayout.setLiftOnScroll(false);
    appBarLayout.setLiftable(true);
    if (scrollView != null) {
      if (lift) {
        if (scrollView.getScrollY() == 0) {
          appBarLayout.setLifted(false);
          tintAppBarLayout(R.color.background);
          scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        } else {
          appBarLayout.setLifted(true);
          tintAppBarLayout(R.color.primary);
        }
      } else {
        appBarLayout.setLifted(true);
        tintAppBarLayout(R.color.primary);
        scrollView.setOverScrollMode(
            noOverScroll
                ? View.OVER_SCROLL_NEVER
                : View.OVER_SCROLL_IF_CONTENT_SCROLLS
        );
      }
    } else {
      if (lift) {
        appBarLayout.setLiftable(true);
        appBarLayout.setLifted(false);
        tintAppBarLayout(R.color.background);
      } else {
        appBarLayout.setLiftable(false);
        tintAppBarLayout(R.color.primary);
      }
    }
    if (DEBUG) {
      Log.i(TAG, "setLiftOnScroll(" + lift + ")");
    }
  }

  private void measureScrollView() {
    if (scrollView == null) {
      return;
    }
    scrollView.getViewTreeObserver().addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            int scrollViewHeight = scrollView.getMeasuredHeight();
            if (scrollView.getChildAt(0) != null) {
              int scrollContentHeight = scrollView.getChildAt(0).getHeight();
              pufferSize = (scrollContentHeight - scrollViewHeight) / pufferDivider;
            } else if (DEBUG) {
              Log.e(TAG, "measureScrollView: no child");
            }
            // Kill ViewTreeObserver
            if (!killObserver) {
              return;
            }
            if (scrollView.getViewTreeObserver().isAlive()) {
              scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(
                  this
              );
            }
          }
        });
  }

  private void tintAppBarLayout(@ColorRes int target) {
    int appBarColor = getAppBarLayoutColor();
    int targetColor = ContextCompat.getColor(activity, target);
    if (appBarColor == targetColor) {
      return;
    }

    ValueAnimator valueAnimator = ValueAnimator.ofArgb(appBarColor, targetColor);
    valueAnimator.addUpdateListener(
        animation -> appBarLayout.setBackgroundColor((int) valueAnimator.getAnimatedValue())
    );
    valueAnimator.setDuration(
        activity.getResources().getInteger(R.integer.app_bar_elevation_anim_duration)
    ).start();
  }

  private int getAppBarLayoutColor() {
    Drawable background = appBarLayout.getBackground();
    if (background == null || background.getClass() != ColorDrawable.class) {
      appBarLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.background));
    }
    return ((ColorDrawable) appBarLayout.getBackground()).getColor();
  }
}
