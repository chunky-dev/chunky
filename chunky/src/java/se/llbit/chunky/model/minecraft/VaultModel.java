package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class VaultModel extends QuadModel {
  private static final Quad[] quadsNorth = new Quad[]{
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(0 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(16 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(0 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(16 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(0 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(16 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(15.998 / 16.0, 15.998 / 16.0, 15.998 / 16.0),
      new Vector3(0.002 / 16.0, 15.998 / 16.0, 15.998 / 16.0),
      new Vector3(15.998 / 16.0, 15.998 / 16.0, 0.002 / 16.0),
      new Vector4(16 / 16.0, 0 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(15.998 / 16.0, 3.002 / 16.0, 0.002 / 16.0),
      new Vector3(0.002 / 16.0, 3.002 / 16.0, 0.002 / 16.0),
      new Vector3(15.998 / 16.0, 3.002 / 16.0, 15.998 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(15.998 / 16.0, 15.998 / 16.0, 15.998 / 16.0),
      new Vector3(15.998 / 16.0, 15.998 / 16.0, 0.002 / 16.0),
      new Vector3(15.998 / 16.0, 3.002 / 16.0, 15.998 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(0.002 / 16.0, 15.998 / 16.0, 0.002 / 16.0),
      new Vector3(0.002 / 16.0, 15.998 / 16.0, 15.998 / 16.0),
      new Vector3(0.002 / 16.0, 3.002 / 16.0, 0.002 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(15.998 / 16.0, 15.998 / 16.0, 0.002 / 16.0),
      new Vector3(0.002 / 16.0, 15.998 / 16.0, 0.002 / 16.0),
      new Vector3(15.998 / 16.0, 3.002 / 16.0, 0.002 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 3 / 16.0)
    ),
    new Quad(
      new Vector3(0.002 / 16.0, 15.998 / 16.0, 15.998 / 16.0),
      new Vector3(15.998 / 16.0, 15.998 / 16.0, 15.998 / 16.0),
      new Vector3(0.002 / 16.0, 3.002 / 16.0, 15.998 / 16.0),
      new Vector4(0 / 16.0, 16 / 16.0, 16 / 16.0, 3 / 16.0)
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

  public VaultModel(
    String facing, boolean ominous, String vaultState
  ) {
    quads = orientedQuads[getOrientationIndex(facing)];
    Texture top = getTopTexture(ominous, vaultState);
    Texture bottom = ominous ? Texture.vaultBottomOminous : Texture.vaultBottom;
    Texture front, side;
    switch (vaultState) {
      case "ejecting":
      case "unlocking":
        front = ominous ? Texture.vaultFrontEjectingOminous : Texture.vaultFrontEjecting;
        side = ominous ? Texture.vaultSideOnOminous : Texture.vaultSideOn;
        break;
      case "inactive":
        front = ominous ? Texture.vaultFrontOffOminous : Texture.vaultFrontOff;
        side = ominous ? Texture.vaultSideOffOminous : Texture.vaultSideOff;
        break;
      case "active":
      default:
        front = ominous ? Texture.vaultFrontOnOminous : Texture.vaultFrontOn;
        side = ominous ? Texture.vaultSideOnOminous : Texture.vaultSideOn;
        break;
    }
    textures = new Texture[]{
      top, bottom, side, side, front, side, top, bottom, side, side, front, side
    };
  }

  public static Texture getTopTexture(boolean ominous, String vaultState) {
    switch (vaultState) {
      case "ejecting":
        return ominous ? Texture.vaultTopEjectingOminous : Texture.vaultTopEjecting;
      case "inactive":
      case "active":
      case "unlocking":
      default:
        return ominous ? Texture.vaultTopOminous : Texture.vaultTop;
    }
  }

  private static int getOrientationIndex(String facing) {
    switch (facing) {
      case "east":
        return 1;
      case "south":
        return 2;
      case "west":
        return 3;
      case "north":
      default:
        return 0;
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
