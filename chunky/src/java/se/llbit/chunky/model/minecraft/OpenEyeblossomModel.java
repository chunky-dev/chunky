package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.block.minecraft.OpenEyeblossom;
import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;

import java.util.Arrays;

public class OpenEyeblossomModel extends QuadModel {
  private static final Quad[] quads;
  private static final Texture[] textures;

  static {
    SpriteModel base = new SpriteModel(Texture.openEyeblossom, "up");
    SpriteModel emissive = new SpriteModel(Texture.openEyeblossomEmissive, "up");

    quads = Model.join(base.getQuads(), emissive.getQuads());
    textures = Arrays.copyOf(base.getTextures(), base.getTextures().length + emissive.getTextures().length);
    System.arraycopy(emissive.getTextures(), 0, textures, base.getTextures().length, emissive.getTextures().length);
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;

    Quad[] quads = getQuads();
    Texture[] textures = getTextures();
    Tint[] tintedQuads = getTints();

    float[] color = null;
    Tint tint = Tint.NONE;
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        float[] c = textures[i].getColor(ray.u, ray.v);
        if (c[3] > Ray.EPSILON) {
          tint = tintedQuads == null ? Tint.NONE : tintedQuads[i];
          color = c;
          ray.t = ray.tNext;
          if (quad.doubleSided)
            ray.orientNormal(quad.n);
          else
            ray.setNormal(quad.n);
          hit = true;
          if (textures[i] == Texture.openEyeblossomEmissive) {
            ray.setCurrentMaterial(OpenEyeblossom.emissiveMaterial);
          }
        }
      }
    }

    if (hit) {
      ray.color.set(color);
      tint.tint(ray.color, ray, scene);
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }
}
