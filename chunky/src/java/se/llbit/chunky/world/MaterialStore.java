/* Copyright (c) 2019 Jesper Öqvist <jesper@llbit.se>
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

import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.BlockProvider;
import se.llbit.chunky.block.BlockSpec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

// TODO: introduce block tags
public class MaterialStore {
  public static final Map<String, Collection<Block>> collections = new LinkedHashMap<>();
  public static final Collection<String> idMap = new ArrayList<String>();

  static {
    //collections.put("all:blocks", Arrays.asList(blocks));
    //collections.put("all:water", Arrays.asList(IdBlock.WATER, IdBlock.STATIONARYWATER));
    //collections.put("all:lava", Arrays.asList(IdBlock.LAVA, IdBlock.STATIONARYLAVA));
    BlockSpec.blockProviders.forEach(provider -> idMap.addAll(provider.getSupportedBlocks()));
  }

  public static void loadDefaultMaterialProperties() {

  }
}
