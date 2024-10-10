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

package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public class Flowerbed extends QuadModel {
  //region flowerbed_1
  private static final BiFunction<AbstractTexture, AbstractTexture, AbstractTexture[]> flowerbed1Textures = (flowerbed, stem) -> new AbstractTexture[]{
    flowerbed, flowerbed, stem, stem, stem, stem, stem, stem, stem, stem, stem, stem, stem, stem
  };

  private static final Tint[] flowerbed1Tints = new Tint[]{
    Tint.NONE, Tint.NONE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
  };

  private static final Quad[] flowerbed1 = Model.join(
    new Quad[]{
      new Quad(
        new Vector3(0 / 16.0, 2.99 / 16.0, 8 / 16.0),
        new Vector3(8 / 16.0, 2.99 / 16.0, 8 / 16.0),
        new Vector3(0 / 16.0, 2.99 / 16.0, 0 / 16.0),
        new Vector4(0 / 16.0, 8 / 16.0, 8 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 2.99 / 16.0, 0 / 16.0),
        new Vector3(8 / 16.0, 2.99 / 16.0, 0 / 16.0),
        new Vector3(0 / 16.0, 2.99 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 8 / 16.0, 16 / 16.0, 8 / 16.0)
      )
    },
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(4.25 / 16.0, 2.99 / 16.0, -1.6 / 16.0),
          new Vector3(4.25 / 16.0, 2.99 / 16.0, -2.6 / 16.0),
          new Vector3(4.25 / 16.0, 0 / 16.0, -1.6 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(4.25 / 16.0, 2.99 / 16.0, -2.6 / 16.0),
          new Vector3(4.25 / 16.0, 2.99 / 16.0, -1.6 / 16.0),
          new Vector3(4.25 / 16.0, 0 / 16.0, -2.6 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(3.75 / 16.0, 2.99 / 16.0, -2.1 / 16.0),
          new Vector3(4.75 / 16.0, 2.99 / 16.0, -2.1 / 16.0),
          new Vector3(3.75 / 16.0, 0 / 16.0, -2.1 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(4.75 / 16.0, 2.99 / 16.0, -2.1 / 16.0),
          new Vector3(3.75 / 16.0, 2.99 / 16.0, -2.1 / 16.0),
          new Vector3(4.75 / 16.0, 0 / 16.0, -2.1 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(4.9 / 16.0, 2.99 / 16.0, 3.3 / 16.0),
          new Vector3(4.9 / 16.0, 2.99 / 16.0, 2.3 / 16.0),
          new Vector3(4.9 / 16.0, 0 / 16.0, 3.3 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(4.9 / 16.0, 2.99 / 16.0, 2.3 / 16.0),
          new Vector3(4.9 / 16.0, 2.99 / 16.0, 3.3 / 16.0),
          new Vector3(4.9 / 16.0, 0 / 16.0, 2.3 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(4.4 / 16.0, 2.99 / 16.0, 2.8 / 16.0),
          new Vector3(5.4 / 16.0, 2.99 / 16.0, 2.8 / 16.0),
          new Vector3(4.4 / 16.0, 0 / 16.0, 2.8 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(5.4 / 16.0, 2.99 / 16.0, 2.8 / 16.0),
          new Vector3(4.4 / 16.0, 2.99 / 16.0, 2.8 / 16.0),
          new Vector3(5.4 / 16.0, 0 / 16.0, 2.8 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(9.15 / 16.0, 2.99 / 16.0, 0.55 / 16.0),
          new Vector3(9.15 / 16.0, 2.99 / 16.0, -0.45 / 16.0),
          new Vector3(9.15 / 16.0, 0 / 16.0, 0.55 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(9.15 / 16.0, 2.99 / 16.0, -0.45 / 16.0),
          new Vector3(9.15 / 16.0, 2.99 / 16.0, 0.55 / 16.0),
          new Vector3(9.15 / 16.0, 0 / 16.0, -0.45 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(8.65 / 16.0, 2.99 / 16.0, 0.05 / 16.0),
          new Vector3(9.65 / 16.0, 2.99 / 16.0, 0.05 / 16.0),
          new Vector3(8.65 / 16.0, 0 / 16.0, 0.05 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(9.65 / 16.0, 2.99 / 16.0, 0.05 / 16.0),
          new Vector3(8.65 / 16.0, 2.99 / 16.0, 0.05 / 16.0),
          new Vector3(9.65 / 16.0, 0 / 16.0, 0.05 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        )
      }, Math.toRadians(-45), new Vector3(0, 0, 0))
  );
  //endregion

  //region flowerbed_2
  private static final BiFunction<AbstractTexture, AbstractTexture, AbstractTexture[]> flowerbed2Textures = (flowerbed, stem) -> new AbstractTexture[]{
    flowerbed, flowerbed, flowerbed, flowerbed, stem, stem, stem, stem
  };

  private static final Tint[] flowerbed2Tints = new Tint[]{
    Tint.NONE, Tint.NONE, Tint.NONE, Tint.NONE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
  };

  private static final Quad[] flowerbed2 = Model.join(
    new Quad[]{
      new Quad(
        new Vector3(0 / 16.0, 1 / 16.0, 16 / 16.0),
        new Vector3(8 / 16.0, 1 / 16.0, 16 / 16.0),
        new Vector3(0 / 16.0, 1 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 8 / 16.0, 0 / 16.0, 8 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 1 / 16.0, 8 / 16.0),
        new Vector3(8 / 16.0, 1 / 16.0, 8 / 16.0),
        new Vector3(0 / 16.0, 1 / 16.0, 16 / 16.0),
        new Vector4(0 / 16.0, 8 / 16.0, 8 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 1 / 16.0, 16 / 16.0),
        new Vector3(8 / 16.0, 1 / 16.0, 16 / 16.0),
        new Vector3(0 / 16.0, 1 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 8 / 16.0, 0 / 16.0, 8 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 1 / 16.0, 8 / 16.0),
        new Vector3(8 / 16.0, 1 / 16.0, 8 / 16.0),
        new Vector3(0 / 16.0, 1 / 16.0, 16 / 16.0),
        new Vector4(0 / 16.0, 8 / 16.0, 8 / 16.0, 0 / 16.0)
      )
    },
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(10.15 / 16.0, 1 / 16.0, 5.25 / 16.0),
        new Vector3(11.15 / 16.0, 1 / 16.0, 5.25 / 16.0),
        new Vector3(10.15 / 16.0, 0 / 16.0, 5.25 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 10 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(11.15 / 16.0, 1 / 16.0, 5.25 / 16.0),
        new Vector3(10.15 / 16.0, 1 / 16.0, 5.25 / 16.0),
        new Vector3(11.15 / 16.0, 0 / 16.0, 5.25 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 10 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(10.65 / 16.0, 1 / 16.0, 5.75 / 16.0),
        new Vector3(10.65 / 16.0, 1 / 16.0, 4.75 / 16.0),
        new Vector3(10.65 / 16.0, 0 / 16.0, 5.75 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 10 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(10.65 / 16.0, 1 / 16.0, 4.75 / 16.0),
        new Vector3(10.65 / 16.0, 1 / 16.0, 5.75 / 16.0),
        new Vector3(10.65 / 16.0, 0 / 16.0, 4.75 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 10 / 16.0, 9 / 16.0)
      )
    }, Math.toRadians(-45), new Vector3(0, 0, 1 / 16.))
  );
  //endregion

  //region flowerbed_3
  private static final BiFunction<AbstractTexture, AbstractTexture, AbstractTexture[]> flowerbed3Textures = (flowerbed, stem) -> new AbstractTexture[]{
    flowerbed, flowerbed, stem, stem, stem, stem, stem, stem, stem, stem, stem, stem, stem, stem
  };

  private static final Tint[] flowerbed3Tints = new Tint[]{
    Tint.NONE, Tint.NONE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
  };

  private static final Quad[] flowerbed3 = Model.join(
    new Quad[]{
      new Quad(
        new Vector3(8 / 16.0, 2 / 16.0, 16 / 16.0),
        new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
        new Vector3(8 / 16.0, 2 / 16.0, 8 / 16.0),
        new Vector4(8 / 16.0, 16 / 16.0, 0 / 16.0, 8 / 16.0)
      ),
      new Quad(
        new Vector3(8 / 16.0, 2 / 16.0, 8 / 16.0),
        new Vector3(16 / 16.0, 2 / 16.0, 8 / 16.0),
        new Vector3(8 / 16.0, 2 / 16.0, 16 / 16.0),
        new Vector4(8 / 16.0, 16 / 16.0, 8 / 16.0, 0 / 16.0)
      )
    },
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(17.65 / 16.0, 2 / 16.0, 1.9 / 16.0),
        new Vector3(18.65 / 16.0, 2 / 16.0, 1.9 / 16.0),
        new Vector3(17.65 / 16.0, 0 / 16.0, 1.9 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(18.65 / 16.0, 2 / 16.0, 1.9 / 16.0),
        new Vector3(17.65 / 16.0, 2 / 16.0, 1.9 / 16.0),
        new Vector3(18.65 / 16.0, 0 / 16.0, 1.9 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(18.15 / 16.0, 2 / 16.0, 2.4 / 16.0),
        new Vector3(18.15 / 16.0, 2 / 16.0, 1.4 / 16.0),
        new Vector3(18.15 / 16.0, 0 / 16.0, 2.4 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(18.15 / 16.0, 2 / 16.0, 1.4 / 16.0),
        new Vector3(18.15 / 16.0, 2 / 16.0, 2.4 / 16.0),
        new Vector3(18.15 / 16.0, 0 / 16.0, 1.4 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
    }, Math.toRadians(-45), new Vector3(0.5 / 16, 0, 0.5 / 16)),
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(17.65 / 16.0, 2 / 16.0, -2.35 / 16.0),
        new Vector3(17.65 / 16.0, 2 / 16.0, -3.35 / 16.0),
        new Vector3(17.65 / 16.0, 0 / 16.0, -2.35 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(17.65 / 16.0, 2 / 16.0, -3.35 / 16.0),
        new Vector3(17.65 / 16.0, 2 / 16.0, -2.35 / 16.0),
        new Vector3(17.65 / 16.0, 0 / 16.0, -3.35 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(17.15 / 16.0, 2 / 16.0, -2.85 / 16.0),
        new Vector3(18.15 / 16.0, 2 / 16.0, -2.85 / 16.0),
        new Vector3(17.15 / 16.0, 0 / 16.0, -2.85 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(18.15 / 16.0, 2 / 16.0, -2.85 / 16.0),
        new Vector3(17.15 / 16.0, 2 / 16.0, -2.85 / 16.0),
        new Vector3(18.15 / 16.0, 0 / 16.0, -2.85 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(13.4 / 16.0, 2 / 16.0, 0.5 / 16.0),
        new Vector3(13.4 / 16.0, 2 / 16.0, -0.5 / 16.0),
        new Vector3(13.4 / 16.0, 0 / 16.0, 0.5 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(13.4 / 16.0, 2 / 16.0, -0.5 / 16.0),
        new Vector3(13.4 / 16.0, 2 / 16.0, 0.5 / 16.0),
        new Vector3(13.4 / 16.0, 0 / 16.0, -0.5 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(12.9 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector3(13.9 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector3(12.9 / 16.0, 0 / 16.0, 0 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(13.9 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector3(12.9 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector3(13.9 / 16.0, 0 / 16.0, 0 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      )
    }, Math.toRadians(-45), new Vector3(0, 0, 0))
  );
  //endregion

  //region flowerbed_4
  private static final BiFunction<AbstractTexture, AbstractTexture, AbstractTexture[]> flowerbed4Textures = (flowerbed, stem) -> new AbstractTexture[]{
    flowerbed, flowerbed, stem, stem, stem, stem
  };

  private static final Tint[] flowerbed4Tints = new Tint[]{
    Tint.NONE, Tint.NONE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE
  };

  private static final Quad[] flowerbed4 = Model.join(
    new Quad[]{
      new Quad(
        new Vector3(8 / 16.0, 2 / 16.0, 8 / 16.0),
        new Vector3(16 / 16.0, 2 / 16.0, 8 / 16.0),
        new Vector3(8 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector4(8 / 16.0, 16 / 16.0, 8 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(8 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector3(8 / 16.0, 2 / 16.0, 8 / 16.0),
        new Vector4(8 / 16.0, 16 / 16.0, 16 / 16.0, 8 / 16.0)
      )
    },
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(12.4 / 16.0, 2 / 16.0, -6.7 / 16.0),
        new Vector3(12.4 / 16.0, 2 / 16.0, -7.7 / 16.0),
        new Vector3(12.4 / 16.0, 0 / 16.0, -6.7 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(12.4 / 16.0, 2 / 16.0, -7.7 / 16.0),
        new Vector3(12.4 / 16.0, 2 / 16.0, -6.7 / 16.0),
        new Vector3(12.4 / 16.0, 0 / 16.0, -7.7 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(11.9 / 16.0, 2 / 16.0, -7.2 / 16.0),
        new Vector3(12.9 / 16.0, 2 / 16.0, -7.2 / 16.0),
        new Vector3(11.9 / 16.0, 0 / 16.0, -7.2 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(12.9 / 16.0, 2 / 16.0, -7.2 / 16.0),
        new Vector3(11.9 / 16.0, 2 / 16.0, -7.2 / 16.0),
        new Vector3(12.9 / 16.0, 0 / 16.0, -7.2 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      )
    }, Math.toRadians(-45), new Vector3(-1 / 16., 0, -3 / 16.))
  );
  //endregion

  private final AbstractTexture[] textures;

  private final Quad[] quads;

  private final Tint[] tints;

  public Flowerbed(AbstractTexture flowerbed, AbstractTexture stem, int flowerAmount, String facing) {
    List<AbstractTexture> textures = new ArrayList<>();
    List<Quad> quadList = new ArrayList<>();
    List<Tint> tints = new ArrayList<>();

    Collections.addAll(textures, flowerbed1Textures.apply(flowerbed, stem));
    Collections.addAll(quadList, flowerbed1);
    Collections.addAll(tints, flowerbed1Tints);

    if (flowerAmount >= 2) {
      Collections.addAll(textures, flowerbed2Textures.apply(flowerbed, stem));
      Collections.addAll(quadList, flowerbed2);
      Collections.addAll(tints, flowerbed2Tints);
    }
    if (flowerAmount >= 3) {
      Collections.addAll(textures, flowerbed3Textures.apply(flowerbed, stem));
      Collections.addAll(quadList, flowerbed3);
      Collections.addAll(tints, flowerbed3Tints);
    }
    if (flowerAmount == 4) {
      Collections.addAll(textures, flowerbed4Textures.apply(flowerbed, stem));
      Collections.addAll(quadList, flowerbed4);
      Collections.addAll(tints, flowerbed4Tints);
    }

    this.textures = textures.toArray(new AbstractTexture[0]);
    Quad[] quads = quadList.toArray(new Quad[0]);
    this.tints = tints.toArray(new Tint[0]);

    switch (facing) {
      case "east":
        quads = Model.rotateY(quads);
        break;
      case "south":
        quads = Model.rotateY(Model.rotateY(quads));
        break;
      case "west":
        quads = Model.rotateNegY(quads);
        break;
    }
    this.quads = quads;
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public AbstractTexture[] getTextures() {
    return textures;
  }

  @Override
  public Tint[] getTints() {
    return tints;
  }
}
