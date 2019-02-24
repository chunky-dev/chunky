package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Peony extends SpriteBlock {
  public Peony(String half) {
    super("peony",
        half.equals("upper")
            ? Texture.peonyTop
            : Texture.peonyBottom);
  }
}
