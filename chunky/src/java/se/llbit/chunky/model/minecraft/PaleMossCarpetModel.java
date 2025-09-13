package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaleMossCarpetModel extends QuadModel {
  private static final Quad[] carpet = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 1 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 1 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 1 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 1 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 1 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 1 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 1 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 1 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 1 / 16.0, 0 / 16.0)
    )
  };

  private static final Quad[] carpetSide = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 0.1 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 0.1 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0.1 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0),
      true
    )
  };

  private final Quad[] quads;
  private final Texture[] textures;

  public PaleMossCarpetModel(boolean bottom, String north, String east, String south, String west) {
    List<Quad> quads = new ArrayList<>();
    List<Texture> textures = new ArrayList<>();

    boolean noSides = !bottom && north.equals("none") && east.equals("none") && south.equals("none") && west.equals("none");

    // bottom
    if (bottom || noSides) {
      quads.addAll(Arrays.asList(carpet));
      for (Quad quad : quads) {
        textures.add(Texture.paleMossCarpet);
      }
    }

    // north side
    if (!north.equals("none") || noSides) {
      quads.addAll(Arrays.asList(carpetSide));
      textures.add(north.equals("tall") || noSides ? Texture.paleMossCarpetSideTall : Texture.paleMossCarpetSideSmall);
    }

    // east side
    if (!east.equals("none") || noSides) {
      quads.addAll(Arrays.asList(Model.rotateY(carpetSide)));
      textures.add(east.equals("tall") || noSides ? Texture.paleMossCarpetSideTall : Texture.paleMossCarpetSideSmall);
    }

    // east side
    if (!south.equals("none") || noSides) {
      quads.addAll(Arrays.asList(Model.rotateY(carpetSide, Math.toRadians(180))));
      textures.add(south.equals("tall") || noSides ? Texture.paleMossCarpetSideTall : Texture.paleMossCarpetSideSmall);
    }

    // east side
    if (!west.equals("none") || noSides) {
      quads.addAll(Arrays.asList(Model.rotateNegY(carpetSide)));
      textures.add(west.equals("tall") || noSides ? Texture.paleMossCarpetSideTall : Texture.paleMossCarpetSideSmall);
    }

    this.quads = quads.toArray(new Quad[0]);
    this.textures = textures.toArray(new Texture[0]);
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
