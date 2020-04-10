package se.llbit.chunky.block;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class TurtleEgg extends MinecraftBlockTranslucent {
  private static final Quad[][] egg_models = {
      {
      // cube1
      new Quad(
          new Vector3(5 / 16.0, 7 / 16.0, 9 / 16.0),
          new Vector3(10 / 16.0, 7 / 16.0, 9 / 16.0),
          new Vector3(5 / 16.0, 7 / 16.0, 4 / 16.0),
          new Vector4(0, 4 / 16.0, 12 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(5 / 16.0, 0, 4 / 16.0),
          new Vector3(10 / 16.0, 0, 4 / 16.0),
          new Vector3(5 / 16.0, 0, 9 / 16.0),
          new Vector4(0, 4 / 16.0, 12 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 9 / 16.0),
          new Vector3(10 / 16.0, 0, 4 / 16.0),
          new Vector3(10 / 16.0, 7 / 16.0, 9 / 16.0),
          new Vector4(1 / 16.0, 5 / 16.0, 5 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(5 / 16.0, 0, 4 / 16.0),
          new Vector3(5 / 16.0, 0, 9 / 16.0),
          new Vector3(5 / 16.0, 7 / 16.0, 4 / 16.0),
          new Vector4(1 / 16.0, 5 / 16.0, 5 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 4 / 16.0),
          new Vector3(5 / 16.0, 0, 4 / 16.0),
          new Vector3(10 / 16.0, 7 / 16.0, 4 / 16.0),
          new Vector4(1 / 16.0, 5 / 16.0, 5 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(5 / 16.0, 0, 9 / 16.0),
          new Vector3(10 / 16.0, 0, 9 / 16.0),
          new Vector3(5 / 16.0, 7 / 16.0, 9 / 16.0),
          new Vector4(1 / 16.0, 5 / 16.0, 5 / 16.0, 12 / 16.0)),
      },
      {
      // cube2
      new Quad(
          new Vector3(1 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector3(5 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector3(1 / 16.0, 5 / 16.0, 7 / 16.0),
          new Vector4(6 / 16.0, 10 / 16.0, 5 / 16.0, 9 / 16.0)),
      new Quad(
          new Vector3(1 / 16.0, 0, 7 / 16.0),
          new Vector3(5 / 16.0, 0, 7 / 16.0),
          new Vector3(1 / 16.0, 0, 11 / 16.0),
          new Vector4(6 / 16.0, 10 / 16.0, 5 / 16.0, 9 / 16.0)),
      new Quad(
          new Vector3(5 / 16.0, 0, 11 / 16.0),
          new Vector3(5 / 16.0, 0, 7 / 16.0),
          new Vector3(5 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector4(10 / 16.0, 14 / 16.0, 1 / 16.0, 6 / 16.0)),
      new Quad(
          new Vector3(1 / 16.0, 0, 7 / 16.0),
          new Vector3(1 / 16.0, 0, 11 / 16.0),
          new Vector3(1 / 16.0, 5 / 16.0, 7 / 16.0),
          new Vector4(10 / 16.0, 14 / 16.0, 1 / 16.0, 6 / 16.0)),
      new Quad(
          new Vector3(5 / 16.0, 0, 7 / 16.0),
          new Vector3(1 / 16.0, 0, 7 / 16.0),
          new Vector3(5 / 16.0, 5 / 16.0, 7 / 16.0),
          new Vector4(10 / 16.0, 14 / 16.0, 1 / 16.0, 6 / 16.0)),
      new Quad(
          new Vector3(1 / 16.0, 0, 11 / 16.0),
          new Vector3(5 / 16.0, 0, 11 / 16.0),
          new Vector3(1 / 16.0, 5 / 16.0, 11 / 16.0),
          new Vector4(10 / 16.0, 14 / 16.0, 1 / 16.0, 6 / 16.0)),
      },
      {
      // cube3
      new Quad(
          new Vector3(11 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector3(14 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector3(11 / 16.0, 4 / 16.0, 7 / 16.0),
          new Vector4(5 / 16.0, 8 / 16.0, 13 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(11 / 16.0, 0, 7 / 16.0),
          new Vector3(14 / 16.0, 0, 7 / 16.0),
          new Vector3(11 / 16.0, 0, 10 / 16.0),
          new Vector4(5 / 16.0, 8 / 16.0, 13 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(14 / 16.0, 0, 10 / 16.0),
          new Vector3(14 / 16.0, 0, 7 / 16.0),
          new Vector3(14 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector4(8 / 16.0, 11 / 16.0, 9 / 16.0, 13 / 16.0)),
      new Quad(
          new Vector3(11 / 16.0, 0, 7 / 16.0),
          new Vector3(11 / 16.0, 0, 10 / 16.0),
          new Vector3(11 / 16.0, 4 / 16.0, 7 / 16.0),
          new Vector4(8 / 16.0, 11 / 16.0, 9 / 16.0, 13 / 16.0)),
      new Quad(
          new Vector3(14 / 16.0, 0, 7 / 16.0),
          new Vector3(11 / 16.0, 0, 7 / 16.0),
          new Vector3(14 / 16.0, 4 / 16.0, 7 / 16.0),
          new Vector4(8 / 16.0, 11 / 16.0, 9 / 16.0, 13 / 16.0)),
      new Quad(
          new Vector3(11 / 16.0, 0, 10 / 16.0),
          new Vector3(14 / 16.0, 0, 10 / 16.0),
          new Vector3(11 / 16.0, 4 / 16.0, 10 / 16.0),
          new Vector4(8 / 16.0, 11 / 16.0, 9 / 16.0, 13 / 16.0)),
      },
      {
      // cube4
      new Quad(
          new Vector3(7 / 16.0, 3 / 16.0, 13 / 16.0),
          new Vector3(10 / 16.0, 3 / 16.0, 13 / 16.0),
          new Vector3(7 / 16.0, 3 / 16.0, 10 / 16.0),
          new Vector4(0, 4 / 16.0, 1 / 16.0, 5 / 16.0)),
      new Quad(
          new Vector3(7 / 16.0, 0, 10 / 16.0),
          new Vector3(10 / 16.0, 0, 10 / 16.0),
          new Vector3(7 / 16.0, 0, 13 / 16.0),
          new Vector4(0, 4 / 16.0, 1 / 16.0, 5 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 13 / 16.0),
          new Vector3(10 / 16.0, 0, 10 / 16.0),
          new Vector3(10 / 16.0, 3 / 16.0, 13 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 1 / 16.0, 5 / 16.0)),
      new Quad(
          new Vector3(7 / 16.0, 0, 10 / 16.0),
          new Vector3(7 / 16.0, 0, 13 / 16.0),
          new Vector3(7 / 16.0, 3 / 16.0, 10 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 1 / 16.0, 5 / 16.0)),
      new Quad(
          new Vector3(10 / 16.0, 0, 10 / 16.0),
          new Vector3(7 / 16.0, 0, 10 / 16.0),
          new Vector3(10 / 16.0, 3 / 16.0, 10 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 1 / 16.0, 5 / 16.0)),
      new Quad(
          new Vector3(7 / 16.0, 0, 13 / 16.0),
          new Vector3(10 / 16.0, 0, 13 / 16.0),
          new Vector3(7 / 16.0, 3 / 16.0, 13 / 16.0),
          new Vector4(4 / 16.0, 8 / 16.0, 1 / 16.0, 5 / 16.0)),
      },
  };

  private static final Texture[] textures = {
      Texture.turtleEgg,
      Texture.turtleEggSlightlyCracked,
      Texture.turtleEggVeryCracked
  };

  static final Quad[][][] rot;

  static {
    rot = new Quad[3][][];
    rot[0] = egg_models;
    rot[1] = new Quad[4][];
    for (int i = 0; i < 4; ++i) {
      rot[1][i] = Model.rotateNegY(egg_models[i]);
    }
    rot[2] = new Quad[4][];
    for (int i = 0; i < 4; ++i) {
      rot[2][i] = Model.rotateY(egg_models[i]);
    }
  }

  private final String description;
  private int eggs;
  private int hatch;

  public TurtleEgg(int eggs, int hatch) {
    super("turtle_egg", Texture.turtleEgg);
    eggs = Math.max(1, Math.min(egg_models.length, eggs));
    hatch = Math.max(0, Math.min(rot.length, hatch));
    this.description = String.format("eggs=%d, hatch=%d", eggs, hatch);
    this.eggs = eggs;
    this.hatch = hatch;
    localIntersect = true;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (int i = 0; i < eggs; ++i) {
      for (Quad quad : rot[hatch][i]) {
        if (quad.intersect(ray)) {
          textures[hatch].getColor(ray);
          ray.n.set(quad.n);
          ray.t = ray.tNext;
          hit = true;
        }
      }
    }
    if (hit) {
      ray.distance += ray.t;
      ray.o.scaleAdd(ray.t, ray.d);
    }
    return hit;
  }

  @Override public String description() {
    return description;
  }
}
