package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StrippedAcaciaLog extends Log {
  public StrippedAcaciaLog(String axis) {
    super("stripped_acacia_log",
        new Texture[] { Texture.strippedAcaciaLog, Texture.strippedAcaciaLogTop },
        axis);
  }
}
