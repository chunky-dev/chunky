package se.llbit.math.structures;

public interface Position2IntStructure {
  /**
   * @param x World X.
   * @param y World Y.
   * @param z World Z.
   */
  void set(int x, int y, int z, int data);

  /**
   * @param x World X.
   * @param y World Y.
   * @param z World Z.
   */
  int get(int x, int y, int z);
}
