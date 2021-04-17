package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.ButtonModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Button extends MinecraftBlockTranslucent implements ModelBlock {
  private final String description;
  private final ButtonModel model;
  // TODO(llbit): render powered buttons

  public Button(String name, Texture texture, String face, String facing, boolean powered) {
    super(name, texture);
    this.description = String.format("face=%s, facing=%s, powered=%s",
        face, facing, powered);
    this.model = new ButtonModel(face, facing, texture);
    localIntersect = true;
    // TODO handle rotation on top/bottom positions!
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
