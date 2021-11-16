package se.llbit.chunky.block;

import se.llbit.chunky.model.GlassPaneModel;
import se.llbit.chunky.resources.Texture;

public class GlassPane extends AbstractModelBlock {
  private final String description;

  public GlassPane(String name, Texture side, Texture top,
      boolean north, boolean south, boolean east, boolean west) {
    super(name, side);
    localIntersect = true;
    this.description = String.format("north=%s, south=%s, east=%s, west=%s",
        north, south, east, west);
    this.model = new GlassPaneModel(top, side, north, south, east, west);
  }

  @Override
  public String description() {
    return description;
  }
}
