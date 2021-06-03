package com.plattysoft.leonids.modifiers;

import com.plattysoft.leonids.Particle;

public interface ParticleModifier {

  /**
   * modifies the specific value of a particle given the current milliseconds
   */
  void apply(Particle particle, long milliseconds);
}
