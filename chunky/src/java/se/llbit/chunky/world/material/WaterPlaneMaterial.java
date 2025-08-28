package se.llbit.chunky.world.material;

import se.llbit.chunky.block.Block;
import se.llbit.chunky.resources.SolidColorTexture;

/**
 * The material used for the water plane.
 */
public class WaterPlaneMaterial extends Block {

  public static final WaterPlaneMaterial INSTANCE = new WaterPlaneMaterial();

  private WaterPlaneMaterial() {
    super("water_plane", SolidColorTexture.EMPTY);
  }

  @Override
  public void restoreDefaults() {
    ior = 1.333f;
    alpha = 0f;
  }
}
