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
import se.llbit.chunky.model.minecraft.CandleModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.math.Vector3;

import java.util.Random;

public class Candle extends AbstractModelBlock {

  public static final Material flameMaterial = new TextureMaterial(Texture.flameParticle);

  private final int candles;
  private final boolean lit;
  private final FlameParticles entity;

  public Candle(String name, Texture candle, Texture candleLit, int candles, boolean lit) {
    super(name, candle);
    this.candles = Math.max(1, Math.min(4, candles));
    this.lit = lit;
    this.model = new CandleModel(lit ? candleLit : candle, candles);
    switch (candles) {
      case 1:
        entity = new FlameParticles(this, new Vector3[]{
                new Vector3(0, 7 / 16.0, 0)
        });
        break;
      case 2:
        entity = new FlameParticles(this, new Vector3[]{
                new Vector3(-2 / 16.0, 6 / 16.0, 0),
                new Vector3(2 / 16.0, 7 / 16.0, -1 / 16.0)
        });
        break;
      case 3:
        entity = new FlameParticles(this, new Vector3[]{
                new Vector3(0, 4 / 16.0, 2 / 16.0),
                new Vector3(-2 / 16.0, 6 / 16.0, 0),
                new Vector3(1 / 16.0, 7 / 16.0, -1 / 16.0)
        });
        break;
      case 4:
        entity = new FlameParticles(this, new Vector3[]{
                new Vector3(-1 / 16.0, 4 / 16.0, 1 / 16.0),
                new Vector3(2 / 16.0, 6 / 16.0, 1 / 16.0),
                new Vector3(-2 / 16.0, 6 / 16.0, -2 / 16.0),
                new Vector3(1 / 16.0, 7 / 16.0, -2 / 16.0)
        });
        break;
      default:
        entity = null;
    }
  }

  public boolean isLit() {
    return lit;
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
  public Entity[] toEntity(Vector3 position) {
    if (entity != null) {
      return new FlameParticles[] {new FlameParticles(position, entity)};
    } else {
      return new FlameParticles[] {new FlameParticles(position, this, new Vector3[0])};
    }
  }

  @Override
  public String description() {
    return "candles=" + candles + ", lit=" + isLit();
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
