package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.AttachedStemModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

/**
 * Attached melon or pumpkin stem.
 */
public class AttachedStem extends MinecraftBlockTranslucent implements ModelBlock {
  private final AttachedStemModel model;
  private final String description;

  public AttachedStem(String name, String facingString) {
    super(name, Texture.stemBent);
    description = "facing=" + facingString;
    localIntersect = true;
    int facing;
    switch (facingString) {
      default:
      case "north":
        facing = 2; //0;
        break;
      case "south":
        facing = 3; //1;
        break;
      case "east":
        facing = 1; //2;
        break;
      case "west":
        facing = 0; //3;
        break;
    }
    this.model = new AttachedStemModel(facing);
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
