package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class LeafLitterModel extends QuadModel {
  private static final Quad[] leafLitter1 = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 0.25 / 16.0, 8 / 16.0),
      new Vector3(8 / 16.0, 0.25 / 16.0, 8 / 16.0),
      new Vector3(0 / 16.0, 0.25 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 8 / 16.0, 8 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0.25 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, 0.25 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0.25 / 16.0, 8 / 16.0),
      new Vector4(0 / 16.0, 8 / 16.0, 16 / 16.0, 8 / 16.0)
    )
  };
  private static final Quad[] leafLitter2 = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 0.25 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, 0.25 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 0.25 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 8 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0.25 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, 0.25 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0.25 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 8 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };
  private static final Quad[] leafLitter3 = new Quad[]{
    new Quad(
      new Vector3(8 / 16.0, 0.25 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0.25 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, 0.25 / 16.0, 8 / 16.0),
      new Vector4(8 / 16.0, 16 / 16.0, 0 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 0.25 / 16.0, 8 / 16.0),
      new Vector3(16 / 16.0, 0.25 / 16.0, 8 / 16.0),
      new Vector3(8 / 16.0, 0.25 / 16.0, 16 / 16.0),
      new Vector4(8 / 16.0, 16 / 16.0, 8 / 16.0, 0 / 16.0)
    )
  };
  private static final Quad[] leafLitter4 = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 0.25 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0.25 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 0.25 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0.25 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0.25 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0.25 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };

  private final Quad[] quads;
  private final Texture[] textures;
  private final Tint[] tints;

  public LeafLitterModel(String facing, int segmentAmount) {
    ArrayList<Quad> quads = new ArrayList<>();
    if (segmentAmount == 1) {
      Collections.addAll(quads, leafLitter1);
    }
    if (segmentAmount == 2 || segmentAmount == 3) {
      Collections.addAll(quads, leafLitter2);
    }
    if (segmentAmount == 3) {
      Collections.addAll(quads, leafLitter3);
    }
    if (segmentAmount == 4) {
      Collections.addAll(quads, leafLitter4);
    }
    this.quads = switch (facing) {
      case "east" -> Model.rotateY(quads.toArray(new Quad[0]));
      case "south" -> Model.rotateY(Model.rotateY(quads.toArray(new Quad[0])));
      case "west" -> Model.rotateNegY(quads.toArray(new Quad[0]));
      default -> quads.toArray(new Quad[0]);
    };
    textures = new Texture[this.quads.length];
    Arrays.fill(textures, Texture.leafLitter);
    tints = new Tint[this.quads.length];
    Arrays.fill(tints, Tint.BIOME_DRY_FOLIAGE);
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
  public Tint[] getTints() {
    return tints;
  }
}
