package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;

public class Diorite extends MinecraftBlock {
  public static final Diorite INSTANCE = new Diorite();

  private Diorite() {
    super("diorite", Texture.diorite);
  }
}
