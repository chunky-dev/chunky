package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Lilac extends SpriteBlock {
  public Lilac(String half) {
    super("lilac",
        half.equals("upper")
            ? Texture.lilacTop
            : Texture.lilacBottom);
  }
}
