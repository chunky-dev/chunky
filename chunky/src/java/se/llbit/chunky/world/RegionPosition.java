package se.llbit.chunky.world;

import java.util.Objects;

import static se.llbit.chunky.world.ChunkPosition.*;

public class RegionPosition {
  public final int x;
  public final int z;

  public RegionPosition(int x, int z) {
    this.x = x;
    this.z = z;
  }

  public RegionPosition(long position) {
    this(longPositionX(position), longPositionZ(position));
  }

  /**
   * @return This region position packed into a long.
   */
  public long getLong() {
    return positionToLong(x, z);
  }

  /**
   * @param localChunkX Chunk X coordinate relative to this region position
   * @param localChunkZ Chunk Z coordinate relative to this region position
   * @return Returns the global chunk position of the given region-local chunk coordinates
   */
  public ChunkPosition asChunkPosition(int localChunkX, int localChunkZ) {
    return new ChunkPosition(
      this.x << 5 | (localChunkX & 0x1f),
      this.z << 5 | (localChunkZ & 0x1f)
    );
  }

  /**
   * @return The .mca name for the region with this position
   */
  public String getMcaName() {
    return String.format("r.%d.%d.mca", x, z);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RegionPosition that = (RegionPosition) o;
    return x == that.x && z == that.z;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, z);
  }
}
