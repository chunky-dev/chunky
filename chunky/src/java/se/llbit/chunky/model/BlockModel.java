package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.*;

import java.util.Random;

@PluginApi
public interface BlockModel {

  boolean intersect(Ray ray, IntersectionRecord intersectionRecord, Scene scene);

  int faceCount();

  void sample(int face, Vector3 loc, Random rand);

  double faceSurfaceArea(int face);

  boolean isInside(Ray ray);
}
