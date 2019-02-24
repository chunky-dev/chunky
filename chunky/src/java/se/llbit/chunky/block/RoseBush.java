package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class RoseBush extends SpriteBlock {
  public RoseBush(String half) {
    super("rose_bush",
        half.equals("upper")
            ? Texture.roseBushTop
            : Texture.roseBushBottom);
  }
}
