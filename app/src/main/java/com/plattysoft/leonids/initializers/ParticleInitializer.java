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

package com.plattysoft.leonids.initializers;

import com.plattysoft.leonids.Particle;
import java.util.Random;

public interface ParticleInitializer {

  void initParticle(Particle p, Random r);
}
