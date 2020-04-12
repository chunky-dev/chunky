package se.llbit.chunky.block;

import se.llbit.chunky.model.DirectionalBlockModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class RepeatingCommandBlock extends MinecraftBlock {
  private static final Texture[][] texture = {
      {
          Texture.repeatingCommandBlockBack,
          Texture.repeatingCommandBlockFront,
          Texture.repeatingCommandBlockSide
      },
      {
          Texture.repeatingCommandBlockBack,
          Texture.repeatingCommandBlockFront,
          Texture.repeatingCommandBlockConditional
      },
  };

  private final int facing;
  private final String description;
  private final Texture[] textures;

  public RepeatingCommandBlock(String facing, boolean conditional) {
    super("repeating_command_block", Texture.commandBlockFront);
    this.description = String.format("facing=%s, conditional=%s", facing, conditional);
    textures = conditional ? texture[1] : texture[0];
    switch (facing) {
      case "up":
        this.facing = 1;
        break;
      case "down":
        this.facing = 0;
        break;
      default:
      case "north":
        this.facing = 2;
        break;
      case "east":
        this.facing = 5;
        break;
      case "south":
        this.facing = 3;
        break;
      case "west":
        this.facing = 4;
        break;
    }
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return DirectionalBlockModel.intersect(ray, textures, facing);
  }

  @Override public String description() {
    return description;
  }
}
