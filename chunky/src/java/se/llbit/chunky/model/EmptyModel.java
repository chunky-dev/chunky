package se.llbit.chunky.model;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray2;
import se.llbit.math.Vector3;

import java.util.Random;

public class EmptyModel implements BlockModel {
  public static final EmptyModel INSTANCE = new EmptyModel();

  @Override
  public boolean intersect(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    return false;
  }

  @Override
  public int faceCount() {
    return 0;
  }

  @Override
  public void sample(int face, Vector3 loc, Random rand) {

  }

  @Override
  public double faceSurfaceArea(int face) {
    return 0;
  }

  @Override
  public boolean isInside(Ray2 ray) {
    return false;
  }
}
