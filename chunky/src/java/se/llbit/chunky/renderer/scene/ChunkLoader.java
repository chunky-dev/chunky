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

import static java.lang.Math.min;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.block.Air;
import se.llbit.chunky.block.Block;
import se.llbit.chunky.block.Lava;
import se.llbit.chunky.block.Water;
import se.llbit.chunky.chunk.BlockPalette;
import se.llbit.chunky.chunk.GenericChunkData;
import se.llbit.chunky.chunk.SimpleChunkData;
import se.llbit.chunky.entity.ArmorStand;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.Lectern;
import se.llbit.chunky.entity.PaintingEntity;
import se.llbit.chunky.entity.PlayerEntity;
import se.llbit.chunky.entity.Poseable;
import se.llbit.chunky.renderer.EmitterSamplingStrategy;
import se.llbit.chunky.world.Biomes;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.Heightmap;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.WorldTexture;
import se.llbit.json.JsonObject;
import se.llbit.log.Log;
import se.llbit.math.Grid;
import se.llbit.math.Grid.EmitterPosition;
import se.llbit.math.Octree;
import se.llbit.math.Octree.BlockBounds;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector3i;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.Tag;
import se.llbit.util.MCDownloader;
import se.llbit.util.TaskTracker;
import se.llbit.util.TaskTracker.Task;

/**
 * Loads chunks into octrees and other associated data structures.
 */
public class ChunkLoader {

  private final String octreeImplementation;
  private final int yMin;
  private final int yMax;
  private final int yClipMin;
  private final int yClipMax;
  private final EmitterSamplingStrategy emitterSamplingStrategy;
  private final int gridSize;

  public ChunkLoader(String octreeImplementation, int yMin, int yMax, int yClipMin, int yClipMax,
      EmitterSamplingStrategy emitterSamplingStrategy, int gridSize) {
    this.octreeImplementation = octreeImplementation;
    this.yMin = yMin;
    this.yMax = yMax;
    this.yClipMin = yClipMin;
    this.yClipMax = yClipMax;
    this.gridSize = gridSize;
    this.emitterSamplingStrategy = emitterSamplingStrategy;
  }

  /**
   * Result from loading chunks.
   */
  public static class ChunkLoadResult {

    final BlockPalette blockPalette;
    final Octree worldOctree;
    final Octree waterOctree;
    Grid emitterGrid;
    final Collection<Entity> entities;
    final Collection<Entity> actors;
    final Map<PlayerEntity, JsonObject> profiles;
    final Vector3i origin;
    final WorldTexture foliageTexture;
    final WorldTexture waterTexture;
    final WorldTexture grassTexture;
    final Collection<ChunkPosition> loadedChunks;

    public ChunkLoadResult(String octreeImplementation, int requiredDepth,
        EmitterSamplingStrategy emitterSamplingStrategy, int gridSize) {
      blockPalette = new BlockPalette();
      worldOctree = new Octree(octreeImplementation, requiredDepth);
      waterOctree = new Octree(octreeImplementation, requiredDepth);
      emitterGrid =
          emitterSamplingStrategy.equals(EmitterSamplingStrategy.NONE) ? null : new Grid(gridSize);
      entities = new LinkedList<>();
      actors = new LinkedList<>();
      profiles = new HashMap<>();
      origin = new Vector3i();
      foliageTexture = new WorldTexture();
      waterTexture = new WorldTexture();
      grassTexture = new WorldTexture();
      loadedChunks = new HashSet<>();
    }
  }

