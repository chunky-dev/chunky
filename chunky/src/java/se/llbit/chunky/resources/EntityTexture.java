/* Copyright (c) 2015 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.resources;

import se.llbit.math.Vector4;

/**
 * Stores additional UV coordinates used for entity textures.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class EntityTexture extends Texture {

  // Head layer coordinates.
  public final Vector4 headFront = new Vector4();
  public final Vector4 headBack = new Vector4();
  public final Vector4 headTop = new Vector4();
  public final Vector4 headBottom = new Vector4();
  public final Vector4 headRight = new Vector4();
  public final Vector4 headLeft = new Vector4();

  // Hat layer coordinates.
  public final Vector4 hatFront = new Vector4();
  public final Vector4 hatBack = new Vector4();
  public final Vector4 hatTop = new Vector4();
  public final Vector4 hatBottom = new Vector4();
  public final Vector4 hatRight = new Vector4();
  public final Vector4 hatLeft = new Vector4();

  // Chest layer coordinates
  public final Vector4 chestFront = new Vector4();
  public final Vector4 chestBack = new Vector4();
  public final Vector4 chestTop = new Vector4();
  public final Vector4 chestBottom = new Vector4();
  public final Vector4 chestRight = new Vector4();
  public final Vector4 chestLeft = new Vector4();

  // Jacket layer coordinates
  public final Vector4 jacketFront = new Vector4();
  public final Vector4 jacketBack = new Vector4();
  public final Vector4 jacketTop = new Vector4();
  public final Vector4 jacketBottom = new Vector4();
  public final Vector4 jacketRight = new Vector4();
  public final Vector4 jacketLeft = new Vector4();

  // Right leg layer coordinates
  public final Vector4 rightLegFront = new Vector4();
  public final Vector4 rightLegBack = new Vector4();
  public final Vector4 rightLegTop = new Vector4();
  public final Vector4 rightLegBottom = new Vector4();
  public final Vector4 rightLegRight = new Vector4();
  public final Vector4 rightLegLeft = new Vector4();

  // Right pant layer coordinates
  public final Vector4 rightPantFront = new Vector4();
  public final Vector4 rightPantBack = new Vector4();
  public final Vector4 rightPantTop = new Vector4();
  public final Vector4 rightPantBottom = new Vector4();
  public final Vector4 rightPantRight = new Vector4();
  public final Vector4 rightPantLeft = new Vector4();

  // Left leg layer coordinates
  public final Vector4 leftLegFront = new Vector4();
  public final Vector4 leftLegBack = new Vector4();
  public final Vector4 leftLegTop = new Vector4();
  public final Vector4 leftLegBottom = new Vector4();
  public final Vector4 leftLegRight = new Vector4();
  public final Vector4 leftLegLeft = new Vector4();

  // Left pant layer coordinates
  public final Vector4 leftPantFront = new Vector4();
  public final Vector4 leftPantBack = new Vector4();
  public final Vector4 leftPantTop = new Vector4();
  public final Vector4 leftPantBottom = new Vector4();
  public final Vector4 leftPantRight = new Vector4();
  public final Vector4 leftPantLeft = new Vector4();

  // Right arm layer coordinates
  public final Vector4 rightArmFront = new Vector4();
  public final Vector4 rightArmBack = new Vector4();
  public final Vector4 rightArmTop = new Vector4();
  public final Vector4 rightArmBottom = new Vector4();
  public final Vector4 rightArmRight = new Vector4();
  public final Vector4 rightArmLeft = new Vector4();

  // Right sleeve layer coordinates
  public final Vector4 rightSleeveFront = new Vector4();
  public final Vector4 rightSleeveBack = new Vector4();
  public final Vector4 rightSleeveTop = new Vector4();
  public final Vector4 rightSleeveBottom = new Vector4();
  public final Vector4 rightSleeveRight = new Vector4();
  public final Vector4 rightSleeveLeft = new Vector4();

  // Left arm layer coordinates
  public final Vector4 leftArmFront = new Vector4();
  public final Vector4 leftArmBack = new Vector4();
  public final Vector4 leftArmTop = new Vector4();
  public final Vector4 leftArmBottom = new Vector4();
  public final Vector4 leftArmRight = new Vector4();
  public final Vector4 leftArmLeft = new Vector4();

  // Left sleeve layer coordinates
  public final Vector4 leftSleeveFront = new Vector4();
  public final Vector4 leftSleeveBack = new Vector4();
  public final Vector4 leftSleeveTop = new Vector4();
  public final Vector4 leftSleeveBottom = new Vector4();
  public final Vector4 leftSleeveRight = new Vector4();
  public final Vector4 leftSleeveLeft = new Vector4();
}
