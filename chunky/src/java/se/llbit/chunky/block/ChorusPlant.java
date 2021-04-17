package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.ChorusPlantModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class ChorusPlant extends MinecraftBlockTranslucent implements ModelBlock {
  private final ChorusPlantModel model;
  private final String description;

  public ChorusPlant(
      boolean north, boolean south, boolean east, boolean west,
      boolean up, boolean down) {
    super("chorus_plant", Texture.chorusPlant);
    localIntersect = true;
    this.description = String.format("north=%s, south=%s, east=%s, west=%s",
        north, south, east, west);
    model = new ChorusPlantModel(north, south, east, west, up, down);
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
