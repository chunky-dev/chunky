package se.llbit.chunky.block;

import se.llbit.chunky.model.TexturedBlockModel;
import se.llbit.chunky.renderer.RenderMode;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;

public class LightBlock extends MinecraftBlockTranslucent {

  private static final TexturedBlockModel previewBlockModel = new TexturedBlockModel(
      Texture.light, Texture.light, Texture.light,
      Texture.light, Texture.light, Texture.light
  );

  private final int level;

  public LightBlock(String name, int level) {
    super(name, Texture.EMPTY_TEXTURE);
    this.level = level;
    localIntersect = true;
    solid = false;
  }

  public int getLevel() {
    return level;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    if (scene.getMode() == RenderMode.PREVIEW) {
      return previewBlockModel.intersect(ray, scene);
    }
    if (scene.getMode() != RenderMode.PREVIEW &&
        (!scene.getEmittersEnabled() || emittance < Ray.EPSILON
            || ray.depth >= scene.getRayDepth() - 1 || ray.specular)) {
      return false;
    }
    ray.color.set(1, 1, 1, 1);
    return true;
  }

  @Override
  public String description() {
    return "level=" + level;
  }
}
