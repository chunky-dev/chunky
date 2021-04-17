package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class AzaleaModel extends QuadModel {

  //region Azalea Model
  private static final Quad[] quads = Model.join(
      new Quad[]{
          // top
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          // side
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 5 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 16 / 16.0, 0.01 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 0.01 / 16.0),
              new Vector3(16 / 16.0, 5 / 16.0, 0.01 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 15.99 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 15.99 / 16.0),
              new Vector3(0 / 16.0, 5 / 16.0, 15.99 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 5 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0 / 16.0, 5 / 16.0, 16 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(0.01 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(0.01 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(0.01 / 16.0, 5 / 16.0, 0 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(15.99 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(15.99 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(15.99 / 16.0, 5 / 16.0, 16 / 16.0),
              new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
          new Quad(
              new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
              new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
              new Vector3(16 / 16.0, 5 / 16.0, 0 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 5 / 16.0)
          ),
      },
      // plant
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(0.1 / 16.0, 15.9 / 16.0, 8 / 16.0),
              new Vector3(15.9 / 16.0, 15.9 / 16.0, 8 / 16.0),
              new Vector3(0.1 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(15.9 / 16.0, 15.9 / 16.0, 8 / 16.0),
              new Vector3(0.1 / 16.0, 15.9 / 16.0, 8 / 16.0),
              new Vector3(15.9 / 16.0, 0 / 16.0, 8 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
      }, Math.toRadians(45)),
      Model.rotateY(new Quad[]{
          new Quad(
              new Vector3(8 / 16.0, 15.9 / 16.0, 15.9 / 16.0),
              new Vector3(8 / 16.0, 15.9 / 16.0, 0.1 / 16.0),
              new Vector3(8 / 16.0, 0 / 16.0, 15.9 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          ),
          new Quad(
              new Vector3(8 / 16.0, 15.9 / 16.0, 0.1 / 16.0),
              new Vector3(8 / 16.0, 15.9 / 16.0, 15.9 / 16.0),
              new Vector3(8 / 16.0, 0 / 16.0, 0.1 / 16.0),
              new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
          )
      }, Math.toRadians(45))
  );
  //endregion

  private final Texture[] textures;

  public AzaleaModel(Texture top, Texture side) {
    Texture plant = Texture.azaleaPlant;
    textures = new Texture[14];
    for (int i = 0; i < 2; i++) textures[i] = top;
    for (int i = 2; i < 10; i++) textures[i] = side;
    for (int i = 10; i < 14; i++) textures[i] = plant;
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
