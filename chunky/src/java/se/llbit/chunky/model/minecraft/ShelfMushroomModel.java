package se.llbit.chunky.model.minecraft;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Arrays;

public class ShelfMushroomModel extends QuadModel {
  private static final Quad[][] mushroom = new Quad[][] {
    // age 0
    {
      // top
      new Quad(
        new Vector3(13 / 16.0, 9 / 16.0, 9 / 16.0),
        new Vector3(3 / 16.0, 9 / 16.0, 9 / 16.0),
        new Vector3(13 / 16.0, 11 / 16.0, 9 / 16.0),
        new Vector4(5 / 16.0, 10 / 16.0, 13 / 16.0, 14 / 16.0)
      ),
      new Quad(
        new Vector3(13 / 16.0, 9 / 16.0, 16 / 16.0),
        new Vector3(13 / 16.0, 9 / 16.0, 9 / 16.0),
        new Vector3(13 / 16.0, 11 / 16.0, 16 / 16.0),
        new Vector4(5 / 16.0, 8.5 / 16.0, 15 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(3 / 16.0, 9 / 16.0, 16 / 16.0),
        new Vector3(13 / 16.0, 9 / 16.0, 16 / 16.0),
        new Vector3(3 / 16.0, 11 / 16.0, 16 / 16.0),
        new Vector4(5 / 16.0, 10 / 16.0, 12 / 16.0, 13 / 16.0)
      ),
      new Quad(
        new Vector3(3 / 16.0, 9 / 16.0, 9 / 16.0),
        new Vector3(3 / 16.0, 9 / 16.0, 16 / 16.0),
        new Vector3(3 / 16.0, 11 / 16.0, 9 / 16.0),
        new Vector4(5 / 16.0, 8.5 / 16.0, 14 / 16.0, 15 / 16.0)
      ),
      new Quad(
        new Vector3(13 / 16.0, 11 / 16.0, 9 / 16.0),
        new Vector3(3 / 16.0, 11 / 16.0, 9 / 16.0),
        new Vector3(13 / 16.0, 11 / 16.0, 16 / 16.0),
        new Vector4(0 / 16.0, 5 / 16.0, 12.5 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(3 / 16.0, 9 / 16.0, 9 / 16.0),
        new Vector3(13 / 16.0, 9 / 16.0, 9 / 16.0),
        new Vector3(3 / 16.0, 9 / 16.0, 16 / 16.0),
        new Vector4(5 / 16.0, 0 / 16.0, 9 / 16.0, 12.5 / 16.0)
      ),
      // bottom
      new Quad(
        new Vector3(11 / 16.0, 8 / 16.0, 12 / 16.0),
        new Vector3(5 / 16.0, 8 / 16.0, 12 / 16.0),
        new Vector3(11 / 16.0, 9 / 16.0, 12 / 16.0),
        new Vector4(5 / 16.0, 8 / 16.0, 10.5 / 16.0, 11 / 16.0)
      ),
      new Quad(
        new Vector3(11 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(11 / 16.0, 8 / 16.0, 12 / 16.0),
        new Vector3(11 / 16.0, 9 / 16.0, 16 / 16.0),
        new Vector4(5 / 16.0, 7 / 16.0, 11.5 / 16.0, 12 / 16.0)
      ),
      new Quad(
        new Vector3(5 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(11 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(5 / 16.0, 9 / 16.0, 16 / 16.0),
        new Vector4(5 / 16.0, 8 / 16.0, 10 / 16.0, 10.5 / 16.0)
      ),
      new Quad(
        new Vector3(5 / 16.0, 8 / 16.0, 12 / 16.0),
        new Vector3(5 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(5 / 16.0, 9 / 16.0, 12 / 16.0),
        new Vector4(5 / 16.0, 7 / 16.0, 11 / 16.0, 11.5 / 16.0)
      ),
      new Quad(
        new Vector3(11 / 16.0, 9 / 16.0, 12 / 16.0),
        new Vector3(5 / 16.0, 9 / 16.0, 12 / 16.0),
        new Vector3(11 / 16.0, 9 / 16.0, 16 / 16.0),
        new Vector4(0 / 16.0, 3 / 16.0, 7 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(5 / 16.0, 8 / 16.0, 12 / 16.0),
        new Vector3(11 / 16.0, 8 / 16.0, 12 / 16.0),
        new Vector3(5 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector4(3 / 16.0, 0 / 16.0, 5 / 16.0, 7 / 16.0)
      )
    },
    // age 1
    {
      // top
      new Quad(
        new Vector3(15 / 16.0, 8 / 16.0, 6 / 16.0),
        new Vector3(1 / 16.0, 8 / 16.0, 6 / 16.0),
        new Vector3(15 / 16.0, 11 / 16.0, 6 / 16.0),
        new Vector4(7 / 16.0, 14 / 16.0, 11.5 / 16.0, 13 / 16.0)
      ),
      new Quad(
        new Vector3(15 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(15 / 16.0, 8 / 16.0, 6 / 16.0),
        new Vector3(15 / 16.0, 11 / 16.0, 16 / 16.0),
        new Vector4(7 / 16.0, 12 / 16.0, 14.5 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(1 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(15 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(1 / 16.0, 11 / 16.0, 16 / 16.0),
        new Vector4(7 / 16.0, 14 / 16.0, 10 / 16.0, 11.5 / 16.0)
      ),
      new Quad(
        new Vector3(1 / 16.0, 8 / 16.0, 6 / 16.0),
        new Vector3(1 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector3(1 / 16.0, 11 / 16.0, 6 / 16.0),
        new Vector4(7 / 16.0, 12 / 16.0, 13 / 16.0, 14.5 / 16.0)
      ),
      new Quad(
        new Vector3(15 / 16.0, 11 / 16.0, 6 / 16.0),
        new Vector3(1 / 16.0, 11 / 16.0, 6 / 16.0),
        new Vector3(15 / 16.0, 11 / 16.0, 16 / 16.0),
        new Vector4(0 / 16.0, 7 / 16.0, 11 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(1 / 16.0, 8 / 16.0, 6 / 16.0),
        new Vector3(15 / 16.0, 8 / 16.0, 6 / 16.0),
        new Vector3(1 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector4(7 / 16.0, 0 / 16.0, 6 / 16.0, 11 / 16.0)
      ),
      // bottom
      new Quad(
        new Vector3(12 / 16.0, 6 / 16.0, 10 / 16.0),
        new Vector3(4 / 16.0, 6 / 16.0, 10 / 16.0),
        new Vector3(12 / 16.0, 8 / 16.0, 10 / 16.0),
        new Vector4(4 / 16.0, 8 / 16.0, 3 / 16.0, 4 / 16.0)
      ),
      new Quad(
        new Vector3(12 / 16.0, 6 / 16.0, 16 / 16.0),
        new Vector3(12 / 16.0, 6 / 16.0, 10 / 16.0),
        new Vector3(12 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector4(4 / 16.0, 7 / 16.0, 5 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(4 / 16.0, 6 / 16.0, 16 / 16.0),
        new Vector3(12 / 16.0, 6 / 16.0, 16 / 16.0),
        new Vector3(4 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector4(4 / 16.0, 8 / 16.0, 2 / 16.0, 3 / 16.0)
      ),
      new Quad(
        new Vector3(4 / 16.0, 6 / 16.0, 10 / 16.0),
        new Vector3(4 / 16.0, 6 / 16.0, 16 / 16.0),
        new Vector3(4 / 16.0, 8 / 16.0, 10 / 16.0),
        new Vector4(4 / 16.0, 7 / 16.0, 4 / 16.0, 5 / 16.0)
      ),
      new Quad(
        new Vector3(12 / 16.0, 8 / 16.0, 10 / 16.0),
        new Vector3(4 / 16.0, 8 / 16.0, 10 / 16.0),
        new Vector3(12 / 16.0, 8 / 16.0, 16 / 16.0),
        new Vector4(0 / 16.0, 4 / 16.0, 3 / 16.0, 6 / 16.0)
      ),
      new Quad(
        new Vector3(4 / 16.0, 6 / 16.0, 10 / 16.0),
        new Vector3(12 / 16.0, 6 / 16.0, 10 / 16.0),
        new Vector3(4 / 16.0, 6 / 16.0, 16 / 16.0),
        new Vector4(4 / 16.0, 0 / 16.0, 0 / 16.0, 3 / 16.0)
      )
    }
  };

  private final Quad[] quads;
  private final Texture[] textures;

  public ShelfMushroomModel(Texture texture, String facing, int age) {
    switch (facing) {
      case "east" -> quads = Model.rotateY(mushroom[age], FastMath.toRadians(-90));
      case "south" -> quads = Model.rotateY(mushroom[age], FastMath.toRadians(180));
      case "west" -> quads = Model.rotateY(mushroom[age], FastMath.toRadians(-270));
      default -> quads = mushroom[age];
    }

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
