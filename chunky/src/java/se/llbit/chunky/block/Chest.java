package se.llbit.chunky.block;

import se.llbit.chunky.model.ChestModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class Chest extends MinecraftBlock {
  private static final Texture[][] texture = {
    // Single.
    {
      Texture.chestFront, Texture.chestBack, Texture.chestLeft, Texture.chestRight,
      Texture.chestTop, Texture.chestBottom, Texture.chestLock, Texture.chestLock,
      Texture.chestLock, Texture.chestLock, Texture.chestLock,
    },

    // Left.
    {
      Texture.largeChestFrontLeft, Texture.largeChestBackLeft, Texture.largeChestLeft,
      Texture.largeChestTopLeft, Texture.largeChestBottomLeft, Texture.chestLock,
      Texture.chestLock, Texture.chestLock, Texture.chestLock,
    },

    // Right.
    {
      Texture.largeChestFrontRight, Texture.largeChestBackRight, Texture.largeChestRight,
      Texture.largeChestTopRight, Texture.largeChestBottomRight, Texture.chestLock,
      Texture.chestLock, Texture.chestLock, Texture.chestLock,
    }
  };

  private static final Texture[][] textureTrapped = {
    // Single.
    {
      Texture.trappedChestFront, Texture.trappedChestBack, Texture.trappedChestLeft, Texture.trappedChestRight,
      Texture.trappedChestTop, Texture.trappedChestBottom, Texture.trappedChestLock, Texture.trappedChestLock,
      Texture.trappedChestLock, Texture.trappedChestLock, Texture.trappedChestLock,
    },

    // Left.
    {
      Texture.largeTrappedChestFrontLeft, Texture.largeTrappedChestBackLeft, Texture.largeTrappedChestLeft,
      Texture.largeTrappedChestTopLeft, Texture.largeTrappedChestBottomLeft, Texture.trappedChestLock,
      Texture.trappedChestLock, Texture.trappedChestLock, Texture.trappedChestLock,
    },

    // Right.
    {
      Texture.largeTrappedChestFrontRight, Texture.largeTrappedChestBackRight, Texture.largeTrappedChestRight,
      Texture.largeTrappedChestTopRight, Texture.largeTrappedChestBottomRight, Texture.trappedChestLock,
      Texture.trappedChestLock, Texture.trappedChestLock, Texture.trappedChestLock,
    }
  };

  private final int type, facing;
  private final boolean trapped;
  private final String description;

  public Chest(String name, String type, String facing, boolean trapped) {
    super(name, trapped ? Texture.trappedChestFront : Texture.chestFront);
    this.trapped = trapped;
    this.description = String.format("type=%s, facing=%s", type, facing);
    localIntersect = true;
    opaque = false;
    switch (type) {
      default:
      case "single":
        this.type = 0;
        break;
      case "left":
        this.type = 1;
        break;
      case "right":
        this.type = 2;
        break;
    }
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

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return ChestModel.intersect(ray, trapped ? textureTrapped[type] : texture[type], type, facing);
  }

  @Override
  public String description() {
    return description;
  }
}
