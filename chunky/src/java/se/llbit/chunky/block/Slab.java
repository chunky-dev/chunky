package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.SlabModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Slab extends MinecraftBlockTranslucent implements ModelBlock {
  private final SlabModel model;
  private final String description;

  public Slab(String name, Texture sideTexture, Texture topTexture, String type) {
    super(name, sideTexture);
    this.description = String.format("type=%s", type);
    this.model = new SlabModel(sideTexture, topTexture, type);
    localIntersect = true;
    solid = false;
  }

  public Slab(String name, Texture texture, String type) {
    this(name, texture, texture, type);
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
