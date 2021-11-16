package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.resources.Texture;

public class Farmland extends AbstractModelBlock {

  private final int moisture;

  public Farmland(int moisture) {
    super("farmland", Texture.farmlandWet);
    this.model = new TexturedBlockModel(Texture.dirt, Texture.dirt, Texture.dirt, Texture.dirt,
    moisture >= 7 ? Texture.farmlandWet : Texture.farmlandDry, Texture.dirt);
    this.moisture = moisture;
    opaque = true; 
    // TODO farmland shouldn't be a full block
  }

  @Override
  public String description() {
    return "moisture=" + moisture;
  }
}
