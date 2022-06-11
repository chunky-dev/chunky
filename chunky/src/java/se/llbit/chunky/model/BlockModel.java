package se.llbit.chunky.model;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Ray;

@PluginApi
public interface BlockModel {

  boolean intersect(Ray ray, Scene scene);
}