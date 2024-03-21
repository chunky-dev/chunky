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
package se.llbit.chunky.world.material;

import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;

import java.util.Collections;
import java.util.Map;

public class TextureMaterial extends Material {

  // Cache the Texture -> TextureMaterial association to prevent creating tons of
  // TextureMaterial using the same Texture
  static private final Map<Texture, TextureMaterial> cache = Collections.synchronizedMap(
    new ReferenceMap<>(AbstractReferenceMap.ReferenceStrength.HARD, AbstractReferenceMap.ReferenceStrength.WEAK)
  );

  public static TextureMaterial getForTexture(Texture texture) {
    TextureMaterial mat = cache.get(texture);
    if(mat == null) {
      mat = new TextureMaterial(texture);
      cache.put(texture, mat);
    }
    return mat;
  }

  public TextureMaterial(Texture texture) {
    super("texture", texture);

    if (texture == null) {
      throw new NullPointerException("Texture material may not have a null texture.");
    }
  }

}
