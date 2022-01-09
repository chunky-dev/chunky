package se.llbit.chunky.block;

import se.llbit.chunky.model.SlabModel;
import se.llbit.chunky.resources.Texture;

public class Slab extends AbstractModelBlock {

  private final String description;

  public Slab(String name, Texture sideTexture, Texture topTexture, String type) {
    super(name, sideTexture);
    this.description = String.format("type=%s", type);
    this.model = new SlabModel(sideTexture, topTexture, type);
    solid = type.equals("double");
  }

  public Slab(String name, Texture texture, String type) {
    this(name, texture, texture, type);
  }

  @Override
  public String description() {
    return description;
  }
}
