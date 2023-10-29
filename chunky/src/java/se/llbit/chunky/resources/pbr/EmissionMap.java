package se.llbit.chunky.resources.pbr;

public interface EmissionMap {

  EmissionMap EMPTY = new EmissionMap() {
    @Override
    public float getEmittanceAt(double u, double v) {
      return 0;
    }
  };

  EmissionMap DEFAULT = new EmissionMap() {
    @Override
    public float getEmittanceAt(double u, double v) {
      return 1;
    }
  };

  /**
   * Get the emittance at the given texture coordinate.
   *
   * @param u u component of the texture coordinate [0...1]
   * @param v v component of the texture coordinate [0...1]
   * @return Emittance [0...1] at the given texture coordinate
   */
  float getEmittanceAt(double u, double v);
}
