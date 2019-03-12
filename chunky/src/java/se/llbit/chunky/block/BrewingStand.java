package se.llbit.chunky.block;

import se.llbit.chunky.model.BrewingStandModel;
import se.llbit.chunky.model.EndPortalFrameModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class BrewingStand extends MinecraftBlockTranslucent {
  public final int state;
  private final String description;

  public BrewingStand(boolean bottle0, boolean bottle1, boolean bottle2) {
    super("end_portal_frame", Texture.endPortalFrameSide);
    description = String.format("has_bottle_0=%s, has_bottle_1=%s, has_bottle_2=%s",
        bottle0, bottle1, bottle2);
    localIntersect = true;
    state =
        (bottle0 ? 1 : 0)
        | (bottle1 ? 4 : 0)
        | (bottle2 ? 2 : 0);
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return BrewingStandModel.intersect(ray, state);
  }

  @Override public String description() {
    return description;
  }
}
