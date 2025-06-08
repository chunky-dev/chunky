package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class RedstoneWallTorchModel extends QuadModel {
  private static final Quad[] wallTorchNorth = Model.join(
    Model.rotateZ(
      new Quad[]{
        new Quad(
          new Vector3(-1 / 16.0, 13.5 / 16.0, 9 / 16.0),
          new Vector3(1 / 16.0, 13.5 / 16.0, 9 / 16.0),
          new Vector3(-1 / 16.0, 13.5 / 16.0, 7 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 8 / 16.0, 10 / 16.0)
        ),
        new Quad(
          new Vector3(-1 / 16.0, 3.5 / 16.0, 7 / 16.0),
          new Vector3(1 / 16.0, 3.5 / 16.0, 7 / 16.0),
          new Vector3(-1 / 16.0, 3.5 / 16.0, 9 / 16.0),
          new Vector4(7 / 16.0, 9 / 16.0, 1 / 16.0, 3 / 16.0)
        ),
        new Quad(
          new Vector3(-1 / 16.0, 13.5 / 16.0, 9 / 16.0),
          new Vector3(-1 / 16.0, 13.5 / 16.0, 7 / 16.0),
          new Vector3(-1 / 16.0, 3.5 / 16.0, 9 / 16.0),
          new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(1 / 16.0, 13.5 / 16.0, 7 / 16.0),
          new Vector3(1 / 16.0, 13.5 / 16.0, 9 / 16.0),
          new Vector3(1 / 16.0, 3.5 / 16.0, 7 / 16.0),
          new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(-1 / 16.0, 13.5 / 16.0, 7 / 16.0),
          new Vector3(1 / 16.0, 13.5 / 16.0, 7 / 16.0),
          new Vector3(-1 / 16.0, 3.5 / 16.0, 7 / 16.0),
          new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(1 / 16.0, 13.5 / 16.0, 9 / 16.0),
          new Vector3(-1 / 16.0, 13.5 / 16.0, 9 / 16.0),
          new Vector3(1 / 16.0, 3.5 / 16.0, 9 / 16.0),
          new Vector4(9 / 16.0, 7 / 16.0, 10 / 16.0, 0 / 16.0)
        ),
        new RedstoneTorchModel.GlowQuad(
          new Vector3(-1.5 / 16.0, 11 / 16.0, 9.5 / 16.0),
          new Vector3(1.5 / 16.0, 11 / 16.0, 9.5 / 16.0),
          new Vector3(-1.5 / 16.0, 11 / 16.0, 6.5 / 16.0),
          new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
        ),
        new RedstoneTorchModel.GlowQuad(
          new Vector3(-1.5 / 16.0, 14 / 16.0, 6.5 / 16.0),
          new Vector3(1.5 / 16.0, 14 / 16.0, 6.5 / 16.0),
          new Vector3(-1.5 / 16.0, 14 / 16.0, 9.5 / 16.0),
          new Vector4(6 / 16.0, 7 / 16.0, 10 / 16.0, 11 / 16.0)
        ),
        new RedstoneTorchModel.GlowQuad(
          new Vector3(1.5 / 16.0, 14 / 16.0, 6.5 / 16.0),
          new Vector3(-1.5 / 16.0, 14 / 16.0, 6.5 / 16.0),
          new Vector3(1.5 / 16.0, 11 / 16.0, 6.5 / 16.0),
          new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
        ),
        new RedstoneTorchModel.GlowQuad(
          new Vector3(1.5 / 16.0, 14 / 16.0, 9.5 / 16.0),
          new Vector3(1.5 / 16.0, 14 / 16.0, 6.5 / 16.0),
          new Vector3(1.5 / 16.0, 11 / 16.0, 9.5 / 16.0),
          new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
        ),
        new RedstoneTorchModel.GlowQuad(
          new Vector3(-1.5 / 16.0, 14 / 16.0, 9.5 / 16.0),
          new Vector3(1.5 / 16.0, 14 / 16.0, 9.5 / 16.0),
          new Vector3(-1.5 / 16.0, 11 / 16.0, 9.5 / 16.0),
          new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
        ),
        new RedstoneTorchModel.GlowQuad(
          new Vector3(-1.5 / 16.0, 14 / 16.0, 6.5 / 16.0),
          new Vector3(-1.5 / 16.0, 14 / 16.0, 9.5 / 16.0),
          new Vector3(-1.5 / 16.0, 11 / 16.0, 6.5 / 16.0),
          new Vector4(7 / 16.0, 6 / 16.0, 11 / 16.0, 10 / 16.0)
        )
      },
      Math.toRadians(-22.5),
      new Vector3(0, 3.5 / 16., 0)
    )
  );

  private final Texture[] textures;
  private Quad[] quads;

  public RedstoneWallTorchModel(boolean isLit, String facing) {
    textures = new Texture[wallTorchNorth.length];
    quads = wallTorchNorth;
    switch (facing) {
      case "east":
        quads = Model.rotateNegY(quads);
        break;
      case "south":
        quads = Model.rotateY(quads, Math.toRadians(180));
        break;
      case "west":
        quads = Model.rotateY(quads);
        break;
    }
    Arrays.fill(this.textures, isLit ? Texture.redstoneTorchOn : Texture.redstoneTorchOff);
  }

  @Override
  public Quad[] getQuads() {
    return wallTorchNorth;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return RedstoneTorchModel.intersectWithGlow(ray, scene, this);
  }
}
