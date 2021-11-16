package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class LightningRodModel extends QuadModel {

  //region Model
  private static final Quad[] lightningRod = new Quad[] {
      new Quad(
          new Vector3(6 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(10 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(6 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(6 / 16.0, 12 / 16.0, 6 / 16.0),
          new Vector3(10 / 16.0, 12 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 12 / 16.0, 10 / 16.0),
          new Vector4(0 / 16.0, 4 / 16.0, 12 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(6 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(6 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 12 / 16.0, 10 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(10 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(10 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(10 / 16.0, 12 / 16.0, 6 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(6 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(10 / 16.0, 16 / 16.0, 6 / 16.0),
          new Vector3(6 / 16.0, 12 / 16.0, 6 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(10 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(6 / 16.0, 16 / 16.0, 10 / 16.0),
          new Vector3(10 / 16.0, 12 / 16.0, 10 / 16.0),
          new Vector4(4 / 16.0, 0 / 16.0, 16 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(0 / 16.0, 2 / 16.0, 10 / 16.0, 12 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(7 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(9 / 16.0, 12 / 16.0, 7 / 16.0),
          new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
      ),
      new Quad(
          new Vector3(9 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(7 / 16.0, 12 / 16.0, 9 / 16.0),
          new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
          new Vector4(2 / 16.0, 0 / 16.0, 12 / 16.0, 0 / 16.0)
      )
  };
  //endregion

  static final Quad[][] orientedQuads = new Quad[6][];

  static {
    orientedQuads[4] = lightningRod;
    orientedQuads[0] = Model.rotateX(Model.rotateX(lightningRod));
    orientedQuads[2] = Model.rotateNegX(lightningRod);
    orientedQuads[1] = Model.rotateY(orientedQuads[2]);
    orientedQuads[3] = Model.rotateY(orientedQuads[1]);
    orientedQuads[5] = Model.rotateY(orientedQuads[3]);
  }

  private final static Texture[] texturesOn = new Texture[lightningRod.length];
  static { Arrays.fill(texturesOn, Texture.lightningRodOn); }

  private final static Texture[] texturesOff = new Texture[lightningRod.length];
  static { Arrays.fill(texturesOff, Texture.lightningRod); }

  private final Quad[] quads;
  private final Texture[] textures;

  public LightningRodModel(String facing, boolean powered) {
    this.quads = orientedQuads[getOrientationIndex(facing)];
    this.textures = powered ? texturesOn : texturesOff;
  }

  private static int getOrientationIndex(String facing) {
    switch (facing) {
      case "down":
        return 0;
      case "east":
        return 1;
      case "north":
        return 2;
      case "south":
        return 3;
      case "west":
        return 5;
      case "up":
      default:
        return 4;
    }
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
