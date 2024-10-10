/*
 * Copyright (c) 2012-2023 Chunky contributors
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

import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.chunky.world.BlockData;
import se.llbit.math.ColorUtil;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Arrays;

public class RedstoneWireModel extends QuadModel {
  //region Model
  private static final Quad[] model = {
      // 0000 no connection
      new Quad(new Vector3(11 / 16., 0, 5 / 16.), new Vector3(5 / 16., 0, 5 / 16.),
          new Vector3(11 / 16., 0, 11 / 16.), new Vector4(11 / 16., 5 / 16., 11 / 16., 5 / 16.)),

      // 0001 east
      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 0, 1),
          new Vector4(1, 0, 1, 0)),

      // 0010 west
      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 0, 1),
          new Vector4(1, 0, 1, 0)),

      // 0011 east west
      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 0, 1),
          new Vector4(1, 0, 1, 0)),

      // 0100 north
      new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(1, 0, 1, 0)),

      // 0101 north east
      new Quad(new Vector3(1, 0, 0), new Vector3(5 / 16., 0, 0), new Vector3(1, 0, 11 / 16.),
          new Vector4(1, 5 / 16., 1, 5 / 16.)),

      // 0110 north west
      new Quad(new Vector3(11 / 16., 0, 0), new Vector3(0, 0, 0),
          new Vector3(11 / 16., 0, 11 / 16.), new Vector4(11 / 16., 0, 1, 5 / 16.)),

      // 0111 north east west
      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 0, 11 / 16.),
          new Vector4(1, 0 / 16., 1, 5 / 16.)),

      // 1000 south
      new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(1, 0, 1, 0)),

      // 1001 south east
      new Quad(new Vector3(1, 0, 5 / 16.), new Vector3(5 / 16., 0, 5 / 16.),
          new Vector3(1, 0, 1), new Vector4(1, 5 / 16., 11 / 16., 0)),

      // 1010 south west
      new Quad(new Vector3(11 / 16., 0, 5 / 16.), new Vector3(0, 0, 5 / 16.),
          new Vector3(11 / 16., 0, 1), new Vector4(11 / 16., 0, 11 / 16., 0)),

      // 1011 south east west
      new Quad(new Vector3(16 / 16., 0, 5 / 16.), new Vector3(0 / 16., 0, 5 / 16.),
          new Vector3(16 / 16., 0, 1), new Vector4(16 / 16., 0 / 16., 11 / 16., 0)),

      // 1100 north south
      new Quad(new Vector3(1, 0, 1), new Vector3(1, 0, 0), new Vector3(0, 0, 1),
          new Vector4(1, 0, 1, 0)),

      // 1101 north south east
      new Quad(new Vector3(1, 0, 0), new Vector3(5 / 16., 0, 0), new Vector3(1, 0, 1),
          new Vector4(1, 5 / 16., 1, 0)),

      // 1110 north south west
      new Quad(new Vector3(11 / 16., 0, 0), new Vector3(0, 0, 0), new Vector3(11 / 16., 0, 1),
          new Vector4(11 / 16., 0, 1, 0)),

      // 1111 north south east west
      new Quad(new Vector3(1, 0, 0), new Vector3(0, 0, 0), new Vector3(1, 0, 1),
          new Vector4(1, 0, 1, 0))
  };
  //endregion

  private static final Quad eastSide =
      new Quad(new Vector3(1, 1, 0), new Vector3(1, 0, 0), new Vector3(1, 1, 1),
          new Vector4(1, 0, 1, 0));
  private static final Quad westSide =
      new Quad(new Vector3(0, 1, 1), new Vector3(0, 0, 1), new Vector3(0, 1, 0),
          new Vector4(1, 0, 1, 0));
  private static final Quad northSide =
      new Quad(new Vector3(0, 1, 0), new Vector3(0, 0, 0), new Vector3(1, 1, 0),
          new Vector4(1, 0, 1, 0));
  private static final Quad southSide =
      new Quad(new Vector3(1, 1, 1), new Vector3(1, 0, 1), new Vector3(0, 1, 1),
          new Vector4(1, 0, 1, 0));

  private static final AbstractTexture[] tex = {
      Texture.redstoneWireCross, Texture.redstoneWire, Texture.redstoneWire, Texture.redstoneWire,
      Texture.redstoneWire, Texture.redstoneWireCross, Texture.redstoneWireCross,
      Texture.redstoneWireCross, Texture.redstoneWire, Texture.redstoneWireCross,
      Texture.redstoneWireCross, Texture.redstoneWireCross, Texture.redstoneWire,
      Texture.redstoneWireCross, Texture.redstoneWireCross, Texture.redstoneWireCross
  };

  private static final Tint[] wireTints = new Tint[16];

  static {
    float[] color0 = new float[3];
    float[] color1 = new float[3];
    float[] wireColor = new float[3];
    ColorUtil.getRGBComponents(0x4D0000, color0);
    ColorUtil.toLinear(color0);
    ColorUtil.getRGBComponents(0xFD3100, color1);
    ColorUtil.toLinear(color1);
    for (int i = 0; i < 16; ++i) {
      wireColor[0] = color0[0] + (i / 15.f) * (color1[0] - color0[0]);
      wireColor[1] = color0[1] + (i / 15.f) * (color1[1] - color0[1]);
      wireColor[2] = color0[2] + (i / 15.f) * (color1[2] - color0[2]);
      wireTints[i] = new Tint(wireColor);
    }
  }

  private final Quad[] quads;
  private final AbstractTexture[] textures;
  private final Tint[] tints;

  public RedstoneWireModel(int power, int data) {
    int connection = 0xF & (data >> BlockData.RSW_EAST_CONNECTION);
    ArrayList<Quad> quads = new ArrayList<>();
    quads.add(model[connection]);
    if ((data & (1 << BlockData.RSW_EAST_UP)) != 0)
      quads.add(eastSide);
    if ((data & (1 << BlockData.RSW_WEST_UP)) != 0)
      quads.add(westSide);
    if ((data & (1 << BlockData.RSW_NORTH_UP)) != 0)
      quads.add(northSide);
    if ((data & (1 << BlockData.RSW_SOUTH_UP)) != 0)
      quads.add(southSide);
    this.quads = quads.toArray(new Quad[0]);
    this.textures = new AbstractTexture[this.quads.length];
    Arrays.fill(this.textures, Texture.redstoneWire);
    this.textures[0] = tex[connection];
    this.tints = new Tint[this.quads.length];
    Arrays.fill(this.tints, wireTints[power]);
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
