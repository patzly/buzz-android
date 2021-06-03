package com.plattysoft.leonids.modifiers;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.plattysoft.leonids.Particle;

public class ScaleModifier implements ParticleModifier {

  private final float mInitialValue;
  private final float mFinalValue;
  private final long mEndTime;
  private final long mStartTime;
  private final long mDuration;
  private final float mValueIncrement;
  private final Interpolator mInterpolator;

  public ScaleModifier(float initialValue, float finalValue, long startMillis, long endMillis,
      Interpolator interpolator) {
    mInitialValue = initialValue;
    mFinalValue = finalValue;
    mStartTime = startMillis;
    mEndTime = endMillis;
    mDuration = mEndTime - mStartTime;
    mValueIncrement = mFinalValue - mInitialValue;
    mInterpolator = interpolator;
  }

  public ScaleModifier(float initialValue, float finalValue, long startMillis, long endMillis) {
    this(initialValue, finalValue, startMillis, endMillis, new LinearInterpolator());
  }

  @Override
  public void apply(Particle particle, long milliseconds) {
    if (milliseconds < mStartTime) {
      particle.mScale = mInitialValue;
    } else if (milliseconds > mEndTime) {
      particle.mScale = mFinalValue;
    } else {
      float interpolatedValue = mInterpolator
          .getInterpolation((milliseconds - mStartTime) * 1f / mDuration);
      particle.mScale = mInitialValue + mValueIncrement * interpolatedValue;
    }
  }

}
