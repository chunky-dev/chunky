package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.NetherPortalModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class NetherPortal extends MinecraftBlockTranslucent implements ModelBlock {
  private final NetherPortalModel model;
  private final String description;

  public NetherPortal(String axis) {
    super("nether_portal", Texture.portal);
    localIntersect = true;
    this.description = "axis=" + axis;
    this.model = new NetherPortalModel(axis);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
