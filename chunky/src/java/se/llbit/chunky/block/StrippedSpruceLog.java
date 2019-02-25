package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StrippedSpruceLog extends Log {
  public StrippedSpruceLog(String axis) {
    super("stripped_spruce_log",
        new Texture[] { Texture.strippedSpruceLog, Texture.strippedSpruceLogTop },
        axis);
  }
}
