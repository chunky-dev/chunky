package se.llbit.chunky.block;

import se.llbit.chunky.model.ChestModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class EnderChest extends MinecraftBlock {
  private static final Texture[] texture = {
      Texture.enderChestFront, Texture.enderChestBack, Texture.enderChestLeft,
      Texture.enderChestRight, Texture.enderChestTop, Texture.enderChestBottom,
      Texture.enderChestLock, Texture.enderChestLock, Texture.enderChestLock,
      Texture.enderChestLock, Texture.enderChestLock,
  };

  private final int facing;
  private final String description;

  public EnderChest(String facing) {
    super("ender_chest", Texture.chestFront);
    this.description = "facing=" + facing;
    localIntersect = true;
    opaque = false;
    switch (facing) {
      default:
      case "north":
        this.facing = 2;
        break;
      case "south":
        this.facing = 3;
        break;
      case "west":
        this.facing = 4;
        break;
      case "east":
        this.facing = 5;
        break;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return ChestModel.intersect(ray, texture, 0, facing);
  }

  @Override public String description() {
    return description;
  }
}
