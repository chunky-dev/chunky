/* Copyright (c) 2021 Chunky contributors
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
package se.llbit.chunky.renderer.scene;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.renderer.scene.ChunkReader.ReadOnlyChunkData;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;
import se.llbit.nbt.CompoundTag;

public class ChunkReaderTest {

  @Test
  public void forEach() {
    List<ChunkPosition> chunksToLoad = Stream
        .of(ChunkPosition.get(1, 1), ChunkPosition.get(1, 2), ChunkPosition.get(1, 3))
        .collect(
            Collectors.toList());
    ChunkData chunkData1 = new MockChunkData("A");
    ChunkData chunkData2 = new MockChunkData("B");
    Iterator<ChunkData> iterator = Arrays.asList(chunkData1, chunkData2).iterator();
    MockWorld world = new MockWorld();
    ListeningExecutorService executorService = MoreExecutors.newDirectExecutorService();
    ChunkReader chunkReader = new ChunkReader(iterator::next, world,
        new BlockPalette(new HashMap<>(), new ArrayList<>()), chunksToLoad,
        executorService);

    List<ChunkPosition> consumedPositions = new ArrayList<>();
    List<ReadOnlyChunkData> consumedChunkData = new ArrayList<>();
    List<Integer> consumedProgress = new ArrayList<>();
    chunkReader.forEach((position, chunkData, progress) -> {
      consumedPositions.add(position);
      consumedChunkData.add((ReadOnlyChunkData) chunkData);
      consumedProgress.add(progress);
    });

    assertThat(consumedPositions)
        .containsExactly(ChunkPosition.get(1, 1), ChunkPosition.get(1, 2), ChunkPosition.get(1, 3));
    assertThat(consumedChunkData.stream().map(roc -> roc.chunkData).collect(Collectors.toList()))
        .containsExactly(chunkData1, chunkData2, chunkData1).inOrder();
    assertThat(world.mockChunk.chunksCalledWith)
        .containsExactly(chunkData1, chunkData2, chunkData1).inOrder();
    assertThat(consumedProgress).containsExactly(1, 2, 3).inOrder();
    assertThat(executorService.isShutdown()).isTrue();
  }

  private static final class MockChunk extends Chunk {

    private final List<ChunkData> chunksCalledWith;

    public MockChunk(World world) {
      super(ChunkPosition.get(0, 0), world);
      this.chunksCalledWith = new ArrayList<>();
    }

    @Override
    public synchronized ChunkData getChunkData(ChunkData reuseChunkData, BlockPalette palette) {
      chunksCalledWith.add(reuseChunkData);
      return reuseChunkData;
    }
  }

  private static final class MockWorld extends World {

    private final MockChunk mockChunk;

    protected MockWorld() {
      super("Mock World", null, 1, Collections.emptySet(), false, 0, 0);
      mockChunk = new MockChunk(this);
    }

    @Override
    public synchronized Chunk getChunk(ChunkPosition pos) {
      return mockChunk;
    }
  }

  private static final class MockChunkData implements ChunkData {

    private final String name;

    private MockChunkData(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return "MockChunkData: " + name;
    }

    @Override
    public int minY() {
      return 0;
    }

    @Override
    public int maxY() {
      return 0;
    }

    @Override
    public int getBlockAt(int x, int y, int z) {
      return 0;
    }

    @Override
    public void setBlockAt(int x, int y, int z, int block) {

    }

    @Override
    public boolean isBlockOnEdge(int x, int y, int z) {
      return false;
    }

    @Override
    public Collection<CompoundTag> getTileEntities() {
      return null;
    }

    @Override
    public void addTileEntity(CompoundTag tileEntity) {

    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public Collection<CompoundTag> getEntities() {
      return null;
    }

    @Override
    public void addEntity(CompoundTag entity) {

    }

    @Override
    public byte getBiomeAt(int x, int y, int z) {
      return 0;
    }

    @Override
    public void setBiomeAt(int x, int y, int z, byte biome) {

    }

    @Override
    public void clear() {

    }
  }
}