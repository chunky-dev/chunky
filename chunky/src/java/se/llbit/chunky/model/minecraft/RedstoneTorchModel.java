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
  public boolean intersect(Ray ray, IntersectionRecord intersectionRecord, Scene scene) {
    return intersectWithGlow(ray, intersectionRecord, this);
  }

  static boolean intersectWithGlow(Ray ray, IntersectionRecord intersectionRecord, QuadModel model) {
    boolean hit = false;
    Quad lastFrontHitQuad = null;

    Quad[] quads = model.getQuads();
    Texture[] textures = model.getTextures();

    float[] color = null;
    int hitCount = 0;
    for (int i = 0; i < quads.length; ++i) {
      Quad quad = quads[i];
      double distance = intersectionRecord.distance;
      if (quad.closestIntersection(ray, intersectionRecord)) {
        if (quad instanceof GlowQuad) {
          // hitCount++;
          if (ray.d.dot(quad.n) < 0) {
            hitCount++;
          } else {
            hitCount--;
          }
        }
        float[] c = textures[i].getColor(intersectionRecord.uv.x, intersectionRecord.uv.y);
        if (c[3] > Constants.EPSILON && ray.d.dot(quad.n) < 0) {
          color = c;
          intersectionRecord.setNormal(quad.n);
          intersectionRecord.setNoMediumChange(true);
          hit = true;
          lastFrontHitQuad = quad;
        } else {
          intersectionRecord.distance = distance;
        }
      }
    }

    if (hit && (hitCount % 2 == 0 || !(lastFrontHitQuad instanceof GlowQuad))) {
      intersectionRecord.color.set(color);
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

    public GlowQuad(Quad quad, Matrix3 transform) {
      super(quad, transform);
    }

    @Override
    public Quad getScaled(double scale) {
      Matrix3 transform = new Matrix3();
      transform.scale(scale);
      return new GlowQuad(this, transform);
    }

    @Override
    public Quad transform(Transform transform) {
      return new GlowQuad(this, transform);
    }
  }
}
