package se.llbit.chunky.block;

import se.llbit.chunky.model.EndPortalFrameModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class EndPortalFrame extends MinecraftBlockTranslucent {
  public final boolean hasEye;

  public EndPortalFrame(boolean eye) {
    super("end_portal_frame", Texture.endPortalFrameSide);
    this.hasEye = eye;
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return EndPortalFrameModel.intersect(ray, hasEye);
  }

  @Override public String description() {
    return "eye=" + hasEye;
  }
}
