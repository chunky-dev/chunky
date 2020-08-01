package se.llbit.math;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid {
  private static final int GRID_FORMAT_VERSION = 0;

  /**
   * Holds a 3D grid of blocks cube
   * Each cell of the grid holds the position of the emitters present in this cell and in neighboring cells
   * As such when we want to sample emitters close from an intersection point, we only have to look at
   * the cell where this intersection falls in and we will find every emitters we are interested in.
   * The reason we need to hold emitter of neighboring cells is because emitters a fex block away from the intersection point
   * to have an effect even if it falls in a different cell.
   * With this every emitters away for cellSize or less blocks from the intersection points will always be found.
   * The maximum distance where an emitter can be found in some cases is 2*cellSize-1 blocks away.
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

  // Constructor used by the load method
  private Grid(int gridSize, int cellSize, int overloadSelectFlag /*unused*/) {
    this.gridSize = gridSize;
    this.cellSize = cellSize;
  }

  public void addEmitter(int x, int y, int z) {
    emitterPositions.add(new EmitterPosition(x, y, z));
  }

  private int cellIndex(int x, int y, int z) {
    return ((y * gridSize) + x) * gridSize + z;
  }

  /**
   * Needs to be call when every emitter has been added
   * Builds the grid itself
   */
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
            int x = gridX+dx;
            int y = gridY+dy;
            int z = gridZ+dz;
            if(x >= 0 && x < gridSize && y>= 0 && y < gridSize && z >= 0 && z < gridSize) {
              int index = cellIndex(x, y, z);
              grid[index].indexes.add(i);
            }
          }
        }
      }
    }
  }

  /**
   * Returns the position of an emitter not far from the point given in world coordinates
   * or null if no such emitter exist
   */
  public EmitterPosition sampleEmitterPosition(int x, int y, int z, Random random) {
    int gridX = x / cellSize;
    int gridY = y / cellSize;
    int gridZ = z / cellSize;
    int index = cellIndex(gridX, gridY, gridZ);
    Cell cell = grid[index];
    if(cell.indexes.size() == 0)
      return null;
    int randomIndex = random.nextInt(cell.indexes.size());
    int emitterIndex = cell.indexes.get(randomIndex);
    return emitterPositions.get(emitterIndex);
  }

  /**
   * Get the list of emitters position close from a given point
   */
  public List<EmitterPosition> getEmitterPositions(int x, int y, int z) {
    int gridX = x / cellSize;
    int gridY = y / cellSize;
    int gridZ = z / cellSize;
    int index = cellIndex(gridX, gridY, gridZ);
    Cell cell = grid[index];
    List<EmitterPosition> pos = new ArrayList<>();
    for(Integer i : cell.indexes) {
      pos.add(emitterPositions.get(i));
    }
    return pos;
  }

  /**
   * Stores the grid in the given stream
   * @param out The output stream
   */
  public void store(DataOutputStream out) throws IOException {
    out.writeInt(GRID_FORMAT_VERSION);

    out.writeInt(gridSize);
    out.writeInt(cellSize);

    // Write every emitter position
    out.writeInt(emitterPositions.size());
    for(EmitterPosition pos : emitterPositions) {
      out.writeInt(pos.x);
      out.writeInt(pos.y);
      out.writeInt(pos.z);
    }

    // Write, for each cell, how many emitters are contained and their indexes in the array written earlier
    for(Cell cell : grid) {
      out.writeInt(cell.indexes.size());
      for(int index : cell.indexes) {
        out.writeInt(index);
      }
    }
  }

  /**
   * Load the grid from the given input stream
   * @param in The input stream to read the grid from
   * @return The grid
   */
  public static Grid load(DataInputStream in) throws IOException {
    int version = in.readInt();
    if(version != GRID_FORMAT_VERSION) {
      throw new RuntimeException("Unknown grid format version, can't load the grid");
    }

    int gridSize = in.readInt();
    int cellSize = in.readInt();
    Grid grid = new Grid(gridSize, cellSize, 0);

    // Read emitter positions
    int emitterNo = in.readInt();
    grid.emitterPositions = new ArrayList<>(emitterNo);
    for(int i = 0; i < emitterNo; ++i) {
      int x = in.readInt();
      int y = in.readInt();
      int z = in.readInt();
      grid.emitterPositions.add(new EmitterPosition(x, y, z));
    }

    grid.grid = new Cell[gridSize*gridSize*gridSize];
    for(int cellIndex = 0; cellIndex < grid.grid.length; ++cellIndex) {
      Cell cell = new Cell();
      int posNo = in.readInt();
      for(int posIndex = 0; posIndex < posNo; ++posIndex) {
        int index = in.readInt();
        cell.indexes.add(index);
      }
      grid.grid[cellIndex] = cell;
    }

    return grid;
  }
}