  /**
   * Loads chunks into octrees and other associated data.
   *
   * @param world        world to load
   * @param chunksToLoad chunks to load
   * @param taskTracker  tracker for keeping track of progress
   * @param loadActors   whether or not to load actors from the world
   */
  public ChunkLoadResult loadChunks(World world, Collection<ChunkPosition> chunksToLoad,
      TaskTracker taskTracker, boolean loadActors) {
    BlockBounds bounds = Octree.calculateBounds(chunksToLoad);
    int requiredDepth = Octree.calculateOctreeRequiredDepth(bounds, yMax, yMin);

    ChunkLoadResult result = new ChunkLoadResult(octreeImplementation, requiredDepth,
        emitterSamplingStrategy, gridSize);

    try (Task task = taskTracker.task("(1/6) Loading regions")) {
      task.update(2, 1);
      result.origin.set(Octree.calculateOctreeOrigin(yMax, yMin, bounds, false));

      // Parse the regions first - force chunk lists to be populated!
      Set<ChunkPosition> regions = new HashSet<>();
      for (ChunkPosition cp : chunksToLoad) {
        regions.add(cp.getRegionPosition());
      }

      for (ChunkPosition region : regions) {
        world.getRegion(region).parse();
      }
    }

    try (Task task = taskTracker.task("(2/6) Loading result.entities")) {
      if (loadActors && PersistentSettings.getLoadPlayers()) {
        // We don't load actor entities if some already exists. Loading actor entities
        // risks resetting posed actors when reloading chunks for an existing scene.
        Collection<PlayerEntity> players = world.playerEntities();
        int done = 1;
        int target = players.size();
        for (PlayerEntity entity : players) {
          entity.randomPose();
          task.update(target, done);
          done += 1;
          JsonObject profile;
          try {
            profile = MCDownloader.fetchProfile(entity.uuid);
          } catch (IOException e) {
            Log.error(e);
            profile = new JsonObject();
          }
          result.profiles.put(entity, profile);
          result.actors.add(entity);
        }
      }
    }

    Set<ChunkPosition> nonEmptyChunks = new HashSet<>();
    Heightmap biomeIdMap = new Heightmap();

    ChunkReader chunkReader = new ChunkReader(
        isTallWorld(world) ? GenericChunkData::new : SimpleChunkData::new, world,
        result.blockPalette,
        chunksToLoad,
        Executors.newSingleThreadExecutor());

    try (Task task = taskTracker.task("(3/6) Loading chunks")) {
      int target = chunksToLoad.size();

      int[] cubeWorldBlocks = new int[16 * 16 * 16];
      int[] cubeWaterBlocks = new int[16 * 16 * 16];

      chunkReader.forEach((cp, chunkData, progress) -> {
        task.updateEta(target, progress);

        if (result.loadedChunks.contains(cp)) {
          return;
        }

        result.loadedChunks.add(cp);

        int wx0 = cp.x * 16; // Start of this chunk in world coordinates.
        int wz0 = cp.z * 16;
        for (int cz = 0; cz < 16; ++cz) {
          int wz = cz + wz0;
          for (int cx = 0; cx < 16; ++cx) {
            int wx = cx + wx0;
            int biomeId =
                0xFF & chunkData.getBiomeAt(cx, 0, cz); // TODO add vertical biomes support (1.15+)
            biomeIdMap.set(biomeId, wx, wz);
          }
        }

        // Load result.entities from the chunk:
        for (CompoundTag tag : chunkData.getEntities()) {
          Tag posTag = tag.get("Pos");
          if (posTag.isList()) {
            ListTag pos = posTag.asList();
            double x = pos.get(0).doubleValue();
            double y = pos.get(1).doubleValue();
            double z = pos.get(2).doubleValue();

            if (y >= yClipMin && y < yClipMax) {
              String id = tag.get("id").stringValue("");
              if (id.equals("minecraft:painting") || id.equals("Painting")) {
                // Before 1.12 paintings had id=Painting.
                // After 1.12 paintings had id=minecraft:painting.
                float yaw = tag.get("Rotation").get(0).floatValue();
                result.entities.add(
                    new PaintingEntity(new Vector3(x, y, z), tag.get("Motive").stringValue(), yaw));
              } else if (id.equals("minecraft:armor_stand")) {
                result.actors.add(new ArmorStand(new Vector3(x, y, z), tag));
              }
            }
          }
        }

        int yCubeMin = yMin / 16;
        int yCubeMax = (yMax + 15) / 16;
        for (int yCube = yCubeMin; yCube < yCubeMax; ++yCube) {
          // Reset the cubes
          Arrays.fill(cubeWorldBlocks, 0);
          Arrays.fill(cubeWaterBlocks, 0);
          for (int cy = 0; cy < 16;
              ++cy) { //Uses chunk min and max, rather than global - minor optimisation for pre1.13 worlds
            int y = yCube * 16 + cy;
            if (y < yMin || y >= yMax) {
              continue;
            }
            for (int cz = 0; cz < 16; ++cz) {
              int z = cz + cp.z * 16 - result.origin.z;
              for (int cx = 0; cx < 16; ++cx) {
                int x = cx + cp.x * 16 - result.origin.x;

                int cubeIndex = (cz * 16 + cy) * 16 + cx;

                // Change the type of hidden blocks to ANY_TYPE
                boolean onEdge = y <= yMin || y >= yMax - 1 || chunkData.isBlockOnEdge(cx, y, cz);
                boolean isHidden = !onEdge
                    && result.blockPalette.get(chunkData.getBlockAt(cx + 1, y, cz)).opaque
                    && result.blockPalette.get(chunkData.getBlockAt(cx - 1, y, cz)).opaque
                    && result.blockPalette.get(chunkData.getBlockAt(cx, y + 1, cz)).opaque
                    && result.blockPalette.get(chunkData.getBlockAt(cx, y - 1, cz)).opaque
                    && result.blockPalette.get(chunkData.getBlockAt(cx, y, cz + 1)).opaque
                    && result.blockPalette.get(chunkData.getBlockAt(cx, y, cz - 1)).opaque;

                if (isHidden) {
                  cubeWorldBlocks[cubeIndex] = Octree.ANY_TYPE;
                } else {
                  int currentBlock = chunkData.getBlockAt(cx, y, cz);
                  int octNode = currentBlock;
                  Block block = result.blockPalette.get(currentBlock);

                  if (block.isEntity()) {
                    Vector3 position = new Vector3(cx + cp.x * 16, y, cz + cp.z * 16);
                    Entity entity = block.toEntity(position);

                    if (entity instanceof Poseable && !(entity instanceof Lectern
                        && !((Lectern) entity).hasBook())) {
                      // don't add the actor again if it was already loaded from json
                      if (result.actors.stream().noneMatch(actor -> {
                        if (actor.getClass().equals(entity.getClass())) {
                          Vector3 distance = new Vector3(actor.position);
                          distance.sub(entity.position);
                          return distance.lengthSquared() < Ray.EPSILON;
                        }
                        return false;
                      })) {
                        result.actors.add(entity);
                      }
                    } else {
                      result.entities.add(entity);
                      if (result.emitterGrid != null) {
                        for (EmitterPosition emitterPos : entity.getEmitterPosition()) {
                          emitterPos.x -= result.origin.x;
                          emitterPos.y -= result.origin.y;
                          emitterPos.z -= result.origin.z;
                          result.emitterGrid.addEmitter(emitterPos);
                        }
                      }
                    }

                    if (!block.isBlockWithEntity()) {
                      if (block.waterlogged) {
                        block = result.blockPalette.water;
                        octNode = result.blockPalette.waterId;
                      } else {
                        block = Air.INSTANCE;
                        octNode = result.blockPalette.airId;
                      }
                    }
                  }

                  if (block.isWaterFilled()) {
                    int waterNode = result.blockPalette.waterId;
                    if (y + 1 < yMax) {
                      if (result.blockPalette.get(chunkData.getBlockAt(cx, y + 1, cz))
                          .isWaterFilled()) {
                        waterNode = result.blockPalette.getWaterId(0, 1 << Water.FULL_BLOCK);
                      }
                    }
                    if (block.isWater()) {
                      // Move plain water blocks to the water octree.
                      octNode = result.blockPalette.airId;

                      if (!onEdge) {
                        // Perform water computation now for water blocks that are not on th edge of the chunk
                        // Test if the block has not already be marked as full
                        if (((Water) result.blockPalette.get(waterNode)).data == 0) {
                          int level0 = 8 - ((Water) block).level;
                          int corner0 = level0;
                          int corner1 = level0;
                          int corner2 = level0;
                          int corner3 = level0;

                          int level = Chunk
                              .waterLevelAt(chunkData, result.blockPalette, cx - 1, y, cz, level0);
                          corner3 += level;
                          corner0 += level;

                          level = Chunk
                              .waterLevelAt(chunkData, result.blockPalette, cx - 1, y, cz + 1,
                                  level0);
                          corner0 += level;

                          level = Chunk
                              .waterLevelAt(chunkData, result.blockPalette, cx, y, cz + 1, level0);
                          corner0 += level;
                          corner1 += level;

                          level = Chunk
                              .waterLevelAt(chunkData, result.blockPalette, cx + 1, y, cz + 1,
                                  level0);
                          corner1 += level;

                          level = Chunk
                              .waterLevelAt(chunkData, result.blockPalette, cx + 1, y, cz, level0);
                          corner1 += level;
                          corner2 += level;

                          level = Chunk
                              .waterLevelAt(chunkData, result.blockPalette, cx + 1, y, cz - 1,
                                  level0);
                          corner2 += level;

                          level = Chunk
                              .waterLevelAt(chunkData, result.blockPalette, cx, y, cz - 1, level0);
                          corner2 += level;
                          corner3 += level;

                          level = Chunk
                              .waterLevelAt(chunkData, result.blockPalette, cx - 1, y, cz - 1,
                                  level0);
                          corner3 += level;

                          corner0 = min(7, 8 - (corner0 / 4));
                          corner1 = min(7, 8 - (corner1 / 4));
                          corner2 = min(7, 8 - (corner2 / 4));
                          corner3 = min(7, 8 - (corner3 / 4));
                          waterNode = result.blockPalette
                              .getWaterId(((Water) block).level, (corner0 << Water.CORNER_0)
                                  | (corner1 << Water.CORNER_1)
                                  | (corner2 << Water.CORNER_2)
                                  | (corner3 << Water.CORNER_3));
                        }
                      } else {
                        // Water computation for water blocks on the edge of a chunk is done by the OctreeFinalizer but we need the water level information
                        waterNode = result.blockPalette.getWaterId(((Water) block).level, 0);
                      }
                    }
                    cubeWaterBlocks[cubeIndex] = waterNode;
                  } else if (y + 1 < yMax && block instanceof Lava) {
                    if (result.blockPalette
                        .get(chunkData.getBlockAt(cx, y + 1, cz)) instanceof Lava) {
                      octNode = result.blockPalette.getLavaId(0, 1 << Water.FULL_BLOCK);
                    } else if (!onEdge) {
                      // Compute lava level for blocks not on edge
                      Lava lava = (Lava) block;
                      int level0 = 8 - lava.level;
                      int corner0 = level0;
                      int corner1 = level0;
                      int corner2 = level0;
                      int corner3 = level0;

                      int level = Chunk
                          .lavaLevelAt(chunkData, result.blockPalette, cx - 1, y, cz, level0);
                      corner3 += level;
                      corner0 += level;

                      level = Chunk
                          .lavaLevelAt(chunkData, result.blockPalette, cx - 1, y, cz + 1, level0);
                      corner0 += level;

                      level = Chunk
                          .lavaLevelAt(chunkData, result.blockPalette, cx, y, cz + 1, level0);
                      corner0 += level;
                      corner1 += level;

                      level = Chunk
                          .lavaLevelAt(chunkData, result.blockPalette, cx + 1, y, cz + 1, level0);
                      corner1 += level;

                      level = Chunk
                          .lavaLevelAt(chunkData, result.blockPalette, cx + 1, y, cz, level0);
                      corner1 += level;
                      corner2 += level;

                      level = Chunk
                          .lavaLevelAt(chunkData, result.blockPalette, cx + 1, y, cz - 1, level0);
                      corner2 += level;

                      level = Chunk
                          .lavaLevelAt(chunkData, result.blockPalette, cx, y, cz - 1, level0);
                      corner2 += level;
                      corner3 += level;

                      level = Chunk
                          .lavaLevelAt(chunkData, result.blockPalette, cx - 1, y, cz - 1, level0);
                      corner3 += level;

                      corner0 = min(7, 8 - (corner0 / 4));
                      corner1 = min(7, 8 - (corner1 / 4));
                      corner2 = min(7, 8 - (corner2 / 4));
                      corner3 = min(7, 8 - (corner3 / 4));
                      octNode = result.blockPalette.getLavaId(
                          lava.level,
                          (corner0 << Water.CORNER_0)
                              | (corner1 << Water.CORNER_1)
                              | (corner2 << Water.CORNER_2)
                              | (corner3 << Water.CORNER_3)
                      );
                    }
                  }
                  cubeWorldBlocks[cubeIndex] = octNode;

                  if (result.emitterGrid != null && block.emittance > 1e-4) {
                    result.emitterGrid.addEmitter(
                        new EmitterPosition(x + 0.5f, y - result.origin.y + 0.5f, z + 0.5f));
                  }
                }
              }
            }
          }
          result.worldOctree.setCube(4, cubeWorldBlocks, cp.x * 16 - result.origin.x,
              yCube * 16 - result.origin.y,
              cp.z * 16 - result.origin.z);
          result.waterOctree.setCube(4, cubeWaterBlocks, cp.x * 16 - result.origin.x,
              yCube * 16 - result.origin.y,
              cp.z * 16 - result.origin.z);
        }

        // Block result.entities are also called "tile result.entities". These are extra bits of metadata
        // about certain blocks or result.entities.
        // Block result.entities are loaded after the base block data so that metadata can be updated.
        for (CompoundTag entityTag : chunkData.getTileEntities()) {
          int y = entityTag.get("y").intValue(0);
          if (y >= yMin && y < yMax) {
            int x = entityTag.get("x").intValue(0) - wx0; // Chunk-local coordinates.
            int z = entityTag.get("z").intValue(0) - wz0;
            if (x < 0 || x > 15 || z < 0 || z > 15) {
              // Block entity is out of range (bad chunk data?), ignore it
              continue;
            }
            Block block = result.blockPalette.get(chunkData.getBlockAt(x, y, z));
            // Metadata is the old block data (to be replaced in future Minecraft versions?).
            Vector3 position = new Vector3(x + wx0, y, z + wz0);
            if (block.isBlockEntity()) {
              Entity blockEntity = block.toBlockEntity(position, entityTag);
              if (blockEntity == null) {
                continue;
              }
              if (blockEntity instanceof Poseable) {
                // don't add the actor again if it was already loaded from json
                if (result.actors.stream().noneMatch(actor -> {
                  if (actor.getClass().equals(blockEntity.getClass())) {
                    Vector3 distance = new Vector3(actor.position);
                    distance.sub(blockEntity.position);
                    return distance.lengthSquared() < Ray.EPSILON;
                  }
                  return false;
                })) {
                  result.actors.add(blockEntity);
                }
              } else {
                result.entities.add(blockEntity);
                if (result.emitterGrid != null) {
                  for (EmitterPosition emitterPos : blockEntity.getEmitterPosition()) {
                    emitterPos.x -= result.origin.x;
                    emitterPos.y -= result.origin.y;
                    emitterPos.z -= result.origin.z;
                    result.emitterGrid.addEmitter(emitterPos);
                  }
                }
              }
            }
          }
        }

        if (!chunkData.isEmpty()) {
          nonEmptyChunks.add(cp);
        }
      });
    }

    result.blockPalette.unsynchronize();

    try (Task task = taskTracker.task("(4/6) Finalizing octree")) {

      result.worldOctree.startFinalization();
      result.waterOctree.startFinalization();

      int done = 0;
      int target = nonEmptyChunks.size();
      for (ChunkPosition cp : nonEmptyChunks) {
        // Finalize grass and foliage textures.
        // 3x3 box blur.
        for (int x = 0; x < 16; ++x) {
          for (int z = 0; z < 16; ++z) {

            int nsum = 0;
            float[] grassMix = {0, 0, 0};
            float[] foliageMix = {0, 0, 0};
            float[] waterMix = {0, 0, 0};
            for (int sx = x - 1; sx <= x + 1; ++sx) {
              int wx = cp.x * 16 + sx;
              for (int sz = z - 1; sz <= z + 1; ++sz) {
                int wz = cp.z * 16 + sz;

                ChunkPosition ccp = ChunkPosition.get(wx >> 4, wz >> 4);
                if (nonEmptyChunks.contains(ccp)) {
                  nsum += 1;
                  int biomeId = biomeIdMap.get(wx, wz);
                  float[] grassColor = Biomes.getGrassColorLinear(biomeId);
                  grassMix[0] += grassColor[0];
                  grassMix[1] += grassColor[1];
                  grassMix[2] += grassColor[2];
                  float[] foliageColor = Biomes.getFoliageColorLinear(biomeId);
                  foliageMix[0] += foliageColor[0];
                  foliageMix[1] += foliageColor[1];
                  foliageMix[2] += foliageColor[2];
                  float[] waterColor = Biomes.getWaterColorLinear(biomeId);
                  waterMix[0] += waterColor[0];
                  waterMix[1] += waterColor[1];
                  waterMix[2] += waterColor[2];
                }
              }
            }
            grassMix[0] /= nsum;
            grassMix[1] /= nsum;
            grassMix[2] /= nsum;
            result.grassTexture
                .set(cp.x * 16 + x - result.origin.x, cp.z * 16 + z - result.origin.z, grassMix);

            foliageMix[0] /= nsum;
            foliageMix[1] /= nsum;
            foliageMix[2] /= nsum;
            result.foliageTexture
                .set(cp.x * 16 + x - result.origin.x, cp.z * 16 + z - result.origin.z, foliageMix);

            waterMix[0] /= nsum;
            waterMix[1] /= nsum;
            waterMix[2] /= nsum;
            result.waterTexture
                .set(cp.x * 16 + x - result.origin.x, cp.z * 16 + z - result.origin.z, waterMix);
          }
        }
        task.updateEta(target, done);
        done += 1;
        OctreeFinalizer.finalizeChunk(result.worldOctree, result.waterOctree, result.blockPalette,
            result.origin, cp, yMin, yMax);
      }

      result.worldOctree.endFinalization();
      result.waterOctree.endFinalization();
    }

    for (Entity entity : result.actors) {
      entity.loadDataFromOctree(result.worldOctree, result.blockPalette, result.origin);
    }

    for (Entity entity : result.entities) {
      entity.loadDataFromOctree(result.worldOctree, result.blockPalette, result.origin);
    }

    if (result.emitterGrid != null) {
      result.emitterGrid.prepare();
    }

    return result;
  }

  static boolean isTallWorld(World world) {
    return world.getVersionId() >= World.VERSION_21W06A;
  }
}
