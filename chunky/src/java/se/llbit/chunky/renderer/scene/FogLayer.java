package se.llbit.chunky.renderer.scene;

public final class FogLayer {
  public double yMin, y, yWithOrigin, breadth, breadthInv, density;

  public FogLayer(double y, double breadth, double density, Scene scene) {
    this(y, breadth, density, scene.yMin);
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
