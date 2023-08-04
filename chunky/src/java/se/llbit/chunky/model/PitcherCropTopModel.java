package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class PitcherCropTopModel extends QuadModel {
  private static final Texture top = Texture.pitcherCropTop;
  private static final Texture side = Texture.pitcherCropSide;
  private static final Texture bottom = Texture.pitcherCropBottom;
  private static final Texture bottomStage2 = Texture.pitcherCropBottomStage2;
  private static final Texture topStage3 = Texture.pitcherCropTopStage3;
  private static final Texture topStage4 = Texture.pitcherCropTopStage4;

  private static final Texture[][] textures = new Texture[][]{
    null, null,
    new Texture[]{
      bottomStage2, bottomStage2, bottomStage2, bottomStage2, top, bottom, side, side, side, side
    },
    new Texture[]{
      topStage3, topStage3, topStage3, topStage3
    },
    new Texture[]{
      topStage4, topStage4, topStage4, topStage4
    }
  };

  private static final Quad[][] quads = new Quad[][]{
    null, null,
    Model.translate(Model.join(
      Model.rotateY(new Quad[]{
        new Quad(
          new Vector3(0 / 16.0, 21 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 21 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 5 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(16 / 16.0, 21 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 21 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 5 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(8 / 16.0, 21 / 16.0, 16 / 16.0),
          new Vector3(8 / 16.0, 21 / 16.0, 0 / 16.0),
          new Vector3(8 / 16.0, 5 / 16.0, 16 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(8 / 16.0, 21 / 16.0, 0 / 16.0),
          new Vector3(8 / 16.0, 21 / 16.0, 16 / 16.0),
          new Vector3(8 / 16.0, 5 / 16.0, 0 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        )
      }, Math.toRadians(45)),
      new Quad[]{
        new Quad(
          new Vector3(3 / 16.0, 5 / 16.0, 13 / 16.0),
          new Vector3(13 / 16.0, 5 / 16.0, 13 / 16.0),
          new Vector3(3 / 16.0, 5 / 16.0, 3 / 16.0),
          new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)
        ),
        new Quad(
          new Vector3(3 / 16.0, -1 / 16.0, 3 / 16.0),
          new Vector3(13 / 16.0, -1 / 16.0, 3 / 16.0),
          new Vector3(3 / 16.0, -1 / 16.0, 13 / 16.0),
          new Vector4(3 / 16.0, 13 / 16.0, 3 / 16.0, 13 / 16.0)
        ),
        new Quad(
          new Vector3(3 / 16.0, 5 / 16.0, 13 / 16.0),
          new Vector3(3 / 16.0, 5 / 16.0, 3 / 16.0),
          new Vector3(3 / 16.0, -1 / 16.0, 13 / 16.0),
          new Vector4(13 / 16.0, 3 / 16.0, 6 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(13 / 16.0, 5 / 16.0, 3 / 16.0),
          new Vector3(13 / 16.0, 5 / 16.0, 13 / 16.0),
          new Vector3(13 / 16.0, -1 / 16.0, 3 / 16.0),
          new Vector4(13 / 16.0, 3 / 16.0, 6 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(3 / 16.0, 5 / 16.0, 3 / 16.0),
          new Vector3(13 / 16.0, 5 / 16.0, 3 / 16.0),
          new Vector3(3 / 16.0, -1 / 16.0, 3 / 16.0),
          new Vector4(13 / 16.0, 3 / 16.0, 6 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(13 / 16.0, 5 / 16.0, 13 / 16.0),
          new Vector3(3 / 16.0, 5 / 16.0, 13 / 16.0),
          new Vector3(13 / 16.0, -1 / 16.0, 13 / 16.0),
          new Vector4(13 / 16.0, 3 / 16.0, 6 / 16.0, 0 / 16.0)
        )
      }
    ), 0, -1, 0),
    Model.join(
      Model.rotateY(new Quad[]{
        new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        )
      }, Math.toRadians(45)),
      Model.rotateY(new Quad[]{
        new Quad(
          new Vector3(0 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        ),
        new Quad(
          new Vector3(16 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(0 / 16.0, 16 / 16.0, 8 / 16.0),
          new Vector3(16 / 16.0, 0 / 16.0, 8 / 16.0),
          new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
        )
      }, Math.toRadians(-45))
    ),
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(8 / 16.0, 16 / 16.0, 16 / 16.0),
        new Vector3(8 / 16.0, 16 / 16.0, 0 / 16.0),
        new Vector3(8 / 16.0, 0 / 16.0, 16 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(8 / 16.0, 16 / 16.0, 0 / 16.0),
        new Vector3(8 / 16.0, 16 / 16.0, 16 / 16.0),
        new Vector3(8 / 16.0, 0 / 16.0, 0 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(16 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(0 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(16 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(0 / 16.0, 16 / 16.0, 8 / 16.0),
        new Vector3(16 / 16.0, 0 / 16.0, 8 / 16.0),
        new Vector4(16 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
      )
    }, Math.toRadians(45))
  };

  private final int age;

  public PitcherCropTopModel(int age) {
    this.age = age;
  }

  @Override
  public Quad[] getQuads() {
    return quads[age];
  }

  @Override
  public Texture[] getTextures() {
    return textures[age];
  }
}
