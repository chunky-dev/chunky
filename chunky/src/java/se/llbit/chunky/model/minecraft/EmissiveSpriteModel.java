package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.math.Quad;
import se.llbit.math.Ray;

public class EmissiveSpriteModel extends QuadModel {
  private static final Quad[] quads;
  private final Texture[] textures;

  static {
    SpriteModel sprite = new SpriteModel(Texture.unknown, "up");
    quads = Model.join(sprite.getQuads(), sprite.getQuads());
  }

  private final TextureMaterial emissiveMaterial;

  public EmissiveSpriteModel(Texture texture, TextureMaterial emissiveMaterial) {
    this.emissiveMaterial = emissiveMaterial;
    textures = new Texture[quads.length];
    for (int i = 0; i < quads.length / 2; i++) {
      textures[i] = emissiveMaterial.texture;
      textures[i + quads.length / 2] = texture;
    }
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
          if (i < quads.length / 2) {
            ray.setCurrentMaterial(emissiveMaterial);
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
