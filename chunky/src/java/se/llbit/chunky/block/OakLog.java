package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class OakLog extends Log {
  public OakLog(String axis) {
    super("oak_log",
        new Texture[] { Texture.oakWood, Texture.oakWoodTop },
        axis);
  }
}
