package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class DarkOakLog extends Log {
  public DarkOakLog(String axis) {
    super("dark_oak_log",
        new Texture[] { Texture.darkOakWood, Texture.darkOakWoodTop },
        axis);
  }
}
