package se.llbit.chunky.block;

import se.llbit.chunky.model.BlockModel;
import se.llbit.chunky.model.DispenserModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

/**
 * The dispenser behaves almost like a TopBottomOrientedTexturedBlock. If it's facing up or down, it
 * has different textures (and thus different texture orientation logic).
 */
public class Dispenser extends MinecraftBlock implements ModelBlock {
  private final DispenserModel model;
  private final String description;

  public Dispenser(String facing) {
    this("dispenser", facing, Texture.dispenserFront, Texture.dispenserFrontVertical,
        Texture.furnaceSide, Texture.furnaceTop);
  }

  public Dispenser(String name, String facing, Texture front, Texture frontVertical, Texture side,
      Texture back) {
    super(name, front);
    localIntersect = true;
    this.model = new DispenserModel(facing, front, frontVertical, side, back);
    this.description = "facing=" + facing;
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
