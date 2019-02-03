package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class AcaciaPlanks extends MinecraftBlock {
  public static final AcaciaPlanks INSTANCE = new AcaciaPlanks();

  private AcaciaPlanks() {
    super("acacia_planks", Texture.acaciaPlanks);
  }
}
