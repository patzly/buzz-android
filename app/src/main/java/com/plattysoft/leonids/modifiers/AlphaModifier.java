package com.plattysoft.leonids.modifiers;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.plattysoft.leonids.Particle;

public class AlphaModifier implements ParticleModifier {

  private final int mInitialValue;
  private final int mFinalValue;
  private final long mStartTime;
  private final long mEndTime;
  private final float mDuration;
  private final float mValueIncrement;
  private final Interpolator mInterpolator;

  public AlphaModifier(int initialValue, int finalValue, long startMillis, long endMillis,
      Interpolator interpolator) {
    mInitialValue = initialValue;
    mFinalValue = finalValue;
    mStartTime = startMillis;
    mEndTime = endMillis;
    mDuration = mEndTime - mStartTime;
    mValueIncrement = mFinalValue - mInitialValue;
    mInterpolator = interpolator;
  }

  public AlphaModifier(int initialValue, int finalValue, long startMillis, long endMillis) {
    this(initialValue, finalValue, startMillis, endMillis, new LinearInterpolator());
  }

  @Override
  public void apply(Particle particle, long milliseconds) {
    if (milliseconds < mStartTime) {
      particle.mAlpha = mInitialValue;
    } else if (milliseconds > mEndTime) {
      particle.mAlpha = mFinalValue;
    } else {
      float interpolaterdValue = mInterpolator
          .getInterpolation((milliseconds - mStartTime) * 1f / mDuration);
      particle.mAlpha = (int) (mInitialValue + mValueIncrement * interpolaterdValue);
    }
  }

}
