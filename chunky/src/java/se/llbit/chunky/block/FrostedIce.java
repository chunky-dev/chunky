package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class FrostedIce extends MinecraftBlockTranslucent {
  private static final Texture[] texture = {
      Texture.frostedIce0, Texture.frostedIce1, Texture.frostedIce2, Texture.frostedIce3
  };

  private final int age;

  public FrostedIce(int age) {
    super("frosted_ice", texture[age & 3]);
    this.age = age & 3;
  }

  @Override public String description() {
    return "age=" + age;
  }
}
