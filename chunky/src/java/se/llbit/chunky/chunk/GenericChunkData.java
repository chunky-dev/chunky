package se.llbit.chunky.chunk;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import se.llbit.chunky.chunk.biome.BiomeData;
import se.llbit.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static se.llbit.chunky.world.Chunk.*;

/**
 * A general implementation of Chunk Data
 * Supports blocks at any Y range
 */
public class GenericChunkData implements ChunkData {
  private Integer minSectionY = Integer.MAX_VALUE;
  private Integer maxSectionY = Integer.MIN_VALUE;

  private final Int2ObjectOpenHashMap<SectionData> sections;
  private BiomeData biomeData;
  private final Collection<CompoundTag> tileEntities;
  private final Collection<CompoundTag> entities;

  public GenericChunkData() {
    sections = new Int2ObjectOpenHashMap<>();
    tileEntities = new ArrayList<>();
    entities = new ArrayList<>();
  }

  @Override public int minY() {
    return minSectionY << 4;
  }

  @Override public int maxY() {
    return ((maxSectionY+1) << 4);
  }

  @Override public int getBlockAt(int x, int y, int z) {
    SectionData sectionData = sections.get(y >> 4);
    if (sectionData == null)
      return 0;
    return sectionData.blocks[chunkIndex(x & (X_MAX - 1), y & (SECTION_Y_MAX - 1), z & (Z_MAX - 1))];
  }

  @Override public void setBlockAt(int x, int y, int z, int block) {
    int sectionY = y >> 4;
    if(minSectionY > sectionY)
      minSectionY = sectionY;
    if(maxSectionY < sectionY)
      maxSectionY = sectionY;

    if(block == 0)
      return;
    SectionData sectionData = sections.computeIfAbsent(sectionY, SectionData::new);
    sectionData.blocks[chunkIndex(x & (X_MAX - 1), y & (SECTION_Y_MAX - 1), z & (Z_MAX - 1))] = block;
  }

  @Override public boolean isBlockOnEdge(int x, int y, int z) {
    return y <= minSectionY << 4 || y >= ((maxSectionY << 4) | 0xF)
      || x <= 0 || x >= 15
      || z <= 0 || z >= 15;
  }

  @Override public Collection<CompoundTag> getTileEntities() {
    return tileEntities;
  }

  @Override
  public void addTileEntity(CompoundTag tileEntity) {
    tileEntities.add(tileEntity);
  }

  @Override public Collection<CompoundTag> getEntities() {
    return entities;
  }

  @Override
  public void addEntity(CompoundTag entity) {
    entities.add(entity);
  }

  @Override public void clear() {
    minSectionY = Integer.MAX_VALUE;
    maxSectionY = Integer.MIN_VALUE;
    sections.clear();
    biomeData.clear();
    tileEntities.clear();
    entities.clear();
  }

  @Override
  public BiomeData getBiomeData() {
    return biomeData;
  }

  @Override
  public void setBiomeData(BiomeData biomeData) {
    this.biomeData = biomeData;
  }

  @Override
  public boolean isEmpty() {
    return sections.isEmpty() && entities.isEmpty() && tileEntities.isEmpty();
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GenericChunkData that = (GenericChunkData) o;
    return Objects.equals(minSectionY, that.minSectionY) && Objects.equals(maxSectionY, that.maxSectionY) && Objects.equals(sections, that.sections) && Objects.equals(biomeData, that.biomeData) && Objects.equals(tileEntities, that.tileEntities) && Objects.equals(entities, that.entities);
  }

  @Override public int hashCode() {
    int result = Objects.hash(minSectionY, maxSectionY, sections, tileEntities, entities);
    result = 31 * result + biomeData.hashCode();
    return result;
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

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      SectionData that = (SectionData) o;
      return sectionY == that.sectionY && Arrays.equals(blocks, that.blocks);
    }

    @Override public int hashCode() {
      int result = Objects.hash(sectionY);
      result = 31 * result + Arrays.hashCode(blocks);
      return result;
    }
  }
}