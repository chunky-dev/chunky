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

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.FlameParticles;
import se.llbit.chunky.model.minecraft.CakeWithCandleModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Vector3;

import java.util.Random;

public class CakeWithCandle extends AbstractModelBlock {

  private final boolean lit;
  private final FlameParticles entity;

  public CakeWithCandle(String name, AbstractTexture candle, AbstractTexture candleLit, boolean lit) {
    super(name, Texture.cakeTop);
    this.lit = lit;
    this.model = new CakeWithCandleModel(lit ? candleLit : candle);
    this.entity = new FlameParticles(this, new Vector3[] {
            new Vector3(0, 15 / 16.0, 0)
    });
  }

  public boolean isLit() {
    return lit;
  }

  @Override
  public String description() {
    return "lit=" + isLit();
  }

  @Override
  public boolean isEntity() {
    return isLit();
  }

  @Override
  public boolean isBlockWithEntity() {
    return true;
  }

  @Override
  public Entity toEntity(Vector3 position) {
    return new FlameParticles(position, entity);
  }

  @Override
  public int faceCount() {
    return entity.faceCount();
  }

  @Override
  public void sample(int face, Vector3 loc, Random rand) {
    entity.sample(face, loc, rand);
  }

  @Override
  public double surfaceArea(int face) {
    return entity.surfaceArea(face);
  }
}
