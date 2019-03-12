package se.llbit.chunky.block;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

// TODO: Improve rendering of chorus plants - pseudorandom plant part selection.
public class ChorusPlant extends MinecraftBlockTranslucent {
  private final String description;
  private final Collection<Quad> quads;

  static final Quad[] noside_n = {
      new Quad(
          new Vector3(12 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 12 / 16.0, 4 / 16.0),
          new Vector4(12 / 16.0, 4 / 16.0, 4 / 16.0, 12 / 16.0)),
  };

  static final Quad[] side_n = {
      // cube1
      new Quad(
          new Vector3(4 / 16.0, 12 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 12 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 12 / 16.0, 0),
          new Vector4(4 / 16.0, 12 / 16.0, 16 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(4 / 16.0, 4 / 16.0, 0),
          new Vector3(12 / 16.0, 4 / 16.0, 0),
          new Vector3(4 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector4(4 / 16.0, 12 / 16.0, 12 / 16.0, 16 / 16.0)),
      new Quad(
          new Vector3(12 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector3(12 / 16.0, 4 / 16.0, 0),
          new Vector3(12 / 16.0, 12 / 16.0, 4 / 16.0),
          new Vector4(0, 4 / 16.0, 4 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(4 / 16.0, 4 / 16.0, 0),
          new Vector3(4 / 16.0, 4 / 16.0, 4 / 16.0),
          new Vector3(4 / 16.0, 12 / 16.0, 0),
          new Vector4(0, 4 / 16.0, 4 / 16.0, 12 / 16.0)),
      new Quad(
          new Vector3(12 / 16.0, 4 / 16.0, 0),
          new Vector3(4 / 16.0, 4 / 16.0, 0),
          new Vector3(12 / 16.0, 12 / 16.0, 0),
          new Vector4(12 / 16.0, 4 / 16.0, 4 / 16.0, 12 / 16.0)),
  };

  static final Quad[][] noside = new Quad[6][];
  static final Quad[][] side = new Quad[6][];

  static {
    noside[0] = noside_n;
    noside[1] = Model.rotateY(noside[0]);
    noside[2] = Model.rotateY(noside[1]);
    noside[3] = Model.rotateY(noside[2]);
    noside[4] = Model.rotateX(noside[0]);
    noside[5] = Model.rotateNegX(noside[0]);
    side[0] = side_n;
    side[1] = Model.rotateY(side[0]);
    side[2] = Model.rotateY(side[1]);
    side[3] = Model.rotateY(side[2]);
    side[4] = Model.rotateX(side[0]);
    side[5] = Model.rotateNegX(side[0]);
  }

  public ChorusPlant(
      boolean north, boolean south, boolean east, boolean west,
      boolean up, boolean down) {
    super("chorus_plant", Texture.chorusPlant);
    localIntersect = true;
    this.description = String.format("north=%s, south=%s, east=%s, west=%s",
        north, south, east, west);
    quads = new ArrayList<>();
    if (north) {
      Collections.addAll(quads, side[0]);
    } else {
      Collections.addAll(quads, noside[0]);
    }
    if (east) {
      Collections.addAll(quads, side[1]);
    } else {
      Collections.addAll(quads, noside[1]);
    }
    if (south) {
      Collections.addAll(quads, side[2]);
    } else {
      Collections.addAll(quads, noside[2]);
    }
    if (west) {
      Collections.addAll(quads, side[3]);
    } else {
      Collections.addAll(quads, noside[3]);
    }
    if (up) {
      Collections.addAll(quads, side[4]);
    } else {
      Collections.addAll(quads, noside[4]);
    }
    if (down) {
      Collections.addAll(quads, side[5]);
    } else {
      Collections.addAll(quads, noside[5]);
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    boolean hit = false;
    ray.t = Double.POSITIVE_INFINITY;
    for (Quad quad : quads) {
        if (quad.intersect(ray)) {
          texture.getColor(ray);
          ray.t = ray.tNext;
          hit = true;
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
