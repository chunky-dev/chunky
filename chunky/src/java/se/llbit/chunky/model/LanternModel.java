package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class LanternModel extends QuadModel {

  //region Model
  private static final Quad[] model =
      new Quad[]{
          new Quad(
              new Vector3(5 / 16.0, 7 / 16.0, 11 / 16.0),
              new Vector3(11 / 16.0, 7 / 16.0, 11 / 16.0),
              new Vector3(5 / 16.0, 7 / 16.0, 5 / 16.0),
              new Vector4(0 / 16.0, 6 / 16.0, 1 / 16.0, 7 / 16.0)),
          new Quad(
              new Vector3(5 / 16.0, 0 / 16.0, 5 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 5 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 11 / 16.0),
              new Vector4(0 / 16.0, 6 / 16.0, 1 / 16.0, 7 / 16.0)),
          new Quad(
              new Vector3(5 / 16.0, 7 / 16.0, 11 / 16.0),
              new Vector3(5 / 16.0, 7 / 16.0, 5 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 11 / 16.0),
              new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
          new Quad(
              new Vector3(11 / 16.0, 7 / 16.0, 5 / 16.0),
              new Vector3(11 / 16.0, 7 / 16.0, 11 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 5 / 16.0),
              new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
          new Quad(
              new Vector3(5 / 16.0, 7 / 16.0, 5 / 16.0),
              new Vector3(11 / 16.0, 7 / 16.0, 5 / 16.0),
              new Vector3(5 / 16.0, 0 / 16.0, 5 / 16.0),
              new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
          new Quad(
              new Vector3(11 / 16.0, 7 / 16.0, 11 / 16.0),
              new Vector3(5 / 16.0, 7 / 16.0, 11 / 16.0),
              new Vector3(11 / 16.0, 0 / 16.0, 11 / 16.0),
              new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 10 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 10 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 6 / 16.0),
              new Vector4(1 / 16.0, 5 / 16.0, 2 / 16.0, 6 / 16.0)),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 10 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 6 / 16.0),
              new Vector3(6 / 16.0, 7 / 16.0, 10 / 16.0),
              new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
          new Quad(
              new Vector3(10 / 16.0, 9 / 16.0, 6 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 10 / 16.0),
              new Vector3(10 / 16.0, 7 / 16.0, 6 / 16.0),
              new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
          new Quad(
              new Vector3(6 / 16.0, 9 / 16.0, 6 / 16.0),
              new Vector3(10 / 16.0, 9 / 16.0, 6 / 16.0),
              new Vector3(6 / 16.0, 7 / 16.0, 6 / 16.0),
              new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
          new Quad(
              new Vector3(10 / 16.0, 9 / 16.0, 10 / 16.0),
              new Vector3(6 / 16.0, 9 / 16.0, 10 / 16.0),
              new Vector3(10 / 16.0, 7 / 16.0, 10 / 16.0),
              new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
          new Quad(
              new Vector3(6.5 / 16.0, 11 / 16.0, 8 / 16.0),
              new Vector3(9.5 / 16.0, 11 / 16.0, 8 / 16.0),
              new Vector3(6.5 / 16.0, 9 / 16.0, 8 / 16.0),
              new Vector4(11 / 16.0, 14 / 16.0, 15 / 16.0, 13 / 16.0)
          ),
          new Quad(
              new Vector3(9.5 / 16.0, 11 / 16.0, 8 / 16.0),
              new Vector3(6.5 / 16.0, 11 / 16.0, 8 / 16.0),
              new Vector3(9.5 / 16.0, 9 / 16.0, 8 / 16.0),
              new Vector4(14 / 16.0, 11 / 16.0, 15 / 16.0, 13 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 11 / 16.0, 9.5 / 16.0),
              new Vector3(8 / 16.0, 11 / 16.0, 6.5 / 16.0),
              new Vector3(8 / 16.0, 9 / 16.0, 9.5 / 16.0),
              new Vector4(11 / 16.0, 14 / 16.0, 6 / 16.0, 4 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 11 / 16.0, 6.5 / 16.0),
              new Vector3(8 / 16.0, 11 / 16.0, 9.5 / 16.0),
              new Vector3(8 / 16.0, 9 / 16.0, 6.5 / 16.0),
              new Vector4(14 / 16.0, 11 / 16.0, 6 / 16.0, 4 / 16.0)
          )
      };
  //endregion

  //region Hanging
  private static final Quad[] modelHanging =
      Model.join(
          new Quad[]{
              new Quad(
                  new Vector3(5 / 16.0, 8 / 16.0, 11 / 16.0),
                  new Vector3(11 / 16.0, 8 / 16.0, 11 / 16.0),
                  new Vector3(5 / 16.0, 8 / 16.0, 5 / 16.0),
                  new Vector4(0 / 16.0, 6 / 16.0, 1 / 16.0, 7 / 16.0)),
              new Quad(
                  new Vector3(5 / 16.0, 1 / 16.0, 5 / 16.0),
                  new Vector3(11 / 16.0, 1 / 16.0, 5 / 16.0),
                  new Vector3(5 / 16.0, 1 / 16.0, 11 / 16.0),
                  new Vector4(0 / 16.0, 6 / 16.0, 1 / 16.0, 7 / 16.0)),
              new Quad(
                  new Vector3(5 / 16.0, 8 / 16.0, 11 / 16.0),
                  new Vector3(5 / 16.0, 8 / 16.0, 5 / 16.0),
                  new Vector3(5 / 16.0, 1 / 16.0, 11 / 16.0),
                  new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
              new Quad(
                  new Vector3(11 / 16.0, 8 / 16.0, 5 / 16.0),
                  new Vector3(11 / 16.0, 8 / 16.0, 11 / 16.0),
                  new Vector3(11 / 16.0, 1 / 16.0, 5 / 16.0),
                  new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
              new Quad(
                  new Vector3(5 / 16.0, 8 / 16.0, 5 / 16.0),
                  new Vector3(11 / 16.0, 8 / 16.0, 5 / 16.0),
                  new Vector3(5 / 16.0, 1 / 16.0, 5 / 16.0),
                  new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
              new Quad(
                  new Vector3(11 / 16.0, 8 / 16.0, 11 / 16.0),
                  new Vector3(5 / 16.0, 8 / 16.0, 11 / 16.0),
                  new Vector3(11 / 16.0, 1 / 16.0, 11 / 16.0),
                  new Vector4(6 / 16.0, 0 / 16.0, 14 / 16.0, 7 / 16.0)),
              new Quad(
                  new Vector3(6 / 16.0, 10 / 16.0, 10 / 16.0),
                  new Vector3(10 / 16.0, 10 / 16.0, 10 / 16.0),
                  new Vector3(6 / 16.0, 10 / 16.0, 6 / 16.0),
                  new Vector4(1 / 16.0, 5 / 16.0, 2 / 16.0, 6 / 16.0)),
              new Quad(
                  new Vector3(6 / 16.0, 8 / 16.0, 6 / 16.0),
                  new Vector3(10 / 16.0, 8 / 16.0, 6 / 16.0),
                  new Vector3(6 / 16.0, 8 / 16.0, 10 / 16.0),
                  new Vector4(1 / 16.0, 5 / 16.0, 2 / 16.0, 6 / 16.0)),
              new Quad(
                  new Vector3(6 / 16.0, 10 / 16.0, 10 / 16.0),
                  new Vector3(6 / 16.0, 10 / 16.0, 6 / 16.0),
                  new Vector3(6 / 16.0, 8 / 16.0, 10 / 16.0),
                  new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
              new Quad(
                  new Vector3(10 / 16.0, 10 / 16.0, 6 / 16.0),
                  new Vector3(10 / 16.0, 10 / 16.0, 10 / 16.0),
                  new Vector3(10 / 16.0, 8 / 16.0, 6 / 16.0),
                  new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
              new Quad(
                  new Vector3(6 / 16.0, 10 / 16.0, 6 / 16.0),
                  new Vector3(10 / 16.0, 10 / 16.0, 6 / 16.0),
                  new Vector3(6 / 16.0, 8 / 16.0, 6 / 16.0),
                  new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
              new Quad(
                  new Vector3(10 / 16.0, 10 / 16.0, 10 / 16.0),
                  new Vector3(6 / 16.0, 10 / 16.0, 10 / 16.0),
                  new Vector3(10 / 16.0, 8 / 16.0, 10 / 16.0),
                  new Vector4(5 / 16.0, 1 / 16.0, 16 / 16.0, 14 / 16.0)),
          },
          Model.rotateY(
              new Quad[]{
                  new Quad(
                      new Vector3(6.5 / 16.0, 15 / 16.0, 8 / 16.0),
                      new Vector3(9.5 / 16.0, 15 / 16.0, 8 / 16.0),
                      new Vector3(6.5 / 16.0, 11 / 16.0, 8 / 16.0),
                      new Vector4(11 / 16.0, 14 / 16.0, 15 / 16.0, 11 / 16.0)
                  ),
                  new Quad(
                      new Vector3(9.5 / 16.0, 15 / 16.0, 8 / 16.0),
                      new Vector3(6.5 / 16.0, 15 / 16.0, 8 / 16.0),
                      new Vector3(9.5 / 16.0, 11 / 16.0, 8 / 16.0),
                      new Vector4(14 / 16.0, 11 / 16.0, 15 / 16.0, 11 / 16.0)
                  ),
                  new Quad(
                      new Vector3(8 / 16.0, 16 / 16.0, 9.5 / 16.0),
                      new Vector3(8 / 16.0, 16 / 16.0, 6.5 / 16.0),
                      new Vector3(8 / 16.0, 10 / 16.0, 9.5 / 16.0),
                      new Vector4(11 / 16.0, 14 / 16.0, 10 / 16.0, 4 / 16.0)
                  ),
                  new Quad(
                      new Vector3(8 / 16.0, 16 / 16.0, 6.5 / 16.0),
                      new Vector3(8 / 16.0, 16 / 16.0, 9.5 / 16.0),
                      new Vector3(8 / 16.0, 10 / 16.0, 6.5 / 16.0),
                      new Vector4(14 / 16.0, 11 / 16.0, 10 / 16.0, 4 / 16.0)
                  )
              },
              Math.toRadians(45)));
  //endregion

  private final Quad[] quads;
  private final Texture[] textures;

  public LanternModel(Texture texture, boolean hanging) {
    quads = hanging ? modelHanging : model;
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
}
