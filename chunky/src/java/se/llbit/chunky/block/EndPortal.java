package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.EndPortalModel;
import se.llbit.chunky.resources.Texture;

public class EndPortal extends AbstractModelBlock {

  public EndPortal() {
    super("end_portal", Texture.endPortal);
    model = new EndPortalModel();
  }
}
