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

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The new Repeater model introduced in Minecraft 1.21.2 (24w33a).
 */
public class RedstoneRepeaterModel1212 extends QuadModel {
  private static final Texture slab = Texture.smoothStone;
  private static final Texture top = Texture.redstoneRepeaterOff;
  private static final Texture topOn = Texture.redstoneRepeaterOn;
  private static final Texture[] baseTex = new Texture[]{top, slab, slab, slab, slab, slab};
  private static final Texture[] baseOnTex = new Texture[]{topOn, slab, slab, slab, slab, slab};

  //region Body
  private static final Quad[] north = {
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 2 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 2 / 16.0, 0 / 16.0)
    )
  };
  //endregion

  //region Torch (off)
  private static final Quad[] torch = {
    new Quad(
      new Vector3(7 / 16.0, 7 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 7 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 7 / 16.0, 2 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 7 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 7 / 16.0, 2 / 16.0),
      new Vector3(7 / 16.0, 2 / 16.0, 4 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 7 / 16.0, 2 / 16.0),
      new Vector3(9 / 16.0, 7 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 2 / 16.0, 2 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 7 / 16.0, 2 / 16.0),
      new Vector3(9 / 16.0, 7 / 16.0, 2 / 16.0),
      new Vector3(7 / 16.0, 2 / 16.0, 2 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 7 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 7 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 2 / 16.0, 4 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    )
  };
  //endregion

  //region Torch (on)
  private static final Quad[] torchOn = {
    new Quad(
      new Vector3(7 / 16.0, 7 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 7 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 7 / 16.0, 2 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 7 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 7 / 16.0, 2 / 16.0),
      new Vector3(7 / 16.0, 2 / 16.0, 4 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 7 / 16.0, 2 / 16.0),
      new Vector3(9 / 16.0, 7 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 2 / 16.0, 2 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 7 / 16.0, 2 / 16.0),
      new Vector3(9 / 16.0, 7 / 16.0, 2 / 16.0),
      new Vector3(7 / 16.0, 2 / 16.0, 2 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 7 / 16.0, 4 / 16.0),
      new Vector3(7 / 16.0, 7 / 16.0, 4 / 16.0),
      new Vector3(9 / 16.0, 2 / 16.0, 4 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 5 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 4.5 / 16.0, 4.5 / 16.0),
      new Vector3(9.5 / 16.0, 4.5 / 16.0, 4.5 / 16.0),
      new Vector3(6.5 / 16.0, 4.5 / 16.0, 1.5 / 16.0),
      new Vector4(8 / 16.0, 9 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 1.5 / 16.0),
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 1.5 / 16.0),
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 4.5 / 16.0),
      new Vector4(7 / 16.0, 8 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 1.5 / 16.0),
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 1.5 / 16.0),
      new Vector3(9.5 / 16.0, 4.5 / 16.0, 1.5 / 16.0),
      new Vector4(10 / 16.0, 9 / 16.0, 10 / 16.0, 9 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 4.5 / 16.0),
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 4.5 / 16.0),
      new Vector3(6.5 / 16.0, 4.5 / 16.0, 4.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 10 / 16.0, 9 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 1.5 / 16.0),
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 4.5 / 16.0),
      new Vector3(6.5 / 16.0, 4.5 / 16.0, 1.5 / 16.0),
      new Vector4(10 / 16.0, 9 / 16.0, 9 / 16.0, 8 / 16.0)
    ),
    new RedstoneTorchModel.GlowQuad(
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 4.5 / 16.0),
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 1.5 / 16.0),
      new Vector3(9.5 / 16.0, 4.5 / 16.0, 4.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 9 / 16.0, 8 / 16.0)
    )
  };
  //endregion

  //region Lock
  private static final Quad[] lock = {
    new Quad(
      new Vector3(2 / 16.0, 4 / 16.0, 6 / 16.0),
      new Vector3(2 / 16.0, 4 / 16.0, 8 / 16.0),
      new Vector3(14 / 16.0, 4 / 16.0, 6 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 2 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(2 / 16.0, 4 / 16.0, 8 / 16.0),
      new Vector3(2 / 16.0, 4 / 16.0, 6 / 16.0),
      new Vector3(2 / 16.0, 2 / 16.0, 8 / 16.0),
      new Vector4(8 / 16.0, 6 / 16.0, 9 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(14 / 16.0, 4 / 16.0, 6 / 16.0),
      new Vector3(14 / 16.0, 4 / 16.0, 8 / 16.0),
      new Vector3(14 / 16.0, 2 / 16.0, 6 / 16.0),
      new Vector4(8 / 16.0, 6 / 16.0, 9 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(2 / 16.0, 4 / 16.0, 6 / 16.0),
      new Vector3(14 / 16.0, 4 / 16.0, 6 / 16.0),
      new Vector3(2 / 16.0, 2 / 16.0, 6 / 16.0),
      new Vector4(14 / 16.0, 2 / 16.0, 9 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(14 / 16.0, 4 / 16.0, 8 / 16.0),
      new Vector3(2 / 16.0, 4 / 16.0, 8 / 16.0),
      new Vector3(14 / 16.0, 2 / 16.0, 8 / 16.0),
      new Vector4(14 / 16.0, 2 / 16.0, 9 / 16.0, 7 / 16.0)
    )
  };
  //endregion

  private final Quad[] quads;
  private final Texture[] textures;

  public RedstoneRepeaterModel1212(String facing, int delay, boolean powered, boolean locked) {
    List<Quad> model = new ArrayList<>();
    Collections.addAll(model, north);
    Collections.addAll(model, powered ? torchOn : torch);
    if (locked) {
      Collections.addAll(model, Model.translate(lock, 0, 0, (delay - 1) * 2 / 16.));
    } else {
      Collections.addAll(model, Model.translate(powered ? torchOn : torch, 0, 0, 2 / 16. + delay * 2 / 16.));
    }
    quads = switch (facing) {
      case "west" -> Model.rotateY(model.toArray(new Quad[0]));
      case "north" -> Model.rotateY(Model.rotateY(model.toArray(new Quad[0])));
      case "east" -> Model.rotateNegY(model.toArray(new Quad[0]));
      default -> model.toArray(new Quad[0]);
    };
    this.textures = new Texture[this.quads.length];
    System.arraycopy(powered ? baseOnTex : baseTex, 0, this.textures, 0, 6);
    Arrays.fill(textures, 6, 6 + (powered ? torchOn : torch).length, powered ? Texture.redstoneTorchOn : Texture.redstoneTorchOff);
    if (locked) {
      Arrays.fill(textures, 6 + (powered ? torchOn : torch).length, 6 + (powered ? torchOn : torch).length + lock.length, Texture.bedrock);
    } else {
      Arrays.fill(textures, 6 + (powered ? torchOn : torch).length, 6 + 2 * (powered ? torchOn : torch).length, powered ? Texture.redstoneTorchOn : Texture.redstoneTorchOff);
    }
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return RedstoneTorchModel.intersectWithGlow(ray, scene, this);
  }
}
