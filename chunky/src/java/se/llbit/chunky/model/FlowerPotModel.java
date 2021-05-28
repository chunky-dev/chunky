/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.model;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * Flower pot block.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class FlowerPotModel {
  public enum Kind {
    NONE,
    POPPY,
    DANDELION,
    OAK_SAPLING,
    SPRUCE_SAPLING,
    BIRCH_SAPLING,
    JUNGLE_SAPLING,
    ACACIA_SAPLING,
    DARK_OAK_SAPLING,
    RED_MUSHROOM,
    BROWN_MUSHROOM,
    CACTUS,
    DEAD_BUSH,
    FERN,
    BLUE_ORCHID,
    ALLIUM,
    AZURE_BLUET,
    RED_TULIP,
    ORANGE_TULIP,
    WHITE_TULIP,
    PINK_TULIP,
    OXEYE_DAISY,
    BAMBOO,
    CORNFLOWER,
    LILY_OF_THE_VALLEY,
    WITHER_ROSE,
    WARPED_FUNGUS,
    CRIMSON_FUNGUS,
    WARPED_ROOTS,
    CRIMSON_ROOTS,
    AZALEA_BUSH,
    FLOWERING_AZALEA_BUSH
  }

  private static final AABB[] boxes = {
      // East.
      new AABB(10 / 16., 11 / 16., 0, 6 / 16., 5 / 16., 11 / 16.),
      // West.
      new AABB(5 / 16., 6 / 16., 0, 6 / 16., 5 / 16., 11 / 16.),
      // North.
      new AABB(5 / 16., 11 / 16., 0, 6 / 16., 5 / 16., 6 / 16.),
      // South.
      new AABB(5 / 16., 11 / 16., 0, 6 / 16., 10 / 16., 11 / 16.),
      // Center.
      new AABB(6 / 16., 10 / 16., 0, 4 / 16., 6 / 16., 10 / 16.),
  };

  private static final AABB cactus = new AABB(6 / 16., 10 / 16., 4 / 16., 1, 6 / 16., 10 / 16.);

  private static final Quad[] flower = {
      new Quad(new Vector3(0, 4 / 16., 0), new Vector3(1, 4 / 16., 1), new Vector3(0, 1, 0),
          new Vector4(0, 1, 0, 12 / 16.)),

      new Quad(new Vector3(1, 4 / 16., 1), new Vector3(0, 4 / 16., 0), new Vector3(1, 1, 1),
          new Vector4(0, 1, 0, 12 / 16.)),

      new Quad(new Vector3(1, 4 / 16., 0), new Vector3(0, 4 / 16., 1), new Vector3(1, 1, 0),
          new Vector4(0, 1, 0, 12 / 16.)),

      new Quad(new Vector3(0, 4 / 16., 1), new Vector3(1, 4 / 16., 0), new Vector3(0, 1, 1),
          new Vector4(0, 1, 0, 12 / 16.)),
  };

  private static final Quad[] flowerSmall = {
      new Quad(new Vector3(2 / 16., 4 / 16., 2 / 16.), new Vector3(14 / 16., 4 / 16., 14 / 16.),
          new Vector3(2 / 16., 1, 2 / 16.), new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(14 / 16., 4 / 16., 14 / 16.),
          new Vector3(2 / 16., 4 / 16., 2 / 16.), new Vector3(14 / 16., 1, 14 / 16.),
          new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(14 / 16., 4 / 16., 2 / 16.),
          new Vector3(2 / 16., 4 / 16., 14 / 16.), new Vector3(14 / 16., 1, 2 / 16.),
          new Vector4(0, 1, 0, 1)),

      new Quad(new Vector3(2 / 16., 4 / 16., 14 / 16.),
          new Vector3(14 / 16., 4 / 16., 2 / 16.), new Vector3(2 / 16., 1, 14 / 16.),
          new Vector4(0, 1, 0, 1)),
  };

  private static final Quad[] bamboo = {
      new Quad(
          new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
          new Vector4(13 / 16.0, 15 / 16.0, 1 - 0 / 16.0, 1 - 2 / 16.0)),
      new Quad(
          new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(13 / 16.0, 15 / 16.0, 4 / 16.0, 6 / 16.0)),
      new Quad(
        new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
        new Vector4(6 / 16.0, 8 / 16.0, 0 / 16.0, 16 / 16.0)),
      new Quad(
        new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
        new Vector4(6 / 16.0, 8 / 16.0, 0 / 16.0, 16 / 16.0)),
      new Quad(
        new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
        new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
        new Vector3(9 / 16.0, 16 / 16.0, 7 / 16.0),
        new Vector4(6 / 16.0, 8 / 16.0, 0 / 16.0, 16 / 16.0)),
      new Quad(
        new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
        new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
        new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
        new Vector4(6 / 16.0, 8 / 16.0, 0 / 16.0, 16 / 16.0))
  };

  private static final Quad[] bambooLeaf = {
      new Quad(
          new Vector3(16 / 16.0, 2 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 2 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 18 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(0 / 16.0, 2 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 2 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 18 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 0 / 16.0, 16 / 16.0))
  };

  private static final Quad[] azaleaBush = Model.join(new Quad[]{
          new Quad(
              new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
              new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
              new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
              new Vector4(4 / 16.0, 12 / 16.0, 4 / 16.0, 12 / 16.0)
          ),
          new Quad(
              new Vector3(4 / 16.0, 15.9 / 16.0, 4 / 16.0),
              new Vector3(12 / 16.0, 15.9 / 16.0, 4 / 16.0),
              new Vector3(4 / 16.0, 15.9 / 16.0, 12 / 16.0),
              new Vector4(4 / 16.0, 12 / 16.0, 12 / 16.0, 4 / 16.0)
          ),
          new Quad(
              new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
              new Vector3(12 / 16.0, 16 / 16.0, 4 / 16.0),
              new Vector3(4 / 16.0, 8 / 16.0, 4 / 16.0),
              new Vector4(12 / 16.0, 4 / 16.0, 11 / 16.0, 3 / 16.0)
          ),
          new Quad(
              new Vector3(12 / 16.0, 16 / 16.0, 4 / 16.0),
              new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
              new Vector3(12 / 16.0, 8 / 16.0, 4 / 16.0),
              new Vector4(4 / 16.0, 12 / 16.0, 11 / 16.0, 3 / 16.0)
          ),
          new Quad(
              new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
              new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
              new Vector3(4 / 16.0, 8 / 16.0, 12 / 16.0),
              new Vector4(4 / 16.0, 12 / 16.0, 11 / 16.0, 3 / 16.0)
          ),
          new Quad(
              new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
              new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
              new Vector3(12 / 16.0, 8 / 16.0, 12 / 16.0),
              new Vector4(12 / 16.0, 4 / 16.0, 11 / 16.0, 3 / 16.0)
          ),
          new Quad(
              new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
              new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
              new Vector3(4 / 16.0, 8 / 16.0, 12 / 16.0),
              new Vector4(12 / 16.0, 4 / 16.0, 11 / 16.0, 3 / 16.0)
          ),
          new Quad(
              new Vector3(4 / 16.0, 16 / 16.0, 4 / 16.0),
              new Vector3(4 / 16.0, 16 / 16.0, 12 / 16.0),
              new Vector3(4 / 16.0, 8 / 16.0, 4 / 16.0),
              new Vector4(4 / 16.0, 12 / 16.0, 11 / 16.0, 3 / 16.0)
          ),
          new Quad(
              new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
              new Vector3(12 / 16.0, 16 / 16.0, 4 / 16.0),
              new Vector3(12 / 16.0, 8 / 16.0, 12 / 16.0),
              new Vector4(4 / 16.0, 12 / 16.0, 11 / 16.0, 3 / 16.0)
          ),
          new Quad(
              new Vector3(12 / 16.0, 16 / 16.0, 4 / 16.0),
              new Vector3(12 / 16.0, 16 / 16.0, 12 / 16.0),
              new Vector3(12 / 16.0, 8 / 16.0, 4 / 16.0),
              new Vector4(12 / 16.0, 4 / 16.0, 11 / 16.0, 3 / 16.0)
          )
      },
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(2.6 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(13.4 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(2.6 / 16.0, 4 / 16.0, 8 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(13.4 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(2.6 / 16.0, 16 / 16.0, 8 / 16.0),
              new Vector3(13.4 / 16.0, 4 / 16.0, 8 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
          )
      }, Math.toRadians(45)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(8 / 16.0, 16 / 16.0, 13.4 / 16.0),
              new Vector3(8 / 16.0, 16 / 16.0, 2.6 / 16.0),
              new Vector3(8 / 16.0, 4 / 16.0, 13.4 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 16 / 16.0, 2.6 / 16.0),
              new Vector3(8 / 16.0, 16 / 16.0, 13.4 / 16.0),
              new Vector3(8 / 16.0, 4 / 16.0, 2.6 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
          )
      }, Math.toRadians(45))
  );

  private static final Texture[] tex = {
      Texture.flowerPot, Texture.flowerPot, Texture.flowerPot, Texture.flowerPot, Texture.dirt,
  };

  private static final Texture[] azaleaBushTex = {
      Texture.pottedAzaleaBushTop, Texture.pottedAzaleaBushTop,
      Texture.pottedAzaleaBushSide, Texture.pottedAzaleaBushSide,
      Texture.pottedAzaleaBushSide, Texture.pottedAzaleaBushSide,
      Texture.pottedAzaleaBushSide, Texture.pottedAzaleaBushSide,
      Texture.pottedAzaleaBushSide, Texture.pottedAzaleaBushSide,
      Texture.pottedAzaleaBushPlant, Texture.pottedAzaleaBushPlant,
      Texture.pottedAzaleaBushPlant, Texture.pottedAzaleaBushPlant
  };

  private static final Texture[] floweringAzaleaBushTex = {
      Texture.pottedFloweringAzaleaBushTop, Texture.pottedFloweringAzaleaBushTop,
      Texture.pottedFloweringAzaleaBushSide, Texture.pottedFloweringAzaleaBushSide,
      Texture.pottedFloweringAzaleaBushSide, Texture.pottedFloweringAzaleaBushSide,
      Texture.pottedFloweringAzaleaBushSide, Texture.pottedFloweringAzaleaBushSide,
      Texture.pottedFloweringAzaleaBushSide, Texture.pottedFloweringAzaleaBushSide,
      Texture.pottedAzaleaBushPlant, Texture.pottedAzaleaBushPlant,
      Texture.pottedAzaleaBushPlant, Texture.pottedAzaleaBushPlant
  };

  public static boolean intersect(Ray ray, Scene scene) {
    int flowerKind = ray.getBlockData();
    return intersect(ray, scene, Kind.values()[flowerKind-1]);
  }

  public static boolean intersect(Ray ray, Scene scene, Kind kind) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (int i = 0; i < boxes.length; ++i) {
      if (boxes[i].intersect(ray)) {
        tex[i].getColor(ray);
        ray.t = ray.tNext;
        hit = true;
      }
    }
    switch (kind) {
      case NONE:
        break;
      case POPPY:
        hit |= intersect(flower, ray, Texture.poppy);
        break;
      case DANDELION:
        hit |= intersect(flower, ray, Texture.dandelion);
        break;
      case OAK_SAPLING:
        hit |= intersect(flowerSmall, ray, Texture.oakSapling);
        break;
      case SPRUCE_SAPLING:
        hit |= intersect(flowerSmall, ray, Texture.spruceSapling);
        break;
      case BIRCH_SAPLING:
        hit |= intersect(flowerSmall, ray, Texture.birchSapling);
        break;
      case JUNGLE_SAPLING:
        hit |= intersect(flowerSmall, ray, Texture.jungleSapling);
        break;
      case ACACIA_SAPLING:
        hit |= intersect(flowerSmall, ray, Texture.acaciaSapling);
        break;
      case DARK_OAK_SAPLING:
        hit |= intersect(flowerSmall, ray, Texture.darkOakSapling);
        break;
      case RED_MUSHROOM:
        hit |= intersect(flower, ray, Texture.redMushroom);
        break;
      case BROWN_MUSHROOM:
        hit |= intersect(flower, ray, Texture.brownMushroom);
        break;
      case CACTUS:
        hit |= cactus(ray);
        break;
      case DEAD_BUSH:
        hit |= intersect(flowerSmall, ray, Texture.deadBush);
        break;
      case FERN:
        if (intersect(flowerSmall, ray, Texture.fern)) {
          float[] biomeColor = ray.getBiomeGrassColor(scene);
          ray.color.x *= biomeColor[0];
          ray.color.y *= biomeColor[1];
          ray.color.z *= biomeColor[2];
          hit = true;
        }
        break;
      case BLUE_ORCHID:
        hit |= intersect(flowerSmall, ray, Texture.blueOrchid);
        break;
      case ALLIUM:
        hit |= intersect(flowerSmall, ray, Texture.allium);
        break;
      case AZURE_BLUET:
        hit |= intersect(flowerSmall, ray, Texture.azureBluet);
        break;
      case RED_TULIP:
        hit |= intersect(flowerSmall, ray, Texture.redTulip);
        break;
      case ORANGE_TULIP:
        hit |= intersect(flowerSmall, ray, Texture.orangeTulip);
        break;
      case WHITE_TULIP:
        hit |= intersect(flowerSmall, ray, Texture.whiteTulip);
        break;
      case PINK_TULIP:
        hit |= intersect(flowerSmall, ray, Texture.pinkTulip);
        break;
      case OXEYE_DAISY:
        hit |= intersect(flowerSmall, ray, Texture.oxeyeDaisy);
        break;
      case BAMBOO:
        hit |= intersect(bamboo, ray, Texture.bambooStalk);
        hit |= intersect(bambooLeaf, ray, Texture.bambooSingleLeaf);
        break;
      case CORNFLOWER:
        hit |= intersect(flowerSmall, ray, Texture.cornflower);
        break;
      case LILY_OF_THE_VALLEY:
        hit |= intersect(flowerSmall, ray, Texture.lilyOfTheValley);
        break;
      case WITHER_ROSE:
        hit |= intersect(flowerSmall, ray, Texture.witherRose);
        break;
      case WARPED_FUNGUS:
        hit |= intersect(flowerSmall, ray, Texture.warpedFungus);
        break;
      case CRIMSON_FUNGUS:
        hit |= intersect(flowerSmall, ray, Texture.crimsonFungus);
        break;
      case WARPED_ROOTS:
        hit |= intersect(flowerSmall, ray, Texture.warpedRootsPot);
        break;
      case CRIMSON_ROOTS:
        hit |= intersect(flowerSmall, ray, Texture.crimsonRootsPot);
        break;
      case AZALEA_BUSH:
        hit |= intersect(azaleaBush, ray, azaleaBushTex);
        break;
      case FLOWERING_AZALEA_BUSH:
        hit |= intersect(azaleaBush, ray, floweringAzaleaBushTex);
        break;
    }
    if (hit) {
      ray.color.w = 1;
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

  private static boolean intersect(Quad[] quads, Ray ray, Texture texture) {
    boolean hit = false;
    for (Quad quad : quads) {
      if (quad.intersect(ray)) {
        float[] color = texture.getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          hit = true;
        }
      }
    }
    return hit;
  }

  private static boolean intersect(Quad[] quads, Ray ray, Texture[] textures) {
    boolean hit = false;
    for (int i = 0; i < quads.length; i++) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        float[] color = textures[i].getColor(ray.u, ray.v);
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          ray.t = ray.tNext;
          ray.n.set(quad.n);
          hit = true;
        }
      }
    }
    return hit;
  }

  private static boolean cactus(Ray ray) {
    if (cactus.intersect(ray)) {
      if (ray.n.y > 0) {
        Texture.cactusTop.getColor(ray);
      } else {
        Texture.cactusSide.getColor(ray);
      }
      ray.color.w = 1;
      ray.t = ray.tNext;
      return true;
    }
    return false;
  }
}
