package com.plattysoft.leonids.initializers;

import com.plattysoft.leonids.Particle;
import java.util.Random;

public class SpeedByComponentsInitializer implements ParticleInitializer {

  private final float mMinSpeedX;
  private final float mMaxSpeedX;
  private final float mMinSpeedY;
  private final float mMaxSpeedY;

  public SpeedByComponentsInitializer(float speedMinX, float speedMaxX, float speedMinY,
      float speedMaxY) {
    mMinSpeedX = speedMinX;
    mMaxSpeedX = speedMaxX;
    mMinSpeedY = speedMinY;
    mMaxSpeedY = speedMaxY;
  }

  @Override
  public void initParticle(Particle p, Random r) {
    p.mSpeedX = r.nextFloat() * (mMaxSpeedX - mMinSpeedX) + mMinSpeedX;
    p.mSpeedY = r.nextFloat() * (mMaxSpeedY - mMinSpeedY) + mMinSpeedY;
  }
}
