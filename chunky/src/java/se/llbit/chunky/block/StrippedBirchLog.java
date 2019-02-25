package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StrippedBirchLog extends Log {
  public StrippedBirchLog(String axis) {
    super("stripped_birch_log",
        new Texture[] { Texture.strippedBirchLog, Texture.strippedBirchLogTop },
        axis);
  }
}
