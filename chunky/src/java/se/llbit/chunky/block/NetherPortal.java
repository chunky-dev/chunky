package se.llbit.chunky.block;

import se.llbit.chunky.model.NetherPortalModel;
import se.llbit.chunky.resources.Texture;

public class NetherPortal extends AbstractModelBlock {

  private final String description;

  public NetherPortal(String axis) {
    super("nether_portal", Texture.portal);
    this.description = "axis=" + axis;
    this.model = new NetherPortalModel(axis);
  }

  @Override
  public String description() {
    return description;
  }
}
