package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An item model that is generated from a texture by extruding it to some height and adding edges that match the texture.
 * In other words, they look like the generated item models in Minecraft.
 */
public class GeneratedItemModel extends QuadModel {
  private final Quad[] quads;
  private final Texture[] textures;

  public GeneratedItemModel(Texture texture) {
    quads = generateItemModelQuads(texture);
    textures = new Texture[quads.length];
    Arrays.fill(textures, texture);
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }

  public static Quad[] generateItemModelQuads(Texture tex) {
    List<Quad> quads = new ArrayList<>();
    quads.add(new Quad(
      new Vector3(1, 0, 1),
      new Vector3(0, 0, 1),
      new Vector3(1, 0, 0),
      new Vector4(1, 0, 0, 1)
    ));
    quads.add(
      new Quad(
        new Vector3(0, 1 / 16., 1),
        new Vector3(1, 1 / 16., 1),
        new Vector3(0, 1 / 16., 0),
        new Vector4(0, 1, 0, 1)
      ));

    {
      // generate north facing edge quads
      boolean edge = false;
      int start = 0, length = 0;
      for (int y = 0; y < tex.getHeight(); y++) {
        for (int x = 0; x < tex.getWidth(); x++) {
          if (tex.getAlpha(x, y) > Ray.EPSILON && (
            y == 0 || tex.getAlpha(x, y - 1) < Ray.EPSILON
          )) {
            if (edge) {
              length++;
            } else {
              edge = true;
              start = x;
              length = 1;
            }
          } else if (edge) {
            // edge ended
            float fx = ((float) start) / tex.getWidth();
            float tx = ((float) start + length) / tex.getWidth();
            float fz = ((float) y) / tex.getHeight();
            float tz = (y + 1f) / tex.getHeight();
            quads.add(new Quad(
              new Vector3(fx, 1 / 16., fz),
              new Vector3(tx, 1 / 16., fz),
              new Vector3(fx, 0, fz),
              new Vector4(fx, tx, 1 - fz, 1 - tz)
            ));
            edge = false;
          }
        }
        if (edge) {
          float fx = ((float) start) / tex.getWidth();
          float tx = ((float) start + length) / tex.getWidth();
          float fz = ((float) y) / tex.getHeight();
          float tz = (y + 1f) / tex.getHeight();
          quads.add(new Quad(
            new Vector3(fx, 1 / 16., fz),
            new Vector3(tx, 1 / 16., fz),
            new Vector3(fx, 0, fz),
            new Vector4(fx, tx, 1 - fz, 1 - tz)
          ));
          edge = false;
        }
      }
    }
    {
      // generate south facing edge quads
      boolean edge = false;
      int start = 0, length = 0;
      for (int y = 0; y < tex.getHeight(); y++) {
        for (int x = 0; x < tex.getWidth(); x++) {
          if (tex.getAlpha(x, y) > Ray.EPSILON && (
            y == tex.getHeight() - 1 || tex.getAlpha(x, y + 1) < Ray.EPSILON
          )) {
            if (edge) {
              length++;
            } else {
              edge = true;
              start = x;
              length = 1;
            }
          } else if (edge) {
            // edge ended
            float fx = ((float) start) / tex.getWidth();
            float tx = ((float) start + length) / tex.getWidth();
            float fz = ((float) y) / tex.getHeight();
            float tz = (y + 1f) / tex.getHeight();
            quads.add(new Quad(
              new Vector3(tx, 1 / 16., tz),
              new Vector3(fx, 1 / 16., tz),
              new Vector3(tx, 0, tz),
              new Vector4(tx, fx, 1 - fz, 1 - tz)
            ));
            edge = false;
          }
        }
        if (edge) {
          float fx = ((float) start) / tex.getWidth();
          float tx = ((float) start + length) / tex.getWidth();
          float fz = ((float) y) / tex.getHeight();
          float tz = (y + 1f) / tex.getHeight();
          quads.add(new Quad(
            new Vector3(tx, 1 / 16., tz),
            new Vector3(fx, 1 / 16., tz),
            new Vector3(tx, 0, tz),
            new Vector4(tx, fx, 1 - fz, 1 - tz)
          ));
          edge = false;
        }
      }
    }
    {
      // generate west facing edge quads
      boolean edge = false;
      int start = 0, length = 0;
      for (int x = 0; x < tex.getWidth(); x++) {
        for (int y = 0; y < tex.getHeight(); y++) {
          if (tex.getAlpha(x, y) > Ray.EPSILON && (
            x == 0 || tex.getAlpha(x - 1, y) < Ray.EPSILON
          )) {
            if (edge) {
              length++;
            } else {
              edge = true;
              start = y;
              length = 1;
            }
          } else if (edge) {
            // edge ended
            float fz = ((float) start) / tex.getHeight();
            float tz = ((float) start + length) / tex.getHeight();
            float fx = ((float) x) / tex.getWidth();
            float tx = (x + 1f) / tex.getWidth();
            quads.add(new Quad(
              new Vector3(fx, 1 / 16., fz),
              new Vector3(fx, 0, fz),
              new Vector3(fx, 1 / 16., tz),
              new Vector4(fx, tx, 1 - fz, 1 - tz)
            ));
            edge = false;
          }
        }
        if (edge) {
          float fz = ((float) start) / tex.getHeight();
          float tz = ((float) start + length) / tex.getHeight();
          float fx = ((float) x) / tex.getWidth();
          float tx = (x + 1f) / tex.getWidth();
          quads.add(new Quad(
            new Vector3(fx, 1 / 16., fz),
            new Vector3(fx, 0, fz),
            new Vector3(fx, 1 / 16., tz),
            new Vector4(fx, tx, 1 - fz, 1 - tz)
          ));
          edge = false;
        }
      }
    }
    {
      // generate east facing edge quads
      boolean edge = false;
      int start = 0, length = 0;
      for (int x = 0; x < tex.getWidth(); x++) {
        for (int y = 0; y < tex.getHeight(); y++) {
          if (tex.getAlpha(x, y) > Ray.EPSILON && (
            x == tex.getWidth() - 1 || tex.getAlpha(x + 1, y) < Ray.EPSILON
          )) {
            if (edge) {
              length++;
            } else {
              edge = true;
              start = y;
              length = 1;
            }
          } else if (edge) {
            // edge ended
            float fz = ((float) start) / tex.getHeight();
            float tz = ((float) start + length) / tex.getHeight();
            float fx = ((float) x) / tex.getWidth();
            float tx = (x + 1f) / tex.getWidth();
            quads.add(new Quad(
              new Vector3(tx, 0, fz),
              new Vector3(tx, 1 / 16., fz),
              new Vector3(tx, 0, tz),
              new Vector4(fx, tx, 1 - fz, 1 - tz)
            ));
            edge = false;
          }
        }
        if (edge) {
          float fz = ((float) start) / tex.getHeight();
          float tz = ((float) start + length) / tex.getHeight();
          float fx = ((float) x) / tex.getWidth();
          float tx = (x + 1f) / tex.getWidth();
          quads.add(new Quad(
            new Vector3(tx, 0, fz),
            new Vector3(tx, 1 / 16., fz),
            new Vector3(tx, 0, tz),
            new Vector4(fx, tx, 1 - fz, 1 - tz)
          ));
          edge = false;
        }
      }
    }

    return quads.toArray(new Quad[0]);
  }
}
