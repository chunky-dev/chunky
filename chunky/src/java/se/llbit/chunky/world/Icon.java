/* Copyright (c) 2012-2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.world;

import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.Texture;
import se.llbit.resources.ImageLoader;

/**
 * A collection of icon images for Chunky.
 *
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Icon extends Texture {
  public static final Icon bed = new Icon("bed");
  public static final Icon cake = new Icon("cake");
  public static final Icon fence = new Icon("fence");
  public static final Icon ironDoor = new Icon("iron-door");
  public static final Icon noteBlock = new Icon("note-block");
  public static final Icon signPost = new Icon("sign-post");
  public static final Icon stoneButton = new Icon("stone-button");
  public static final Icon stonePressurePlate = new Icon("stone-pressure-plate");
  public static final Icon stoneBrickStairs = new Icon("stone-brick-stairs");
  public static final Icon stoneStairs = new Icon("stone-stairs");
  public static final Icon wallSign = new Icon("wall-sign");
  public static final Icon woodenDoor = new Icon("wooden-door");
  public static final Icon woodenPressurePlate = new Icon("wooden-pressure-plate");
  public static final Icon woodenStairs = new Icon("wooden-stairs");
  public static final Icon skyboxUp = new Icon("skybox-up");
  public static final Icon skyboxDown = new Icon("skybox-down");
  public static final Icon skyboxLeft = new Icon("skybox-left");
  public static final Icon skyboxRight = new Icon("skybox-right");
  public static final Icon skyboxFront = new Icon("skybox-front");
  public static final Icon skyboxBack = new Icon("skybox-back");
  public static final Icon isoNE = new Icon("iso-ne");
  public static final Icon isoWN = new Icon("iso-wn");
  public static final Icon isoSW = new Icon("iso-sw");
  public static final Icon isoES = new Icon("iso-es");
  public static final Icon wrench = new Icon("wrench");
  public static final Icon pencil = new Icon("pencil");
  public static final Icon MC_1_13 = new Icon("1_13");
  public static final Icon map = new Icon("map");
  public static final Icon mapSelected = new Icon("map-selected");
  public static final Icon disk = new Icon("disk");
  public static final Icon load = new Icon("load");
  public static final Icon save = new Icon("save");
  public static final Icon play = new Icon("play");
  public static final Icon pause = new Icon("pause");
  public static final Icon stop = new Icon("stop");
  public static final Icon clear = new Icon("clear");
  public static final Icon reload = new Icon("reload");
  public static final Icon sky = new Icon("sky");
  public static final Icon camera = new Icon("camera");
  public static final Icon water = new Icon("water");
  public static final Icon sun = new Icon("sun");
  public static final Icon question = new Icon("question");
  public static final Icon eye = new Icon("eye");
  public static final Icon scale = new Icon("scale");
  public static final Texture grass = new Texture("grass-side-saturated");
  public static final Texture netherrack = new Texture("netherrack");
  public static final Texture endStone = new Texture("end-stone");
  public static final Texture failed = new Icon("failed");

  public static final Icon face = new Icon("face");
  public static final Icon face_t = new Icon("face_t");
  public static final Icon home = new Icon("home");
  public static final Icon home_t = new Icon("home_t");
  public static final Icon clock = new Icon("clock");
  public static final Icon unknown = new Icon("unknown");
  public static final Icon corruptLayer = new Icon(ImageLoader.missingImage);

  public Icon(String resourceName) {
    super(ImageLoader.readNonNull("icons/" + resourceName + ".png"));
  }

  protected Icon(BitmapImage image) {
    super(image);
  }
}
