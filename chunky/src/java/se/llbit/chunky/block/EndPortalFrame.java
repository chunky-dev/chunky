package se.llbit.chunky.block;

import se.llbit.chunky.model.EndPortalFrameModel;
import se.llbit.chunky.resources.Texture;

public class EndPortalFrame extends AbstractModelBlock {

  private final String description;

  public EndPortalFrame(boolean eye, String facing) {
    super("end_portal_frame", Texture.endPortalFrameSide);
    this.description = "eye=" + eye + ",facing=" + facing;
    this.model = new EndPortalFrameModel(eye, facing);
  }

  @Override
  public String description() {
    return description;
  }
}
