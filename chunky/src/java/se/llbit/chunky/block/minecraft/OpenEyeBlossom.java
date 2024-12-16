package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.MinecraftBlockTranslucent;
import se.llbit.chunky.model.minecraft.SpriteModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.math.Ray;

public class OpenEyeBlossom extends MinecraftBlockTranslucent {
  public static final Material emissiveMaterial = new TextureMaterial(Texture.openEyeblossomEmissive);
  private static final SpriteModel base = new SpriteModel(Texture.openEyeblossom, "up");
  private static final SpriteModel emissive = new SpriteModel(Texture.openEyeblossomEmissive, "up");

  public OpenEyeBlossom() {
    super("open_eyeblossom", Texture.openEyeblossom);
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    if (emissive.intersect(ray, scene)) {
      ray.setCurrentMaterial(emissiveMaterial);
      return true;
    }
    return base.intersect(ray, scene);
  }
}
