package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Arrays;

public class SculkVeinModel extends QuadModel {

  private static final Quad[] sculkVein = {
      // North
      new Quad(new Vector3(0, 0, 0.1 / 16), new Vector3(1, 0, 0.1 / 16),
          new Vector3(0, 1, 0.1 / 16), new Vector4(0, 1, 0, 1), true),

      // South
      new Quad(new Vector3(1, 0, 15.9 / 16), new Vector3(0, 0, 15.9 / 16),
          new Vector3(1, 1, 15.9 / 16), new Vector4(1, 0, 0, 1), true),

      // East
      new Quad(new Vector3(15.9 / 16, 0, 0), new Vector3(15.9 / 16, 0, 1),
          new Vector3(15.9 / 16, 1, 0), new Vector4(0, 1, 0, 1), true),

      // West
      new Quad(new Vector3(0.1 / 16, 0, 1), new Vector3(0.1 / 16, 0, 0),
          new Vector3(0.1 / 16, 1, 1), new Vector4(1, 0, 0, 1), true),

      // Top
      new Quad(new Vector3(0, 15.9 / 16, 0), new Vector3(1, 15.9 / 16, 0),
          new Vector3(0, 15.9 / 16, 1), new Vector4(0, 1, 0, 1), true),

      // Bottom
      new Quad(new Vector3(0, 0.1 / 16, 0), new Vector3(1, 0.1 / 16, 0),
          new Vector3(0, 0.1 / 16, 1), new Vector4(0, 1, 0, 1), true),
  };

  private final Quad[] quads;
  private final Texture[] textures;

  public SculkVeinModel(int connections) {
    ArrayList<Quad> quads = new ArrayList<>();
    for (int i = 0; i < sculkVein.length; i++) {
      if ((connections & (1 << i)) != 0)
        quads.add(sculkVein[i]);
    }
    this.quads = quads.toArray(new Quad[0]);
    this.textures = new Texture[this.quads.length];
    Arrays.fill(this.textures, Texture.sculkVein);
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }
}
