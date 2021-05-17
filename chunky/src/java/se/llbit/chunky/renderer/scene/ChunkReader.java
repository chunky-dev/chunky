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

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.ChunkData;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.World;
import se.llbit.log.Log;
import se.llbit.nbt.CompoundTag;

/**
 * Reads {@link ChunkData} from a {@link World} and allows them to be consumed.
 *
 * <p>
 * ChunkData is only guaranteed to be correct during the current iteration. Using data from a prior
 * iteration will result in reading invalid data.
 */
public class ChunkReader {

  private final Supplier<ChunkData> chunkDataFactory;
  private final World world;
  private final BlockPalette palette;
  private final Collection<ChunkPosition> chunksToLoad;
  private final ExecutorService executor;

  public ChunkReader(Supplier<ChunkData> chunkDataFactory, World world, BlockPalette palette,
      Collection<ChunkPosition> chunksToLoad, ExecutorService executorService) {

    this.chunkDataFactory = chunkDataFactory;
    this.world = world;
    this.palette = palette;
    this.chunksToLoad = chunksToLoad;
    this.executor = executorService;
  }

  /**
   * Consumer for {@link ChunkData}. This consumer additionally provides the {@link ChunkPosition}
   * as well as the current iteration number.
   */
  @FunctionalInterface
  public interface ChunkDataConsumer {

    void accept(ChunkPosition chunkPosition, ChunkData chunkData, int iteration);
  }

  /**
   * Read all {@link ChunkData} and apply the {@code consumer} to the result.
   */
  public void forEach(ChunkDataConsumer consumer) {
    ChunkData readyChunk = chunkDataFactory.get();
    ChunkData loadingChunk = chunkDataFactory.get();

    ChunkPosition[] chunkPositions = chunksToLoad.toArray(new ChunkPosition[0]);

    ChunkData finalReadyChunk = readyChunk;
    Future<?> nextChunkDataTask = executor
        .submit(() -> { //Initialise first chunk data for the for loop
          world.getChunk(chunkPositions[0]).getChunkData(finalReadyChunk, palette);
        });

    for (int i = 0; i < chunkPositions.length; i++) {
      try {
        nextChunkDataTask.get(50, TimeUnit.MILLISECONDS);
      } catch (TimeoutException | InterruptedException logged) { // If except, load the chunk synchronously
        if (logged instanceof TimeoutException) {
          Log.info("Chunk loading timed out.");
        } else {
          Log.warn("Chunky loading interrupted.", logged);
        }
        world.getChunk(chunkPositions[i]).getChunkData(readyChunk, palette);
      } catch (ExecutionException e) {
        throw new RuntimeException(e.getCause());
      }

      if (i + 1 < chunkPositions.length) { //if has next request next
        final int finalI = i;
        ChunkData finalLoadingChunk = loadingChunk;
        nextChunkDataTask = executor.submit(() -> { //Initialise next chunk data for the for loop
          world.getChunk(chunkPositions[finalI + 1]).getChunkData(finalLoadingChunk, palette);
        });
      }
      consumer.accept(chunkPositions[i], new ReadOnlyChunkData(readyChunk), i + 1);

      // Swap chunks for next iteration
      ChunkData temp = loadingChunk;
      loadingChunk = readyChunk;
      readyChunk = temp;
    }

    executor.shutdown();
  }


  /**
   * Wrapper around ChunkData that prevents modification of the underlying ChunkData.
   */
  public static class ReadOnlyChunkData implements ChunkData {

    final ChunkData chunkData;

    private ReadOnlyChunkData(ChunkData chunkData) {
      this.chunkData = chunkData;
    }

    @Override
    public int minY() {
      return chunkData.minY();
    }

    @Override
    public int maxY() {
      return chunkData.maxY();
    }

    @Override
    public int getBlockAt(int x, int y, int z) {
      return chunkData.getBlockAt(x, y, z);
    }

    @Override
    public boolean isBlockOnEdge(int x, int y, int z) {
      return chunkData.isBlockOnEdge(x, y, z);
    }

    @Override
    public Collection<CompoundTag> getTileEntities() {
      return chunkData.getTileEntities();
    }

    @Override
    public Collection<CompoundTag> getEntities() {
      return chunkData.getEntities();
    }

    @Override
    public byte getBiomeAt(int x, int y, int z) {
      return chunkData.getBiomeAt(x, y, z);
    }

    @Override
    public boolean isEmpty() {
      return chunkData.isEmpty();
    }

    @Override
    public void setBlockAt(int x, int y, int z, int block) {
      throw new UnsupportedOperationException("ChunkData is Read Only.");
    }

    @Override
    public void addTileEntity(CompoundTag tileEntity) {
      throw new UnsupportedOperationException("ChunkData is Read Only.");
    }

    @Override
    public void addEntity(CompoundTag entity) {
      throw new UnsupportedOperationException("ChunkData is Read Only.");
    }

    @Override
    public void setBiomeAt(int x, int y, int z, byte biome) {
      throw new UnsupportedOperationException("ChunkData is Read Only.");
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException("ChunkData is Read Only.");
    }
  }
}
