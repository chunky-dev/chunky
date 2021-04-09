package se.llbit.chunky.block;

import se.llbit.chunky.model.AnvilModel;
import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Anvil extends MinecraftBlockTranslucent implements ModelBlock {
  private final int facing;
  private final int damage;
  private final AnvilModel model;

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
    this.model = new AnvilModel(this.facing, this.damage);
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return model.intersect(ray, scene);
  }

  @Override
  public String description() {
    return "damage=" + damage;
  }

  @Override
  public BlockModel getModel() {
    return model;
  }
}
