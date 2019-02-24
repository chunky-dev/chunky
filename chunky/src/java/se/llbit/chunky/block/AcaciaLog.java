package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class AcaciaLog extends Log {
  public AcaciaLog(String axis) {
    super("acacia_log",
        new Texture[] { Texture.acaciaWood, Texture.acaciaWoodTop },
        axis);
  }
}
