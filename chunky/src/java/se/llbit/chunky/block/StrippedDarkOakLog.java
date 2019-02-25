package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StrippedDarkOakLog extends Log {
  public StrippedDarkOakLog(String axis) {
    super("stripped_dark_oak_log",
        new Texture[] { Texture.strippedDarkOakLog, Texture.strippedDarkOakLogTop },
        axis);
  }
}
