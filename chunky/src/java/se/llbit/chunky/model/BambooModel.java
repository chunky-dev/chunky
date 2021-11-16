package se.llbit.chunky.model;

import java.util.Arrays;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class BambooModel extends QuadModel {

  private static final Texture[] tex = new Texture[]{
      Texture.bambooStalk, Texture.bambooStalk, Texture.bambooStalk, Texture.bambooStalk,
      Texture.bambooStalk, Texture.bambooStalk
  };

  //region Bamboo Stem
  private static final Quad[][] stemQuads = new Quad[][]{
      // age = 0
      new Quad[]{
          new Quad(
              new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
              new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
              new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 1 - 0 / 16.0, 1 - 2 / 16.0)
          ),
          new Quad(
              new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
              new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
              new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
              new Vector4(13 / 16.0, 15 / 16.0, 4 / 16.0, 6 / 16.0)
          ),
          new Quad(
              new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
              new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
              new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
              new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
              new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
              new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
              new Vector3(9 / 16.0, 16 / 16.0, 7 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
              new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
              new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
              new Vector4(0 / 16.0, 2 / 16.0, 0 / 16.0, 16 / 16.0)
          )
      },

      // age=1
      new Quad[]{
          new Quad(
              new Vector3(6.5 / 16.0, 16 / 16.0, 9.5 / 16.0),
              new Vector3(9.5 / 16.0, 16 / 16.0, 9.5 / 16.0),
              new Vector3(6.5 / 16.0, 16 / 16.0, 6.5 / 16.0),
              new Vector4(13 / 16.0, 16 / 16.0, 1 - 0 / 16.0, 1 - 3 / 16.0)
          ),
          new Quad(
              new Vector3(6.5 / 16.0, 0 / 16.0, 6.5 / 16.0),
              new Vector3(9.5 / 16.0, 0 / 16.0, 6.5 / 16.0),
              new Vector3(6.5 / 16.0, 0 / 16.0, 9.5 / 16.0),
              new Vector4(13 / 16.0, 16 / 16.0, 4 / 16.0, 7 / 16.0)
          ),
          new Quad(
              new Vector3(6.5 / 16.0, 0 / 16.0, 6.5 / 16.0),
              new Vector3(6.5 / 16.0, 0 / 16.0, 9.5 / 16.0),
              new Vector3(6.5 / 16.0, 16 / 16.0, 6.5 / 16.0),
              new Vector4(0 / 16.0, 3 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(9.5 / 16.0, 0 / 16.0, 9.5 / 16.0),
              new Vector3(9.5 / 16.0, 0 / 16.0, 6.5 / 16.0),
              new Vector3(9.5 / 16.0, 16 / 16.0, 9.5 / 16.0),
              new Vector4(0 / 16.0, 3 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(9.5 / 16.0, 0 / 16.0, 6.5 / 16.0),
              new Vector3(6.5 / 16.0, 0 / 16.0, 6.5 / 16.0),
              new Vector3(9.5 / 16.0, 16 / 16.0, 6.5 / 16.0),
              new Vector4(0 / 16.0, 3 / 16.0, 0 / 16.0, 16 / 16.0)
          ),
          new Quad(
              new Vector3(6.5 / 16.0, 0 / 16.0, 9.5 / 16.0),
              new Vector3(9.5 / 16.0, 0 / 16.0, 9.5 / 16.0),
              new Vector3(6.5 / 16.0, 16 / 16.0, 9.5 / 16.0),
              new Vector4(0 / 16.0, 3 / 16.0, 0 / 16.0, 16 / 16.0)
          )
      }
  };
  //endregion

  private static final Texture[] smallLeavesTex = new Texture[]{
      Texture.bambooSmallLeaves, Texture.bambooSmallLeaves, Texture.bambooSmallLeaves,
      Texture.bambooSmallLeaves,
  };

  //region Bamboo Small Leaves
  private static final Quad[] smallLeaves = new Quad[]{
      new Quad(
          new Vector3(15.2 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector3(0.8 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector3(15.2 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(0.8 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector3(15.2 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector3(0.8 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 0 / 16.0, 0.8 / 16.0),
          new Vector3(8 / 16.0, 0 / 16.0, 15.2 / 16.0),
          new Vector3(8 / 16.0, 16 / 16.0, 0.8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 0 / 16.0, 15.2 / 16.0),
          new Vector3(8 / 16.0, 0 / 16.0, 0.8 / 16.0),
          new Vector3(8 / 16.0, 16 / 16.0, 15.2 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      )
  };
  //endregion

  private static final Texture[] largeLeavesTex = new Texture[]{
      Texture.bambooLargeLeaves, Texture.bambooLargeLeaves, Texture.bambooLargeLeaves,
      Texture.bambooLargeLeaves
  };

  //region Bamboo Large Leaves
  private static final Quad[] largeLeaves = new Quad[]{
      new Quad(
          new Vector3(15.2 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector3(0.8 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector3(15.2 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(0.8 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector3(15.2 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector3(0.8 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 0 / 16.0, 0.8 / 16.0),
          new Vector3(8 / 16.0, 0 / 16.0, 15.2 / 16.0),
          new Vector3(8 / 16.0, 16 / 16.0, 0.8 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      ),
      new Quad(
          new Vector3(8 / 16.0, 0 / 16.0, 15.2 / 16.0),
          new Vector3(8 / 16.0, 0 / 16.0, 0.8 / 16.0),
          new Vector3(8 / 16.0, 16 / 16.0, 15.2 / 16.0),
          new Vector4(0 / 16.0, 16 / 16.0, 0 / 16.0, 16 / 16.0)
      )
  };
  //endregion

  private final Quad[] quads;
  private final Texture[] textures;

  public BambooModel(int age, String leaves) {
    if ("small".equals(leaves)) {
      this.quads = Model.join(
          stemQuads[age],
          smallLeaves
      );
      this.textures = joinTextures(
          tex,
          smallLeavesTex
      );
    } else if ("large".equals(leaves)) {
      this.quads = Model.join(
          stemQuads[age],
          largeLeaves
      );
      this.textures = joinTextures(
          tex,
          largeLeavesTex
      );
    } else {
      this.quads = stemQuads[age];
      this.textures = tex;
    }
  }

  private Texture[] joinTextures(Texture[]... textures) {
    int length = 0;
    for (Texture[] tex : textures) {
      length += tex.length;
    }
    Texture[] joined = Arrays.copyOf(textures[0], length);
    int offset = textures[0].length;
    for (int i = 1; i < textures.length; i++) {
      System.arraycopy(textures[i], 0, joined, offset, textures[i].length);
      offset += textures[i].length;
    }
    return joined;
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
