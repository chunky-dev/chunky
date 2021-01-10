package se.llbit.chunky.resources.pbr;

public interface ReflectanceMap {

  ReflectanceMap DEFAULT = new ReflectanceMap() {
    @Override
    public double getReflectanceAt(double u, double v) {
      return 1;
    }
  };

  ReflectanceMap EMPTY = new ReflectanceMap() {
    @Override
    public double getReflectanceAt(double u, double v) {
      return 0;
    }
  };

  /**
   * Get the reflectance at the given texture coordinate.
   *
   * @param u u component of the texture coordinate [0...1]
   * @param v v component of the texture coordinate [0...1]
   * @return Reflectance [0...1] at the given texture coordinate
   */
  double getReflectanceAt(double u, double v);
}
