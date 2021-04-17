package se.llbit.chunky.block;

import se.llbit.chunky.model.DispenserModel;
import se.llbit.chunky.resources.Texture;

/**
 * The dispenser behaves almost like a TopBottomOrientedTexturedBlock. If it's facing up or down, it
 * has different textures (and thus different texture orientation logic).
 */
public class Dispenser extends AbstractModelBlock {

  private final String description;

  public Dispenser(String facing) {
    this("dispenser", facing, Texture.dispenserFront, Texture.dispenserFrontVertical,
        Texture.furnaceSide, Texture.furnaceTop);
  }

  public Dispenser(String name, String facing, Texture front, Texture frontVertical, Texture side,
      Texture back) {
    super(name, front);
    opaque = true;
    this.model = new DispenserModel(facing, front, frontVertical, side, back);
    this.description = "facing=" + facing;
  }

  @Override
  public String description() {
    return description;
  }
}
