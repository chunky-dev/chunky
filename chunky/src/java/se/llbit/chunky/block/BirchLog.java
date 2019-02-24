package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class BirchLog extends Log {
  public BirchLog(String axis) {
    super("birch_log",
        new Texture[] { Texture.birchWood, Texture.birchWoodTop },
        axis);
  }
}
