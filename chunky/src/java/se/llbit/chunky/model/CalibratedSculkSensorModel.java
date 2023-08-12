package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class CalibratedSculkSensorModel extends QuadModel {
  private static final Texture bottom = Texture.sculkSensorBottom;
  private static final Texture side = Texture.sculkSensorSide;
  private static final Texture calibrated_side = Texture.calibratedSculkSensorInputSide;
  private static final Texture top = Texture.calibratedSculkSensorTop;
  private static final Texture[] tex = new Texture[]{
    top, bottom, side, side, side, calibrated_side,
  };

  private static final Quad[] quadsNorth = Model.join(
    new Quad[]{
      new Quad(
        new Vector3(0 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(16 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(0 / 16.0, 8 / 16.0, 0 / 16.0),
        new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
        new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
        new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
        new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(0 / 16.0, 8 / 16.0, 0 / 16.0),
        new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(16 / 16.0, 8 / 16.0, 0 / 16.0),
        new Vector3(16 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 8 / 16.0, 0 / 16.0),
        new Vector3(16 / 16.0, 8 / 16.0, 0 / 16.0),
        new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(16 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(0 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 8 / 16.0, 0 / 16.0)
      )
    },
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(-1 / 16.0, 16 / 16.0, 3 / 16.0),
          new Vector3(7 / 16.0, 16 / 16.0, 3 / 16.0),
          new Vector3(-1 / 16.0, 8 / 16.0, 3 / 16.0),
          new Vector4(12 / 16.0, 4 / 16.0, 8 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(7 / 16.0, 16 / 16.0, 3 / 16.0),
          new Vector3(-1 / 16.0, 16 / 16.0, 3 / 16.0),
          new Vector3(7 / 16.0, 8 / 16.0, 3 / 16.0),
          new Vector4(4 / 16.0, 12 / 16.0, 8 / 16.0, 0 / 16.0)
        )
      },
      Math.toRadians(45),
      new Vector3(3, 0, 3)
    ),
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(9 / 16.0, 16 / 16.0, 3 / 16.0),
          new Vector3(17 / 16.0, 16 / 16.0, 3 / 16.0),
          new Vector3(9 / 16.0, 8 / 16.0, 3 / 16.0),
          new Vector4(4 / 16.0, 12 / 16.0, 8 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(17 / 16.0, 16 / 16.0, 3 / 16.0),
          new Vector3(9 / 16.0, 16 / 16.0, 3 / 16.0),
          new Vector3(17 / 16.0, 8 / 16.0, 3 / 16.0),
          new Vector4(12 / 16.0, 4 / 16.0, 8 / 16.0, 0 / 16.0)
        )
      },
      Math.toRadians(-45),
      new Vector3(13, 0, 3)
    ),
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(9 / 16.0, 16 / 16.0, 13 / 16.0),
          new Vector3(17 / 16.0, 16 / 16.0, 13 / 16.0),
          new Vector3(9 / 16.0, 8 / 16.0, 13 / 16.0),
          new Vector4(4 / 16.0, 12 / 16.0, 8 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(17 / 16.0, 16 / 16.0, 13 / 16.0),
          new Vector3(9 / 16.0, 16 / 16.0, 13 / 16.0),
          new Vector3(17 / 16.0, 8 / 16.0, 13 / 16.0),
          new Vector4(12 / 16.0, 4 / 16.0, 8 / 16.0, 0 / 16.0)
        )
      },
      Math.toRadians(45),
      new Vector3(13, 0, 13)
    ),
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(-1 / 16.0, 16 / 16.0, 13 / 16.0),
          new Vector3(7 / 16.0, 16 / 16.0, 13 / 16.0),
          new Vector3(-1 / 16.0, 8 / 16.0, 13 / 16.0),
          new Vector4(12 / 16.0, 4 / 16.0, 8 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(7 / 16.0, 16 / 16.0, 13 / 16.0),
          new Vector3(-1 / 16.0, 16 / 16.0, 13 / 16.0),
          new Vector3(7 / 16.0, 8 / 16.0, 13 / 16.0),
          new Vector4(4 / 16.0, 12 / 16.0, 8 / 16.0, 0 / 16.0)
        )
      },
      Math.toRadians(-45),
      new Vector3(3, 0, 13)
    )
  );
  private static final Quad[] quadsEast = Model.rotateY(quadsNorth);
  private static final Quad[] quadsSouth = Model.rotateY(quadsEast);
  private static final Quad[] quadsWest = Model.rotateNegY(quadsNorth);

  private final Quad[] quads;
  private final Texture[] textures;

  public CalibratedSculkSensorModel(boolean active, String facing) {
    textures = new Texture[quadsNorth.length];
    if (facing.equals("east")) {
      quads = quadsEast;
    } else if (facing.equals("south")) {
      quads = quadsSouth;
    } else if (facing.equals("west")) {
      quads = quadsWest;
    } else {
      quads = quadsNorth;
    }
    System.arraycopy(tex, 0, textures, 0, tex.length);
    Arrays.fill(textures, 6, textures.length,
      active ? Texture.sculkSensorTendrilActive : Texture.sculkSensorTendrilInactive);
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
