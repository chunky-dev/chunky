package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.List;
import java.util.Random;

@PluginApi
public interface BlockModel {

  boolean intersect(Ray ray, Scene scene);

  int faceCount();

  void sample(int face, Vector3 loc, Random rand);

  double faceSurfaceArea(int face);
}
