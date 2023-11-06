/*
 * Copyright (c) 2023 Chunky contributors
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.MinecraftBlock;
import se.llbit.chunky.resources.Texture;

public class CopperBulb extends MinecraftBlock {
  private final boolean lit;
  private final boolean powered;

  public CopperBulb(String name, boolean lit, boolean powered, Texture lp, Texture lnp, Texture nlp, Texture nlnp) {
    super(name, lit ? (powered ? lp : lnp) : (powered ? nlp : nlnp));
    this.lit = lit;
    this.powered = powered;
  }
  public boolean isLit() {
    return lit;
  }

  public boolean isPowered() {
    return powered;
  }

  @Override public String description() {
    return "lit=" + lit + ", powered=" + powered;
  }
}
