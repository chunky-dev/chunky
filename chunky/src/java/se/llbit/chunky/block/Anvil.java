package se.llbit.chunky.block;

import se.llbit.chunky.model.AnvilModel;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Anvil extends MinecraftBlockTranslucent {
  private final int facing, damage;

  public Anvil(String name, String facing, int damage) {
    super(name, Texture.anvilSide);
    localIntersect = true;
    this.damage = damage;
    switch (facing) {
      default:
      case "north":
      case "south":
        this.facing = 0;
        break;
      case "east":
      case "west":
        this.facing = 1;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return AnvilModel.intersect(ray, facing, damage);
  }

  @Override public String description() {
    return "damage=" + damage;
  }
}
