package se.llbit.chunky.renderer.scene.fog;

import se.llbit.chunky.renderer.scene.Scene;

public final class FogLayer {
  public static final double DEFAULT_Y = 62;
  public static final double DEFAULT_BREADTH = 5;
  public static final double DEFAULT_DENSITY = 1;
  public double yMin, y, yWithOrigin, breadth, breadthInv, density;

  public FogLayer(double y, double breadth, double density, Scene scene) {
    this(y, breadth, density, scene.getYMin());
  }

  public FogLayer(Scene scene) {
    this(DEFAULT_Y, DEFAULT_BREADTH, DEFAULT_DENSITY, scene.getYMin());
  }

  private FogLayer(double y, double breadth, double density, double yMin) {
    this.yMin = yMin;
    setY(y);
    setBreadth(breadth);
    setDensity(density);
  }

  public void setY(double y) {
    this.y = y;
    this.yWithOrigin = y - yMin;
  }

  public void setBreadth(double breadth) {
    this.breadth = breadth;
    this.breadthInv = 1 / breadth;
  }

  public void setDensity(double density) {
    this.density = density;
  }

  public FogLayer clone() {
    return new FogLayer(y, breadth, density, yMin);
  }
}
