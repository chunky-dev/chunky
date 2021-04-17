package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.JigsawModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class JigsawBlock extends Block implements ModelBlock {
  private final JigsawModel model;
  private final String orientation;

  public JigsawBlock(String name, String orientation) {
    super(name, Texture.jigsawTop);
    this.orientation = orientation;
    this.model = new JigsawModel(orientation);
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return "orientation=" + orientation;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
