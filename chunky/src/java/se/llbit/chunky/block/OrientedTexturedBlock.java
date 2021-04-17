package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.OrientedTexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

/**
 * A textured block that can have one of six orientations, e.g. barrels.
 */
public class OrientedTexturedBlock extends MinecraftBlock implements ModelBlock {
  private final OrientedTexturedBlockModel model;

  public OrientedTexturedBlock(String name, String facing, Texture side, Texture top,
      Texture bottom) {
    this(name, facing, side, side, side, side, top, bottom);
  }

  public OrientedTexturedBlock(String name, String facing, Texture north, Texture south,
      Texture east, Texture west, Texture top, Texture bottom) {
    super(name, north);
    this.localIntersect = true;
    this.model = new OrientedTexturedBlockModel(facing, north, east, south, west, top, bottom);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
