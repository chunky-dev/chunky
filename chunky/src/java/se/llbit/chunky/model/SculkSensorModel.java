package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class SculkSensorModel extends QuadModel {

  private static final Texture bottom = Texture.sculkSensorBottom;
  private static final Texture side = Texture.sculkSensorSide;
  private static final Texture top = Texture.sculkSensorTop;
  private static final Texture[] tex = new Texture[]{
      top, bottom, side, side, side, side
  };

  //region Model
  private static final Quad[] quads = Model.join(
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
      Model.rotateY(new Quad[]{
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
          ),
      }, Math.toRadians(45), new Vector3(3 / 16.0, 0, 3 / 16.0)),
      Model.rotateY(new Quad[]{
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
          ),
      }, Math.toRadians(-45), new Vector3(13 / 16.0, 0, 3 / 16.0)),
      Model.rotateY(new Quad[]{
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
          ),
      }, Math.toRadians(45), new Vector3(13 / 16.0, 0, 13 / 16.0)),
      Model.rotateY(new Quad[]{
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
      }, Math.toRadians(-45), new Vector3(3 / 16.0, 0, 13 / 16.0))
  );
  //endregion

  private final Texture[] textures;

  public SculkSensorModel(boolean active) {
    textures = new Texture[quads.length];
    System.arraycopy(tex, 0, textures, 0, tex.length);
    Arrays.fill(textures, tex.length, textures.length,
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
