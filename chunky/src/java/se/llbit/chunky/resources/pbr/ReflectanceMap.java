package se.llbit.chunky.resources.pbr;

import se.llbit.chunky.resources.BitmapImage;

public interface ReflectanceMap {

  ReflectanceMap DEFAULT = new ReflectanceMap() {
    @Override
    public boolean load(BitmapImage texture) {
      return false;
    }

    @Override
    public double getReflectanceAt(double u, double v) {
      return 1;
    }
  };

  ReflectanceMap EMPTY = new ReflectanceMap() {
    @Override
    public boolean load(BitmapImage texture) {
      return false;
    }

    @Override
    public double getReflectanceAt(double u, double v) {
      return 0;
    }
  };

  /**
   * Load the reflectance map from the given texture. If this returns false, there is no specular
   * map available.
   *
   * @param texture Texture in ARGB format
   * @return true if the texture contains a specular map, false otherwise
   */
  boolean load(BitmapImage texture);

  /**
   * Get the reflectance at the given texture coordinate.
   *
   * @param u u component of the texture coordinate [0...1]
   * @param v v component of the texture coordinate [0...1]
   * @return Reflectance [0...1] at the given texture coordinate
   */
  double getReflectanceAt(double u, double v);
}
