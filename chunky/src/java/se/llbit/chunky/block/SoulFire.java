package se.llbit.chunky.block;

import se.llbit.chunky.model.FireModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.AnimatedTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class SoulFire extends MinecraftBlock {
  private static final AnimatedTexture[] texture = {Texture.soulFireLayer0, Texture.soulFireLayer1};

  public SoulFire() {
    super("soul_fire", Texture.soulFire);
    localIntersect = true;
    opaque = false;
    solid = false;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return FireModel.intersect(ray, texture, scene.getAnimationTime());
  }
}
