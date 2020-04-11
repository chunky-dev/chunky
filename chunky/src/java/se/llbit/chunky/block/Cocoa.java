package se.llbit.chunky.block;

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
public class Cocoa extends MinecraftBlockTranslucent {
  private final int age, facing;
  private final String description;

  public Cocoa(String facing, int age) {
    super("cocoa", Texture.cocoaPlantLarge);
    description = String.format("facing=%s, age=%d", facing, age);
    localIntersect = true;
    this.age = age % 3;
    switch (facing) {
      default:
      case "north":
        this.facing = 2;
        break;
      case "south":
        this.facing = 0;
        break;
      case "west":
        this.facing = 1;
        break;
      case "east":
        this.facing = 3;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return CocoaPlantModel.intersect(ray, facing, age);
  }

  @Override public String description() {
    return description;
  }
}
