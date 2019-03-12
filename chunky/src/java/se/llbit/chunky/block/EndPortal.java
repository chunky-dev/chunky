package se.llbit.chunky.block;

import se.llbit.chunky.model.EndPortalModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class EndPortal extends MinecraftBlockTranslucent {
  public EndPortal() {
    super("end_portal", Texture.endPortal);
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return EndPortalModel.intersect(ray);
  }
}
