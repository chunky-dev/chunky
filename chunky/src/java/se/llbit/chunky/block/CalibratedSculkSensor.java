package se.llbit.chunky.block;

import se.llbit.chunky.entity.CalibratedSculkSensorAmethyst;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.model.CalibratedSculkSensorModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Vector3;

public class CalibratedSculkSensor extends AbstractModelBlock {
  private final String phase;
  private final String facing;

  public CalibratedSculkSensor(String phase, String facing) {
    super("calibrated_sculk_sensor", Texture.calibratedSculkSensorTop);
    this.phase = phase;
    this.facing = facing;
    this.model = new CalibratedSculkSensorModel(isActive(), facing);
  }

  public boolean isActive() {
    return phase.equals("active") || phase.equals("cooldown");
  }

  @Override
  public String description() {
    return "sculk_sensor_phase=" + phase + ", facing=" + facing;
  }

  @Override
  public boolean isEntity() {
    return true;
  }

  @Override
  public boolean isBlockWithEntity() {
    return true;
  }

  @Override
  public Entity toEntity(Vector3 position) {
    return new CalibratedSculkSensorAmethyst(position, this.facing, isActive(), this);
  }
}
