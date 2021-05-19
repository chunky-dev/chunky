package se.llbit.chunky.renderer.scene;

import se.llbit.math.Ray;

public interface FogStrategy {

  void fogalize(Scene scene, Ray ray);
}
