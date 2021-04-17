package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.CocoaPlantModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.DoubleSidedQuad;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * Melon or pumpkin stem.
 */
public class Cocoa extends MinecraftBlockTranslucent implements ModelBlock {
  private final String description;
  private final CocoaPlantModel model;

  public Cocoa(String facingString, int age) {
    super("cocoa", Texture.cocoaPlantLarge);
    description = String.format("facing=%s, age=%d", facingString, age);
    localIntersect = true;
    int facing;
    switch (facingString) {
      default:
      case "north":
        facing = 2;
        break;
      case "south":
        facing = 0;
        break;
      case "west":
        facing = 1;
        break;
      case "east":
        facing = 3;
        break;
    }
    model = new CocoaPlantModel(facing, age);
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
