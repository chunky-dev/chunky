package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class JungleLog extends Log {
  public JungleLog(String axis) {
    super("jungle_log",
        new Texture[] { Texture.jungleWood, Texture.jungleTreeTop },
        axis);
  }
}
