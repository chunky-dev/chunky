package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class StrippedJungleLog extends Log {
  public StrippedJungleLog(String axis) {
    super("stripped_jungle_log",
        new Texture[] { Texture.strippedJungleLog, Texture.strippedJungleLogTop },
        axis);
  }
}
