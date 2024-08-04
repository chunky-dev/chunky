package se.llbit.chunky.resources.pbr;

public interface RoughnessMap {

  RoughnessMap EMPTY = new RoughnessMap() {
    @Override
    public float getRoughnessAt(double u, double v) {
      return 0;
    }
  };

  RoughnessMap DEFAULT = new RoughnessMap() {
    @Override
    public float getRoughnessAt(double u, double v) {
      return 0;
    }
  };

  /**
   * Get the roughness at the given texture coordinate.
   *
   * @param u u component of the texture coordinate [0...1]
   * @param v v component of the texture coordinate [0...1]
   * @return Roughness [0...1] at the given texture coordinate
   */
  float getRoughnessAt(double u, double v);
}
