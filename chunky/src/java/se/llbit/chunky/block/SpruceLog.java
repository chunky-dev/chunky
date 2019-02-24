package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class SpruceLog extends Log {
  public SpruceLog(String axis) {
    super("spruce_log",
        new Texture[] { Texture.spruceWood, Texture.spruceWoodTop },
        axis);
  }
}
