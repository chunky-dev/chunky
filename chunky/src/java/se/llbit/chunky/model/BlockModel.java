package se.llbit.chunky.model;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Ray;

public interface BlockModel {

  boolean intersect(Ray ray, Scene scene);
}
