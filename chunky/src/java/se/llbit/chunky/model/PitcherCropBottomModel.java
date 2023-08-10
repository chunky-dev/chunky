package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class PitcherCropBottomModel extends QuadModel {
  private static final Texture pitcher_top = Texture.pitcherCropTop;
  private static final Texture pitcher_side = Texture.pitcherCropSide;
  private static final Texture pitcher_bottom = Texture.pitcherCropBottom;
  private static final Texture stage_1 = Texture.pitcherCropBottomStage1;
  private static final Texture stage_2 = Texture.pitcherCropBottomStage2;
  private static final Texture stage_3_bottom = Texture.pitcherCropBottomStage3;
  private static final Texture stage_4_bottom = Texture.pitcherCropBottomStage4;

  private static final Texture[][] textures = new Texture[][]{
    new Texture[]{
      pitcher_top, pitcher_bottom, pitcher_side, pitcher_side, pitcher_side, pitcher_side
    },
    new Texture[]{
      stage_1, stage_1, stage_1, stage_1, pitcher_top, pitcher_bottom, pitcher_side, pitcher_side, pitcher_side, pitcher_side
    },
    new Texture[]{
      stage_2, stage_2, stage_2, stage_2, pitcher_top, pitcher_bottom, pitcher_side, pitcher_side, pitcher_side, pitcher_side
    },
    new Texture[]{
      stage_3_bottom, stage_3_bottom, stage_3_bottom, stage_3_bottom, pitcher_top, pitcher_bottom, pitcher_side, pitcher_side, pitcher_side, pitcher_side
    },
    new Texture[]{
      stage_4_bottom, stage_4_bottom, stage_4_bottom, stage_4_bottom, pitcher_top, pitcher_bottom, pitcher_side, pitcher_side, pitcher_side, pitcher_side
    }
  };

  private static final Quad[][] quads = new Quad[][]{
    new Quad[]{
      new Quad(
        new Vector3(5 / 16.0, 3 / 16.0, 11 / 16.0),
        new Vector3(11 / 16.0, 3 / 16.0, 11 / 16.0),
        new Vector3(5 / 16.0, 3 / 16.0, 5 / 16.0),
        new Vector4(5 / 16.0, 11 / 16.0, 5 / 16.0, 11 / 16.0)
      ),
      new Quad(
        new Vector3(5 / 16.0, -1 / 16.0, 5 / 16.0),
        new Vector3(11 / 16.0, -1 / 16.0, 5 / 16.0),
        new Vector3(5 / 16.0, -1 / 16.0, 11 / 16.0),
        new Vector4(5 / 16.0, 11 / 16.0, 5 / 16.0, 11 / 16.0)
      ),
      new Quad(
        new Vector3(5 / 16.0, 3 / 16.0, 11 / 16.0),
        new Vector3(5 / 16.0, 3 / 16.0, 5 / 16.0),
        new Vector3(5 / 16.0, -1 / 16.0, 11 / 16.0),
        new Vector4(9 / 16.0, 3 / 16.0, 6 / 16.0, 2 / 16.0)
      ),
      new Quad(
        new Vector3(11 / 16.0, 3 / 16.0, 5 / 16.0),
        new Vector3(11 / 16.0, 3 / 16.0, 11 / 16.0),
        new Vector3(11 / 16.0, -1 / 16.0, 5 / 16.0),
        new Vector4(9 / 16.0, 3 / 16.0, 6 / 16.0, 2 / 16.0)
      ),
      new Quad(
        new Vector3(5 / 16.0, 3 / 16.0, 5 / 16.0),
        new Vector3(11 / 16.0, 3 / 16.0, 5 / 16.0),
        new Vector3(5 / 16.0, -1 / 16.0, 5 / 16.0),
        new Vector4(9 / 16.0, 3 / 16.0, 6 / 16.0, 2 / 16.0)
      ),
      new Quad(
        new Vector3(11 / 16.0, 3 / 16.0, 11 / 16.0),
        new Vector3(5 / 16.0, 3 / 16.0, 11 / 16.0),
        new Vector3(11 / 16.0, -1 / 16.0, 11 / 16.0),
        new Vector4(9 / 16.0, 3 / 16.0, 6 / 16.0, 2 / 16.0)
      )
    },
    Model.join(
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
        )
      }, Math.toRadians(45)),
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
        )
      }, Math.toRadians(-45)),
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
    ),
    Model.join(
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
    ),
    Model.join(
      Model.rotateY(
        new Quad[]{
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
        }, Math.toRadians(45)
      ),
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
      }, Math.toRadians(-45)),
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
    ),
    Model.join(
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
    )
  };

  private final int age;

  public PitcherCropBottomModel(int age) {
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
