package se.llbit.chunky.chunk;

import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
import se.llbit.chunky.world.Chunk;
import se.llbit.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Collection;

import static se.llbit.chunky.world.Chunk.*;

public class GenericChunkData implements ChunkData {
  private Integer minSectionY = Integer.MAX_VALUE;
  private Integer maxSectionY = Integer.MIN_VALUE;

  private IntObjectHashMap<SectionData> sections;
  public byte[] biomes;
  public Collection<CompoundTag> tileEntities;
  public Collection<CompoundTag> entities;

  public GenericChunkData() {
    sections = new IntObjectHashMap<>();
    biomes = new byte[X_MAX * Z_MAX];
    tileEntities = new ArrayList<>();
    entities = new ArrayList<>();
  }

  @Override
  public int minY() {
    return minSectionY << 4;
  }

  @Override
  public int maxY() {
    return (maxSectionY << 4) + 15;
  }

  @Override
  public int getBlockAt(int x, int y, int z) {
    SectionData sectionData = sections.get(y >> 4);
    if (sectionData == null)
      return 0;
    return sectionData.blocks[chunkIndex(x & (X_MAX - 1), y & (SECTION_Y_MAX - 1), z & (Z_MAX - 1))];
  }

  @Override
  public void setBlockAt(int x, int y, int z, int block) {
    int sectionY = y >> 4;
    if(minSectionY > sectionY)
      minSectionY = sectionY;
    if(maxSectionY < sectionY)
      maxSectionY = sectionY;
    SectionData sectionData = sections.getIfAbsentPut(sectionY, new SectionData(sectionY));
    sectionData.blocks[chunkIndex(x & (X_MAX - 1), y & (SECTION_Y_MAX - 1), z & (Z_MAX - 1))] = block;
  }

  @Override
  public boolean isBlockOnEdge(int x, int y, int z) {
    return y <= minSectionY << 4 || y >= ((maxSectionY << 4) | 0xF)
      || x <= 0 || x >= 15
      || z <= 0 || z >= 15;
  }

  @Override
  public Collection<CompoundTag> getTileEntities() {
    return tileEntities;
  }

  @Override
  public Collection<CompoundTag> getEntities() {
    return entities;
  }

  @Override
  public byte getBiomeAt(int x, int y, int z) {
    return biomes[chunkXZIndex(x, z)];
  }

  @Override
  public void setBiomeAt(int x, int y, int z, byte biome) {
    biomes[chunkXZIndex(x, z)] = biome;
  }

  @Override
  public void clear() {
    minSectionY = Integer.MAX_VALUE;
    maxSectionY = Integer.MIN_VALUE;
    sections.clear();
    biomes = new byte[X_MAX * Chunk.Z_MAX];
    tileEntities.clear();
    entities.clear();
  }

  private static class SectionData {
    public final int sectionY;
    public final int[] blocks;

    public SectionData(int sectionY) {
      this(sectionY, new int[X_MAX * SECTION_Y_MAX * Z_MAX]);
    }

    public SectionData(int sectionY, int[] blocks) {
      this.sectionY = sectionY;
      this.blocks = blocks;
    }
  }
}