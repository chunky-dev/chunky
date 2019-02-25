package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StrippedOakLog extends Log {
  public StrippedOakLog(String axis) {
    super("stripped_oak_log",
        new Texture[] { Texture.strippedOakLog, Texture.strippedOakLogTop },
        axis);
  }
}
