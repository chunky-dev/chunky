package se.llbit.chunky.renderer.projection;

import java.util.Random;

import se.llbit.math.Vector3;

public class ShiftProjector implements Projector {
  private final Projector wrapped;
  private final double shiftX;
  private final double shiftY;

  public ShiftProjector(Projector wrapped, double shiftX, double shiftY) {
    this.wrapped = wrapped;
    this.shiftX = shiftX;
    this.shiftY = shiftY;
  }

  @Override
  public void apply(double x, double y, Random random, Vector3 pos, Vector3 direction) {
    wrapped.apply(x + shiftX, y - shiftY, random, pos, direction);
  }

  @Override
  public void apply(double x, double y, Vector3 pos, Vector3 direction) {
    wrapped.apply(x + shiftX, y - shiftY, pos, direction);
  }

  @Override
  public double getMinRecommendedFoV() {
    return wrapped.getMinRecommendedFoV();
  }

  @Override
  public double getMaxRecommendedFoV() {
    return wrapped.getMaxRecommendedFoV();
  }

  @Override
  public double getDefaultFoV() {
    return wrapped.getDefaultFoV();
  }
}
