/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.resources.Texture;
import se.llbit.resources.ImageLoader;

/**
 * Icon object.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("javadoc")
public class Icon extends Texture {
	public static final Icon bed = new Icon("bed");
	public static final Icon cake = new Icon("cake");
	public static final Icon cauldron = new Icon("cauldron");
	public static final Icon fence = new Icon("fence");
	public static final Icon ironDoor = new Icon("iron-door");
	public static final Icon lever = new Icon("lever");
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
	public static final Icon redTorchOn = new Icon("redstone-torch-on");
	public static final Icon map = new Icon("map");
	public static final Icon mapSelected = new Icon("map-selected");
	public static final Icon chunky = new Icon("chunky");
	public static final Icon lock = new Icon("lock");
	public static final Icon key = new Icon("key");
	public static final Icon disk = new Icon("disk");
	public static final Icon load = new Icon("load");
	public static final Icon save = new Icon("save");
	public static final Icon play = new Icon("play");
	public static final Icon pause = new Icon("pause");
	public static final Icon stop = new Icon("stop");
	public static final Icon reload = new Icon("reload");
	public static final Icon colors = new Icon("colors");
	public static final Icon sky = new Icon("sky");
	public static final Icon camera = new Icon("camera");
    final public static Icon water = new Icon("water");
    final public static Icon light = new Icon("light");
    final public static Icon gear = new Icon("gear");
    final public static Icon advanced = new Icon("advanced");
    final public static Icon question = new Icon("question");

	public Icon(String resourceName) {
		setTexture(ImageLoader.get("icons/" + resourceName + ".png"));
	}
}
