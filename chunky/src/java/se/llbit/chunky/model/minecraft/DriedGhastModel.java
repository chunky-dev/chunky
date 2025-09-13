package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class DriedGhastModel extends QuadModel {
  private static final Quad[] quadsNorth = new Quad[]{
    new Quad(
      new Vector3(13 / 16.0, 10 / 16.0, 3 / 16.0),
      new Vector3(3 / 16.0, 10 / 16.0, 3 / 16.0),
      new Vector3(13 / 16.0, 10 / 16.0, 13 / 16.0),
      new Vector4(0 / 16.0, 10 / 16.0, 6 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(3 / 16.0, 0 / 16.0, 3 / 16.0),
      new Vector3(13 / 16.0, 0 / 16.0, 3 / 16.0),
      new Vector3(3 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(10 / 16.0, 0 / 16.0, 6 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(3 / 16.0, 10 / 16.0, 13 / 16.0),
      new Vector3(3 / 16.0, 10 / 16.0, 3 / 16.0),
      new Vector3(3 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(10 / 16.0, 0 / 16.0, 16 / 16.0, 6 / 16.0)
    ),
    new Quad(
      new Vector3(13 / 16.0, 10 / 16.0, 3 / 16.0),
      new Vector3(13 / 16.0, 10 / 16.0, 13 / 16.0),
      new Vector3(13 / 16.0, 0 / 16.0, 3 / 16.0),
      new Vector4(10 / 16.0, 0 / 16.0, 16 / 16.0, 6 / 16.0)
    ),
    new Quad(
      new Vector3(3 / 16.0, 10 / 16.0, 3 / 16.0),
      new Vector3(13 / 16.0, 10 / 16.0, 3 / 16.0),
      new Vector3(3 / 16.0, 0 / 16.0, 3 / 16.0),
      new Vector4(10 / 16.0, 0 / 16.0, 16 / 16.0, 6 / 16.0)
    ),
    new Quad(
      new Vector3(13 / 16.0, 10 / 16.0, 13 / 16.0),
      new Vector3(3 / 16.0, 10 / 16.0, 13 / 16.0),
      new Vector3(13 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(10 / 16.0, 0 / 16.0, 16 / 16.0, 6 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 1 / 16.0, 5 / 16.0),
      new Vector3(0 / 16.0, 1 / 16.0, 7 / 16.0),
      new Vector3(3 / 16.0, 1 / 16.0, 5 / 16.0),
      new Vector4(2.5 / 16.0, 1.5 / 16.0, 16 / 16.0, 14.5 / 16.0)
    ),
    new Quad(
      new Vector3(3 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector3(3 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector4(2.5 / 16.0, 3.5 / 16.0, 16 / 16.0, 14.5 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 1 / 16.0, 7 / 16.0),
      new Vector3(0 / 16.0, 1 / 16.0, 5 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(3.5 / 16.0, 2.5 / 16.0, 14.5 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(3 / 16.0, 1 / 16.0, 5 / 16.0),
      new Vector3(3 / 16.0, 1 / 16.0, 7 / 16.0),
      new Vector3(3 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector4(1 / 16.0, 0 / 16.0, 14.5 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 1 / 16.0, 5 / 16.0),
      new Vector3(3 / 16.0, 1 / 16.0, 5 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector4(2.5 / 16.0, 1 / 16.0, 14.5 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(3 / 16.0, 1 / 16.0, 7 / 16.0),
      new Vector3(0 / 16.0, 1 / 16.0, 7 / 16.0),
      new Vector3(3 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(5 / 16.0, 3.5 / 16.0, 14.5 / 16.0, 14 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 1 / 16.0, 9 / 16.0),
      new Vector3(0 / 16.0, 1 / 16.0, 11 / 16.0),
      new Vector3(3 / 16.0, 1 / 16.0, 9 / 16.0),
      new Vector4(2.5 / 16.0, 1.5 / 16.0, 14 / 16.0, 12.5 / 16.0)
    ),
    new Quad(
      new Vector3(3 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector3(3 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(2.5 / 16.0, 3.5 / 16.0, 14 / 16.0, 12.5 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 1 / 16.0, 11 / 16.0),
      new Vector3(0 / 16.0, 1 / 16.0, 9 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(3.5 / 16.0, 2.5 / 16.0, 12.5 / 16.0, 12 / 16.0)
    ),
    new Quad(
      new Vector3(3 / 16.0, 1 / 16.0, 9 / 16.0),
      new Vector3(3 / 16.0, 1 / 16.0, 11 / 16.0),
      new Vector3(3 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(1 / 16.0, 0 / 16.0, 12.5 / 16.0, 12 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 1 / 16.0, 9 / 16.0),
      new Vector3(3 / 16.0, 1 / 16.0, 9 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(2.5 / 16.0, 1 / 16.0, 12.5 / 16.0, 12 / 16.0)
    ),
    new Quad(
      new Vector3(3 / 16.0, 1 / 16.0, 11 / 16.0),
      new Vector3(0 / 16.0, 1 / 16.0, 11 / 16.0),
      new Vector3(3 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(5 / 16.0, 3.5 / 16.0, 12.5 / 16.0, 12 / 16.0)
    ),
    new Quad(
      new Vector3(13 / 16.0, 1 / 16.0, 5 / 16.0),
      new Vector3(13 / 16.0, 1 / 16.0, 7 / 16.0),
      new Vector3(16 / 16.0, 1 / 16.0, 5 / 16.0),
      new Vector4(2.5 / 16.0, 1.5 / 16.0, 8.5 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector3(13 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector4(2.5 / 16.0, 3.5 / 16.0, 8.5 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(13 / 16.0, 1 / 16.0, 7 / 16.0),
      new Vector3(13 / 16.0, 1 / 16.0, 5 / 16.0),
      new Vector3(13 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(0 / 16.0, 1 / 16.0, 8.5 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 1 / 16.0, 5 / 16.0),
      new Vector3(16 / 16.0, 1 / 16.0, 7 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector4(2.5 / 16.0, 3.5 / 16.0, 8.5 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(13 / 16.0, 1 / 16.0, 5 / 16.0),
      new Vector3(16 / 16.0, 1 / 16.0, 5 / 16.0),
      new Vector3(13 / 16.0, 0 / 16.0, 5 / 16.0),
      new Vector4(1 / 16.0, 2.5 / 16.0, 8.5 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 1 / 16.0, 7 / 16.0),
      new Vector3(13 / 16.0, 1 / 16.0, 7 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(3.5 / 16.0, 5 / 16.0, 8.5 / 16.0, 8 / 16.0)
    ),
    new Quad(
      new Vector3(13 / 16.0, 1 / 16.0, 9 / 16.0),
      new Vector3(13 / 16.0, 1 / 16.0, 11 / 16.0),
      new Vector3(16 / 16.0, 1 / 16.0, 9 / 16.0),
      new Vector4(2.5 / 16.0, 1.5 / 16.0, 10.5 / 16.0, 12 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector3(13 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(2.5 / 16.0, 3.5 / 16.0, 10.5 / 16.0, 12 / 16.0)
    ),
    new Quad(
      new Vector3(13 / 16.0, 1 / 16.0, 11 / 16.0),
      new Vector3(13 / 16.0, 1 / 16.0, 9 / 16.0),
      new Vector3(13 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(0 / 16.0, 1 / 16.0, 10.5 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 1 / 16.0, 9 / 16.0),
      new Vector3(16 / 16.0, 1 / 16.0, 11 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(2.5 / 16.0, 3.5 / 16.0, 10.5 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(13 / 16.0, 1 / 16.0, 9 / 16.0),
      new Vector3(16 / 16.0, 1 / 16.0, 9 / 16.0),
      new Vector3(13 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(1 / 16.0, 2.5 / 16.0, 10.5 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 1 / 16.0, 11 / 16.0),
      new Vector3(13 / 16.0, 1 / 16.0, 11 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 11 / 16.0),
      new Vector4(3.5 / 16.0, 5 / 16.0, 10.5 / 16.0, 10 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 1 / 16.0, 13 / 16.0),
      new Vector3(9 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(11 / 16.0, 1 / 16.0, 13 / 16.0),
      new Vector4(6 / 16.0, 7.5 / 16.0, 14.5 / 16.0, 13.5 / 16.0)
    ),
    new Quad(
      new Vector3(11 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector3(11 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(7.5 / 16.0, 9 / 16.0, 13.5 / 16.0, 14.5 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(9 / 16.0, 1 / 16.0, 13 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(8.5 / 16.0, 10 / 16.0, 13.5 / 16.0, 13 / 16.0)
    ),
    new Quad(
      new Vector3(11 / 16.0, 1 / 16.0, 13 / 16.0),
      new Vector3(11 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(11 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(6 / 16.0, 7.5 / 16.0, 13.5 / 16.0, 13 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 1 / 16.0, 13 / 16.0),
      new Vector3(11 / 16.0, 1 / 16.0, 13 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(5 / 16.0, 6 / 16.0, 13.5 / 16.0, 13 / 16.0)
    ),
    new Quad(
      new Vector3(11 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(9 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(11 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(7.5 / 16.0, 8.5 / 16.0, 13.5 / 16.0, 13 / 16.0)
    ),
    new Quad(
      new Vector3(5 / 16.0, 1 / 16.0, 13 / 16.0),
      new Vector3(5 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(7 / 16.0, 1 / 16.0, 13 / 16.0),
      new Vector4(6 / 16.0, 7.5 / 16.0, 16 / 16.0, 15 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector3(5 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(7.5 / 16.0, 9 / 16.0, 15 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(5 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(5 / 16.0, 1 / 16.0, 13 / 16.0),
      new Vector3(5 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(8.5 / 16.0, 10 / 16.0, 15 / 16.0, 14.5 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 1 / 16.0, 13 / 16.0),
      new Vector3(7 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(6 / 16.0, 7.5 / 16.0, 15 / 16.0, 14.5 / 16.0)
    ),
    new Quad(
      new Vector3(5 / 16.0, 1 / 16.0, 13 / 16.0),
      new Vector3(7 / 16.0, 1 / 16.0, 13 / 16.0),
      new Vector3(5 / 16.0, 0 / 16.0, 13 / 16.0),
      new Vector4(5 / 16.0, 6 / 16.0, 15 / 16.0, 14.5 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(5 / 16.0, 1 / 16.0, 16 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(7.5 / 16.0, 8.5 / 16.0, 15 / 16.0, 14.5 / 16.0)
    )
  };


  private static final Quad[][] orientedQuads = new Quad[4][];

  static {
    orientedQuads[0] = quadsNorth;
    orientedQuads[1] = Model.rotateY(orientedQuads[0]);
    orientedQuads[2] = Model.rotateY(orientedQuads[1]);
    orientedQuads[3] = Model.rotateY(orientedQuads[2]);
  }

  private final Quad[] quads;
  private final Texture[] textures;

  public DriedGhastModel(
    String facing, int hydration
  ) {
    quads = orientedQuads[getOrientationIndex(facing)];
    Texture top, bottom, west, east, north, south, tentacles;
    switch (hydration) {
      case 1:
        top = Texture.driedGhastHydration1Top;
        bottom = Texture.driedGhastHydration1Bottom;
        west = Texture.driedGhastHydration1West;
        east = Texture.driedGhastHydration1East;
        north = Texture.driedGhastHydration1North;
        south = Texture.driedGhastHydration1South;
        tentacles = Texture.driedGhastHydration1Tentacles;
        break;
      case 2:
        top = Texture.driedGhastHydration2Top;
        bottom = Texture.driedGhastHydration2Bottom;
        west = Texture.driedGhastHydration2West;
        east = Texture.driedGhastHydration2East;
        north = Texture.driedGhastHydration2North;
        south = Texture.driedGhastHydration2South;
        tentacles = Texture.driedGhastHydration2Tentacles;
        break;
      case 3:
        top = Texture.driedGhastHydration3Top;
        bottom = Texture.driedGhastHydration3Bottom;
        west = Texture.driedGhastHydration3West;
        east = Texture.driedGhastHydration3East;
        north = Texture.driedGhastHydration3North;
        south = Texture.driedGhastHydration3South;
        tentacles = Texture.driedGhastHydration3Tentacles;
        break;
      case 0:
      default:
        top = Texture.driedGhastHydration0Top;
        bottom = Texture.driedGhastHydration0Bottom;
        west = Texture.driedGhastHydration0West;
        east = Texture.driedGhastHydration0East;
        north = Texture.driedGhastHydration0North;
        south = Texture.driedGhastHydration0South;
        tentacles = Texture.driedGhastHydration0Tentacles;
        break;
    }
    textures = new Texture[]{
      top, bottom, west, east, north, south,
      tentacles, tentacles, tentacles, tentacles, tentacles, tentacles, tentacles, tentacles, tentacles,
      tentacles, tentacles, tentacles, tentacles, tentacles, tentacles, tentacles, tentacles, tentacles,
      tentacles, tentacles, tentacles, tentacles, tentacles, tentacles, tentacles, tentacles, tentacles,
      tentacles, tentacles, tentacles, tentacles, tentacles, tentacles, tentacles, tentacles, tentacles
    };
  }

  private static int getOrientationIndex(String facing) {
    return switch (facing) {
      case "east" -> 1;
      case "south" -> 2;
      case "west" -> 3;
      default -> 0;
    };
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
