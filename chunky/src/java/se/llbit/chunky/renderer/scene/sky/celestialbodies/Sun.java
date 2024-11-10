package se.llbit.chunky.renderer.scene.sky.celestialbodies;

import se.llbit.chunky.resources.Texture;

public class Sun extends CelestialBodyType {
  public static final String ID = "SUN";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "Sun";
  }

  public static final Texture texture = new Texture();

  @Override
  public Texture getTexture() {
    return texture;
  }
}
