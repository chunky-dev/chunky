package se.llbit.chunky.resources.pbr;

import se.llbit.chunky.resources.BitmapImage;

public interface EmissionMap {

  EmissionMap EMPTY = new EmissionMap() {
    @Override
    public boolean load(BitmapImage texture) {
      return false;
    }

    @Override
    public double getEmittanceAt(double u, double v) {
      return 0;
    }
  };

  /**
   * Load the emission map from the given texture. If this returns false, there is no emission map
   * available.
   *
   * @param texture Texture in ARGB format
   * @return true if the texture contains an emission map, false otherwise
   */
  boolean load(BitmapImage texture);

  /**
   * Get the emittance at the given texture coordinate.
   *
   * @param u u component of the texture coordinate [0...1]
   * @param v v component of the texture coordinate [0...1]
   * @return Emittance [0...1] at the given texture coordinate.
   */
  double getEmittanceAt(double u, double v);
}
