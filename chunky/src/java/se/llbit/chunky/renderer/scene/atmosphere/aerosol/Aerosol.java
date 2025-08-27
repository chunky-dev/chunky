package se.llbit.chunky.renderer.scene.atmosphere;

public abstract class Aerosol {
  private double turbidity;
  private double baseDensity;
  private double backgroundDividedByBaseIntensity;
  private double heightScale;

  public Aerosol(double turbidity, double baseDensity, double backgroundDensity, double heightScale) {
    this.turbidity = turbidity;
    this.baseDensity = baseDensity;
    this.backgroundDividedByBaseIntensity = backgroundDensity / baseDensity;
    this.heightScale = heightScale;
  }

  public double getAbsorption(double height, double wl) {
    return
  }

  public double getScattering(double height, double wl) {
    return
  }

  public double getExtinction(double height, double wl) {
    return
  }

  protected double getAbsorptionCrossSection(double wl) {
    
  }
}
