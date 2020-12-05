package se.llbit.chunky.block;

import se.llbit.chunky.model.SculkSensorModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class SculkSensor extends MinecraftBlockTranslucent {

  private final String phase;

  public SculkSensor(String phase) {
    super("sculk_sensor", Texture.sculkSensorTop);
    this.phase = phase;
    localIntersect = true;
  }

  public boolean isActive() {
    return phase.equals("active");
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return SculkSensorModel.intersect(ray, isActive());
  }

  @Override
  public String description() {
    return "sculk_sensor_phase=" + phase;
  }
}
