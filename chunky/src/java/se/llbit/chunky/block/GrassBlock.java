package se.llbit.chunky.block;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

public class GrassBlock extends MinecraftBlock {
  private final static AABB aabb = new AABB(0, 1, 0, 1, 0, 1);

  public GrassBlock() {
    super("grass_block", Texture.grassTop);
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    ray.t = Double.POSITIVE_INFINITY;
    if (aabb.intersect(ray)) {
      Vector3 n = ray.getN();
      if (n.y == -1) {
        // Bottom face.
        Texture.dirt.getColor(ray);
        ray.t = ray.tNext;
      } else {
        float[] color;
        if (n.y > 0) {
          color = Texture.grassTop.getColor(ray.u, ray.v);
        } else {
          color = Texture.grassSide.getColor(ray.u, ray.v);
        }
        if (color[3] > Ray.EPSILON) {
          ray.color.set(color);
          float[] biomeColor = ray.getBiomeGrassColor(scene);
          ray.color.x *= biomeColor[0];
          ray.color.y *= biomeColor[1];
          ray.color.z *= biomeColor[2];
          ray.t = ray.tNext;
        } else {
          Texture.grassSideSaturated.getColor(ray);
          ray.color.w = 1;
          ray.t = ray.tNext;
        }
      }
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
      return true;
    }
    return false;
  }
}
