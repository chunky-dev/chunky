package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

@PluginApi
public interface BlockModel {

  boolean intersect(Ray ray, Scene scene);

  void sample(Vector3 loc, Random rand);
}
