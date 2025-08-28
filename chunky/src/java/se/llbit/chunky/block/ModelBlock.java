package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.plugin.PluginApi;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray2;

@PluginApi
public interface ModelBlock {

  @PluginApi
  BlockModel getModel();

  default boolean intersect(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    return getModel().intersect(ray, intersectionRecord, scene);
  }
}
