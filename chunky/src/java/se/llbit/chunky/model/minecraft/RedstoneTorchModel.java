package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.*;

import java.util.Arrays;

public class RedstoneTorchModel extends QuadModel {
  private static final Quad[] quads = new Quad[]{
    new Quad(
      new Vector3(7 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 1 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 10 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 10 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 0 / 16.0)
    ),
    new GlowQuad(
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 9.5 / 16.0),
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 9.5 / 16.0),
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 6.5 / 16.0),
      new Vector4(8 / 16.0, 9 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new GlowQuad(
      new Vector3(6.5 / 16.0, 10.5 / 16.0, 6.5 / 16.0),
      new Vector3(9.5 / 16.0, 10.5 / 16.0, 6.5 / 16.0),
      new Vector3(6.5 / 16.0, 10.5 / 16.0, 9.5 / 16.0),
      new Vector4(7 / 16.0, 8 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new GlowQuad(
      new Vector3(9.5 / 16.0, 10.5 / 16.0, 6.5 / 16.0),
      new Vector3(6.5 / 16.0, 10.5 / 16.0, 6.5 / 16.0),
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 6.5 / 16.0),
      new Vector4(10 / 16.0, 9 / 16.0, 10 / 16.0, 9 / 16.0)
    ),
    new GlowQuad(
      new Vector3(9.5 / 16.0, 10.5 / 16.0, 9.5 / 16.0),
      new Vector3(9.5 / 16.0, 10.5 / 16.0, 6.5 / 16.0),
      new Vector3(9.5 / 16.0, 7.5 / 16.0, 9.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 9 / 16.0, 8 / 16.0)
    ),
    new GlowQuad(
      new Vector3(6.5 / 16.0, 10.5 / 16.0, 9.5 / 16.0),
      new Vector3(9.5 / 16.0, 10.5 / 16.0, 9.5 / 16.0),
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 9.5 / 16.0),
      new Vector4(7 / 16.0, 6 / 16.0, 10 / 16.0, 9 / 16.0)
    ),
    new GlowQuad(
      new Vector3(6.5 / 16.0, 10.5 / 16.0, 6.5 / 16.0),
      new Vector3(6.5 / 16.0, 10.5 / 16.0, 9.5 / 16.0),
      new Vector3(6.5 / 16.0, 7.5 / 16.0, 6.5 / 16.0),
      new Vector4(10 / 16.0, 9 / 16.0, 9 / 16.0, 8 / 16.0)
    )
  };

  private final Texture[] textures;

  public RedstoneTorchModel(boolean isLit) {
    this.textures = new Texture[quads.length];
    Arrays.fill(this.textures, isLit ? Texture.redstoneTorchOn : Texture.redstoneTorchOff);
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
    return intersectWithGlow(ray, scene, this);
  }

  static boolean intersectWithGlow(Ray ray, Scene scene, QuadModel model) {
    boolean hit = false;
    Quad lastFrontHitQuad = null;
    ray.t = Double.POSITIVE_INFINITY;

    Quad[] quads = model.getQuads();
    Texture[] textures = model.getTextures();

    float[] color = null;
    int hitCount = 0;
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      if (quad.intersect(ray)) {
        if (quad instanceof GlowQuad) {
          // hitCount++;
          if (ray.d.dot(quad.n) < 0) {
            hitCount++;
          } else {
            hitCount--;
          }
        }
        float[] c = textures[i].getColor(ray.u, ray.v);
        if (c[3] > Ray.EPSILON) {
          if (ray.d.dot(quad.n) < 0) {
            color = c;
            ray.t = ray.tNext;
            ray.setNormal(quad.n);
            hit = true;
            lastFrontHitQuad = quad;
          }
        }
      }
    }

    if (hit && (hitCount % 2 == 0 || !(lastFrontHitQuad instanceof GlowQuad))) {
      double px = ray.o.x - Math.floor(ray.o.x + ray.d.x * Ray.OFFSET) + ray.d.x * ray.tNext;
      double py = ray.o.y - Math.floor(ray.o.y + ray.d.y * Ray.OFFSET) + ray.d.y * ray.tNext;
      double pz = ray.o.z - Math.floor(ray.o.z + ray.d.z * Ray.OFFSET) + ray.d.z * ray.tNext;
      if (px < E0 || px > E1 || py < E0 || py > E1 || pz < E0 || pz > E1) {
        // TODO this check is only really needed for wall torches
        return false;
      }

      ray.color.set(color);
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
      return true;
    }
    return false;
  }

  static class GlowQuad extends Quad {
    public GlowQuad(Vector3 v0, Vector3 v1, Vector3 v2, Vector4 uv) {
      super(v0, v1, v2, uv, true);
    }

    public GlowQuad(Quad quad, Transform transform) {
      super(quad, transform);
    }

    @Override
    public Quad transform(Transform transform) {
      return new GlowQuad(this, transform);
    }
  }
}
