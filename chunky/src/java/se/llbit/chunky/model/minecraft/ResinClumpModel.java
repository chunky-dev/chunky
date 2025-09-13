package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Arrays;

public class ResinClumpModel extends QuadModel {
  private static final Quad northQuad;
  private static final Quad southQuad;
  private static final Quad eastQuad;
  private static final Quad westQuad;
  private static final Quad upQuad;
  private static final Quad downQuad;

  static {
    northQuad = new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 0.1 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 0.1 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0.1 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0),
      true);
    eastQuad = new Quad(northQuad, Transform.NONE.rotateY());
    southQuad = new Quad(eastQuad, Transform.NONE.rotateY());
    westQuad = new Quad(northQuad, Transform.NONE.rotateNegY());
    upQuad = new Quad(northQuad, Transform.NONE.rotateX());
    downQuad = new Quad(northQuad, Transform.NONE.rotateNegX());
  }

  private final Quad[] quads;
  private final Texture[] textures;

  public ResinClumpModel(boolean north, boolean south, boolean east, boolean west, boolean up, boolean down) {
    ArrayList<Quad> quads = new ArrayList<>();
    boolean allSides = !north && !south && !east && !west && !up && !down;
    if (north || allSides) {
      quads.add(northQuad);
    }
    if (south || allSides) {
      quads.add(southQuad);
    }
    if (east || allSides) {
      quads.add(eastQuad);
    }
    if (west || allSides) {
      quads.add(westQuad);
    }
    if (up || allSides) {
      quads.add(upQuad);
    }
    if (down || allSides) {
      quads.add(downQuad);
    }
    this.quads = quads.toArray(new Quad[0]);
    this.textures = new Texture[this.quads.length];
    Arrays.fill(this.textures, Texture.resinClump);
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
