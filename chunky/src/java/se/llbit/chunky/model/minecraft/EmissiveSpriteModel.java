package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.math.Constants;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

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
  public boolean intersect(Ray ray, IntersectionRecord intersectionRecord, Scene scene) {
    boolean hit = false;

    Quad[] quads = getQuads();
    Texture[] textures = getTextures();
    Tint[] tintedQuads = getTints();

    float[] color = null;
    Tint tint = Tint.NONE;
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      double distance = intersectionRecord.distance;
      if (quad.closestIntersection(ray, intersectionRecord)) {
        float[] c = textures[i].getColor(intersectionRecord.uv.x, intersectionRecord.uv.y);
        if (c[3] > Constants.EPSILON) {
          tint = tintedQuads == null ? Tint.NONE : tintedQuads[i];
          color = c;
          if (quad.doubleSided) {
            intersectionRecord.setNormal(Vector3.orientNormal(ray.d, quad.n));
          } else {
            intersectionRecord.setNormal(quad.n);
          }
          intersectionRecord.setNoMediumChange(true);
          hit = true;
          if (i < quads.length / 2) {
            intersectionRecord.material = emissiveMaterial;
          }
        } else {
          intersectionRecord.distance = distance;
        }
      }
    }

    if (hit) {
      intersectionRecord.color.set(color);
      tint.tint(intersectionRecord.color, ray, scene);
    }
    return hit;
  }
}
