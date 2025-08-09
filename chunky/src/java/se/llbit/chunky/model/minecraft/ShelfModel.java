package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ShelfModel extends QuadModel {
  private static final Quad[] shelfBody = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 13 / 16.0),
      new Vector4(16 / 16.0, 8 / 16.0, 12.5 / 16.0, 11 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 8 / 16.0, 11.5 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 13 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 14.5 / 16.0, 16 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(9.5 / 16.0, 8 / 16.0, 16 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 8 / 16.0, 16 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 4 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 4 / 16.0, 13 / 16.0),
      new Vector3(0 / 16.0, 4 / 16.0, 11 / 16.0),
      new Vector4(8 / 16.0, 16 / 16.0, 11.5 / 16.0, 12.5 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(16 / 16.0, 8 / 16.0, 12.5 / 16.0, 11.5 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 4 / 16.0, 13 / 16.0),
      new Vector3(0 / 16.0, 4 / 16.0, 11 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(6.5 / 16.0, 5.5 / 16.0, 10 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 4 / 16.0, 11 / 16.0),
      new Vector3(16 / 16.0, 4 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(2.5 / 16.0, 1.5 / 16.0, 10 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 4 / 16.0, 11 / 16.0),
      new Vector3(16 / 16.0, 4 / 16.0, 11 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(8 / 16.0, 0 / 16.0, 10 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 13 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 11 / 16.0),
      new Vector4(16 / 16.0, 8 / 16.0, 11 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 12 / 16.0, 11 / 16.0),
      new Vector3(16 / 16.0, 12 / 16.0, 11 / 16.0),
      new Vector3(0 / 16.0, 12 / 16.0, 13 / 16.0),
      new Vector4(8 / 16.0, 16 / 16.0, 10 / 16.0, 11 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 13 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 11 / 16.0),
      new Vector3(0 / 16.0, 12 / 16.0, 13 / 16.0),
      new Vector4(6.5 / 16.0, 5.5 / 16.0, 16 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 11 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 12 / 16.0, 11 / 16.0),
      new Vector4(2.5 / 16.0, 1.5 / 16.0, 16 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 11 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 11 / 16.0),
      new Vector3(0 / 16.0, 12 / 16.0, 11 / 16.0),
      new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 14 / 16.0)
    )
  };

  private static final Quad[] shelfLeft = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 12 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 12 / 16.0, 13 / 16.0),
      new Vector3(0 / 16.0, 4 / 16.0, 13 / 16.0),
      new Vector4(8 / 16.0, 0 / 16.0, 8 / 16.0, 4 / 16.0)
    )
  };

  private static final Quad[] shelfRight = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 12 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 12 / 16.0, 13 / 16.0),
      new Vector3(0 / 16.0, 4 / 16.0, 13 / 16.0),
      new Vector4(16 / 16.0, 8 / 16.0, 8 / 16.0, 4 / 16.0)
    )
  };

  private static final Quad[] shelfCenter = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 12 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 12 / 16.0, 13 / 16.0),
      new Vector3(0 / 16.0, 4 / 16.0, 13 / 16.0),
      new Vector4(8 / 16.0, 0 / 16.0, 4 / 16.0, 0 / 16.0)
    )
  };

  private static final Quad[] shelfUnconnected = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 12 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 12 / 16.0, 13 / 16.0),
      new Vector3(0 / 16.0, 4 / 16.0, 13 / 16.0),
      new Vector4(16 / 16.0, 8 / 16.0, 4 / 16.0, 0 / 16.0)
    )
  };

  private static final Quad[] shelfUnpowered = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 12 / 16.0, 13 / 16.0),
      new Vector3(16 / 16.0, 12 / 16.0, 13 / 16.0),
      new Vector3(0 / 16.0, 4 / 16.0, 13 / 16.0),
      new Vector4(8 / 16.0, 0 / 16.0, 14 / 16.0, 10 / 16.0)
    )
  };


  private final Quad[] quads;
  private final Texture[] textures;

  public ShelfModel(Texture texture, String facing, boolean powered, String sideChain) {
    ArrayList<Quad> quads = new ArrayList<>();
    Collections.addAll(quads, shelfBody);
    if (powered) {
      if (sideChain.equals("left")) {
        Collections.addAll(quads, shelfLeft);
      } else if (sideChain.equals("right")) {
        Collections.addAll(quads, shelfRight);
      } else if (sideChain.equals("center")) {
        Collections.addAll(quads, shelfCenter);
      } else if (sideChain.equals("unconnected")) {
        Collections.addAll(quads, shelfUnconnected);
      }
    } else {
      Collections.addAll(quads, shelfUnpowered);
    }
    this.quads = switch (facing) {
      case "east" -> Model.rotateY(quads.toArray(new Quad[0]));
      case "south" -> Model.rotateY(Model.rotateY(quads.toArray(new Quad[0])));
      case "west" -> Model.rotateNegY(quads.toArray(new Quad[0]));
      default -> quads.toArray(new Quad[0]);
    };
    textures = new Texture[this.quads.length];
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
}
