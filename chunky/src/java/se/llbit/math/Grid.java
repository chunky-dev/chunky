package se.llbit.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid {
  /**
   * TODO Add an explanation
   */
  public static class EmitterPosition {
    public EmitterPosition(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public int x, y, z;
  }

  private final int cellSize;
  private List<EmitterPosition> emitterPositions = new ArrayList<>(); // Stored as an object array, may need to be flattened later to improve memory usage

  // Instead of each Cell storing its indexes, we could have a single flat array
  // that is the concatenation of each of those arrays and each cell would have to only
  // store the start and end (or size) of their subarray
  // This would also mean that Cells would only be 2 ints and could be stored directly in a flat array
  // But this would make the implementation more complex so we'll se later
  private static class Cell {
    public List<Integer> indexes = new ArrayList<>();
  }

  private final int gridSize;
  private Cell[] grid;

  public Grid(int octreeDepth, int cellSize) {
    this.cellSize = cellSize;
    long totalSize = (1L << octreeDepth);
    this.gridSize = (int) ((totalSize + (cellSize-1)) / cellSize);
  }

  public void addEmitter(int x, int y, int z) {
    emitterPositions.add(new EmitterPosition(x, y, z));
  }

  private int cellIndex(int x, int y, int z) {
    return ((y * gridSize) + x) * gridSize + z;
  }

  public void prepare() {
    grid = new Cell[gridSize*gridSize*gridSize];
    for(int i = 0; i < grid.length; ++i) {
      grid[i] = new Cell();
    }

    for(int i = 0; i < emitterPositions.size(); ++i) {
      EmitterPosition pos = emitterPositions.get(i);
      int gridX = pos.x / cellSize;
      int gridY = pos.y / cellSize;
      int gridZ = pos.z / cellSize;
      // Add the emitter to its cell and all neighboring cells
      for(int dy = -1; dy <= 1; ++dy) {
        for(int dx = -1; dx <= 1; ++dx) {
          for(int dz = -1; dz <= 1; ++dz) {
            int index = cellIndex(gridX+dx, gridY+dy, gridZ+dz);
            grid[index].indexes.add(i);
          }
        }
      }
    }
  }

  /**
   * Returns the position of an emitter not far from the point given in world coordinates
   * or null if no such emitter exist
   * @param x
   * @param y
   * @param z
   * @param random
   * @return
   */
  EmitterPosition sampleEmitterPosition(int x, int y, int z, Random random) {
    int gridX = x / cellSize;
    int gridY = y / cellSize;
    int gridZ = z / cellSize;
    int index = cellIndex(gridX, gridY, gridZ);
    Cell cell = grid[index];
    int randomIndex = random.nextInt(cell.indexes.size());
    int emitterIndex = cell.indexes.get(randomIndex);
    return emitterPositions.get(emitterIndex);
  }
}
