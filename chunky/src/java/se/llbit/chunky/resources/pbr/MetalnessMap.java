package se.llbit.chunky.resources.pbr;

public interface MetalnessMap {

  MetalnessMap EMPTY = new MetalnessMap() {
    @Override
    public float getMetalnessAt(double u, double v) {
      return 0;
    }
  };

  MetalnessMap DEFAULT = new MetalnessMap() {
    @Override
    public float getMetalnessAt(double u, double v) {
      return 1;
    }
  };

  /**
   * Get the metalness at the given texture coordinate.
   *
   * @param u u component of the texture coordinate [0...1]
   * @param v v component of the texture coordinate [0...1]
   * @return Metalness [0...1] at the given texture coordinate
   */
  float getMetalnessAt(double u, double v);
}
