package se.llbit.chunky.model;

import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public class Flowerbed extends QuadModel {
  //region flowerbed_1
  private static final BiFunction<Texture, Texture, Texture[]> flowerbed1Textures = (flowerbed, stem) -> new Texture[]{
    flowerbed, flowerbed, stem, stem, stem, stem, stem, stem, stem, stem, stem, stem, stem, stem
  };

  private static final Tint[] flowerbed1Tints = new Tint[]{
    Tint.NONE, Tint.NONE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
  };

  private static final Quad[] flowerbed1 = Model.join(
    new Quad[]{
      new Quad(
        new Vector3(0 / 16.0, 2.99 / 16.0, 8 / 16.0),
        new Vector3(8 / 16.0, 2.99 / 16.0, 8 / 16.0),
        new Vector3(0 / 16.0, 2.99 / 16.0, 0 / 16.0),
        new Vector4(0 / 16.0, 8 / 16.0, 8 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 2.99 / 16.0, 0 / 16.0),
        new Vector3(8 / 16.0, 2.99 / 16.0, 0 / 16.0),
        new Vector3(0 / 16.0, 2.99 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 8 / 16.0, 16 / 16.0, 8 / 16.0)
      )
    },
    Model.rotateY(
      new Quad[]{
        new Quad(
          new Vector3(4.25 / 16.0, 2.99 / 16.0, -1.6 / 16.0),
          new Vector3(4.25 / 16.0, 2.99 / 16.0, -2.6 / 16.0),
          new Vector3(4.25 / 16.0, 0 / 16.0, -1.6 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(4.25 / 16.0, 2.99 / 16.0, -2.6 / 16.0),
          new Vector3(4.25 / 16.0, 2.99 / 16.0, -1.6 / 16.0),
          new Vector3(4.25 / 16.0, 0 / 16.0, -2.6 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(3.75 / 16.0, 2.99 / 16.0, -2.1 / 16.0),
          new Vector3(4.75 / 16.0, 2.99 / 16.0, -2.1 / 16.0),
          new Vector3(3.75 / 16.0, 0 / 16.0, -2.1 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(4.75 / 16.0, 2.99 / 16.0, -2.1 / 16.0),
          new Vector3(3.75 / 16.0, 2.99 / 16.0, -2.1 / 16.0),
          new Vector3(4.75 / 16.0, 0 / 16.0, -2.1 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(4.9 / 16.0, 2.99 / 16.0, 3.3 / 16.0),
          new Vector3(4.9 / 16.0, 2.99 / 16.0, 2.3 / 16.0),
          new Vector3(4.9 / 16.0, 0 / 16.0, 3.3 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(4.9 / 16.0, 2.99 / 16.0, 2.3 / 16.0),
          new Vector3(4.9 / 16.0, 2.99 / 16.0, 3.3 / 16.0),
          new Vector3(4.9 / 16.0, 0 / 16.0, 2.3 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(4.4 / 16.0, 2.99 / 16.0, 2.8 / 16.0),
          new Vector3(5.4 / 16.0, 2.99 / 16.0, 2.8 / 16.0),
          new Vector3(4.4 / 16.0, 0 / 16.0, 2.8 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(5.4 / 16.0, 2.99 / 16.0, 2.8 / 16.0),
          new Vector3(4.4 / 16.0, 2.99 / 16.0, 2.8 / 16.0),
          new Vector3(5.4 / 16.0, 0 / 16.0, 2.8 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(9.15 / 16.0, 2.99 / 16.0, 0.55 / 16.0),
          new Vector3(9.15 / 16.0, 2.99 / 16.0, -0.45 / 16.0),
          new Vector3(9.15 / 16.0, 0 / 16.0, 0.55 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(9.15 / 16.0, 2.99 / 16.0, -0.45 / 16.0),
          new Vector3(9.15 / 16.0, 2.99 / 16.0, 0.55 / 16.0),
          new Vector3(9.15 / 16.0, 0 / 16.0, -0.45 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(8.65 / 16.0, 2.99 / 16.0, 0.05 / 16.0),
          new Vector3(9.65 / 16.0, 2.99 / 16.0, 0.05 / 16.0),
          new Vector3(8.65 / 16.0, 0 / 16.0, 0.05 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        ),
        new Quad(
          new Vector3(9.65 / 16.0, 2.99 / 16.0, 0.05 / 16.0),
          new Vector3(8.65 / 16.0, 2.99 / 16.0, 0.05 / 16.0),
          new Vector3(9.65 / 16.0, 0 / 16.0, 0.05 / 16.0),
          new Vector4(1 / 16.0, 0 / 16.0, 12 / 16.0, 9 / 16.0)
        )
      }, Math.toRadians(-45), new Vector3(0, 0, 0))
  );
  //endregion

  //region flowerbed_2
  private static final BiFunction<Texture, Texture, Texture[]> flowerbed2Textures = (flowerbed, stem) -> new Texture[]{
    flowerbed, flowerbed, flowerbed, flowerbed, stem, stem, stem, stem
  };

  private static final Tint[] flowerbed2Tints = new Tint[]{
    Tint.NONE, Tint.NONE, Tint.NONE, Tint.NONE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
  };

  private static final Quad[] flowerbed2 = Model.join(
    new Quad[]{
      new Quad(
        new Vector3(0 / 16.0, 1 / 16.0, 16 / 16.0),
        new Vector3(8 / 16.0, 1 / 16.0, 16 / 16.0),
        new Vector3(0 / 16.0, 1 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 8 / 16.0, 0 / 16.0, 8 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 1 / 16.0, 8 / 16.0),
        new Vector3(8 / 16.0, 1 / 16.0, 8 / 16.0),
        new Vector3(0 / 16.0, 1 / 16.0, 16 / 16.0),
        new Vector4(0 / 16.0, 8 / 16.0, 8 / 16.0, 0 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 1 / 16.0, 16 / 16.0),
        new Vector3(8 / 16.0, 1 / 16.0, 16 / 16.0),
        new Vector3(0 / 16.0, 1 / 16.0, 8 / 16.0),
        new Vector4(0 / 16.0, 8 / 16.0, 0 / 16.0, 8 / 16.0)
      ),
      new Quad(
        new Vector3(0 / 16.0, 1 / 16.0, 8 / 16.0),
        new Vector3(8 / 16.0, 1 / 16.0, 8 / 16.0),
        new Vector3(0 / 16.0, 1 / 16.0, 16 / 16.0),
        new Vector4(0 / 16.0, 8 / 16.0, 8 / 16.0, 0 / 16.0)
      )
    },
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(10.15 / 16.0, 1 / 16.0, 5.25 / 16.0),
        new Vector3(11.15 / 16.0, 1 / 16.0, 5.25 / 16.0),
        new Vector3(10.15 / 16.0, 0 / 16.0, 5.25 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 10 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(11.15 / 16.0, 1 / 16.0, 5.25 / 16.0),
        new Vector3(10.15 / 16.0, 1 / 16.0, 5.25 / 16.0),
        new Vector3(11.15 / 16.0, 0 / 16.0, 5.25 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 10 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(10.65 / 16.0, 1 / 16.0, 5.75 / 16.0),
        new Vector3(10.65 / 16.0, 1 / 16.0, 4.75 / 16.0),
        new Vector3(10.65 / 16.0, 0 / 16.0, 5.75 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 10 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(10.65 / 16.0, 1 / 16.0, 4.75 / 16.0),
        new Vector3(10.65 / 16.0, 1 / 16.0, 5.75 / 16.0),
        new Vector3(10.65 / 16.0, 0 / 16.0, 4.75 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 10 / 16.0, 9 / 16.0)
      )
    }, Math.toRadians(-45), new Vector3(0, 0, 1 / 16.))
  );
  //endregion

  //region flowerbed_3
  private static final BiFunction<Texture, Texture, Texture[]> flowerbed3Textures = (flowerbed, stem) -> new Texture[]{
    flowerbed, flowerbed, stem, stem, stem, stem, stem, stem, stem, stem, stem, stem, stem, stem
  };

  private static final Tint[] flowerbed3Tints = new Tint[]{
    Tint.NONE, Tint.NONE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE,
  };

  private static final Quad[] flowerbed3 = Model.join(
    new Quad[]{
      new Quad(
        new Vector3(8 / 16.0, 2 / 16.0, 16 / 16.0),
        new Vector3(16 / 16.0, 2 / 16.0, 16 / 16.0),
        new Vector3(8 / 16.0, 2 / 16.0, 8 / 16.0),
        new Vector4(8 / 16.0, 16 / 16.0, 0 / 16.0, 8 / 16.0)
      ),
      new Quad(
        new Vector3(8 / 16.0, 2 / 16.0, 8 / 16.0),
        new Vector3(16 / 16.0, 2 / 16.0, 8 / 16.0),
        new Vector3(8 / 16.0, 2 / 16.0, 16 / 16.0),
        new Vector4(8 / 16.0, 16 / 16.0, 8 / 16.0, 0 / 16.0)
      )
    },
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(17.65 / 16.0, 2 / 16.0, 1.9 / 16.0),
        new Vector3(18.65 / 16.0, 2 / 16.0, 1.9 / 16.0),
        new Vector3(17.65 / 16.0, 0 / 16.0, 1.9 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(18.65 / 16.0, 2 / 16.0, 1.9 / 16.0),
        new Vector3(17.65 / 16.0, 2 / 16.0, 1.9 / 16.0),
        new Vector3(18.65 / 16.0, 0 / 16.0, 1.9 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(18.15 / 16.0, 2 / 16.0, 2.4 / 16.0),
        new Vector3(18.15 / 16.0, 2 / 16.0, 1.4 / 16.0),
        new Vector3(18.15 / 16.0, 0 / 16.0, 2.4 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(18.15 / 16.0, 2 / 16.0, 1.4 / 16.0),
        new Vector3(18.15 / 16.0, 2 / 16.0, 2.4 / 16.0),
        new Vector3(18.15 / 16.0, 0 / 16.0, 1.4 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
    }, Math.toRadians(-45), new Vector3(0.5 / 16, 0, 0.5 / 16)),
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(17.65 / 16.0, 2 / 16.0, -2.35 / 16.0),
        new Vector3(17.65 / 16.0, 2 / 16.0, -3.35 / 16.0),
        new Vector3(17.65 / 16.0, 0 / 16.0, -2.35 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(17.65 / 16.0, 2 / 16.0, -3.35 / 16.0),
        new Vector3(17.65 / 16.0, 2 / 16.0, -2.35 / 16.0),
        new Vector3(17.65 / 16.0, 0 / 16.0, -3.35 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(17.15 / 16.0, 2 / 16.0, -2.85 / 16.0),
        new Vector3(18.15 / 16.0, 2 / 16.0, -2.85 / 16.0),
        new Vector3(17.15 / 16.0, 0 / 16.0, -2.85 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(18.15 / 16.0, 2 / 16.0, -2.85 / 16.0),
        new Vector3(17.15 / 16.0, 2 / 16.0, -2.85 / 16.0),
        new Vector3(18.15 / 16.0, 0 / 16.0, -2.85 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(13.4 / 16.0, 2 / 16.0, 0.5 / 16.0),
        new Vector3(13.4 / 16.0, 2 / 16.0, -0.5 / 16.0),
        new Vector3(13.4 / 16.0, 0 / 16.0, 0.5 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(13.4 / 16.0, 2 / 16.0, -0.5 / 16.0),
        new Vector3(13.4 / 16.0, 2 / 16.0, 0.5 / 16.0),
        new Vector3(13.4 / 16.0, 0 / 16.0, -0.5 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(12.9 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector3(13.9 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector3(12.9 / 16.0, 0 / 16.0, 0 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(13.9 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector3(12.9 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector3(13.9 / 16.0, 0 / 16.0, 0 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      )
    }, Math.toRadians(-45), new Vector3(0, 0, 0))
  );
  //endregion

  //region flowerbed_4
  private static final BiFunction<Texture, Texture, Texture[]> flowerbed4Textures = (flowerbed, stem) -> new Texture[]{
    flowerbed, flowerbed, stem, stem, stem, stem
  };

  private static final Tint[] flowerbed4Tints = new Tint[]{
    Tint.NONE, Tint.NONE,
    Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE, Tint.BIOME_FOLIAGE
  };

  private static final Quad[] flowerbed4 = Model.join(
    new Quad[]{
      new Quad(
        new Vector3(8 / 16.0, 2 / 16.0, 8 / 16.0),
        new Vector3(16 / 16.0, 2 / 16.0, 8 / 16.0),
        new Vector3(8 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector4(8 / 16.0, 16 / 16.0, 8 / 16.0, 16 / 16.0)
      ),
      new Quad(
        new Vector3(8 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector3(16 / 16.0, 2 / 16.0, 0 / 16.0),
        new Vector3(8 / 16.0, 2 / 16.0, 8 / 16.0),
        new Vector4(8 / 16.0, 16 / 16.0, 16 / 16.0, 8 / 16.0)
      )
    },
    Model.rotateY(new Quad[]{
      new Quad(
        new Vector3(12.4 / 16.0, 2 / 16.0, -6.7 / 16.0),
        new Vector3(12.4 / 16.0, 2 / 16.0, -7.7 / 16.0),
        new Vector3(12.4 / 16.0, 0 / 16.0, -6.7 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(12.4 / 16.0, 2 / 16.0, -7.7 / 16.0),
        new Vector3(12.4 / 16.0, 2 / 16.0, -6.7 / 16.0),
        new Vector3(12.4 / 16.0, 0 / 16.0, -7.7 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(11.9 / 16.0, 2 / 16.0, -7.2 / 16.0),
        new Vector3(12.9 / 16.0, 2 / 16.0, -7.2 / 16.0),
        new Vector3(11.9 / 16.0, 0 / 16.0, -7.2 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      ),
      new Quad(
        new Vector3(12.9 / 16.0, 2 / 16.0, -7.2 / 16.0),
        new Vector3(11.9 / 16.0, 2 / 16.0, -7.2 / 16.0),
        new Vector3(12.9 / 16.0, 0 / 16.0, -7.2 / 16.0),
        new Vector4(1 / 16.0, 0 / 16.0, 11 / 16.0, 9 / 16.0)
      )
    }, Math.toRadians(-45), new Vector3(-1 / 16., 0, -3 / 16.))
  );
  //endregion

  private final Texture[] textures;

  private final Quad[] quads;

  private final Tint[] tints;

  public Flowerbed(Texture flowerbed, Texture stem, int flowerAmount, String facing) {
    List<Texture> textures = new ArrayList<>();
    List<Quad> quadList = new ArrayList<>();
    List<Tint> tints = new ArrayList<>();

    Collections.addAll(textures, flowerbed1Textures.apply(flowerbed, stem));
    Collections.addAll(quadList, flowerbed1);
    Collections.addAll(tints, flowerbed1Tints);

    if (flowerAmount >= 2) {
      Collections.addAll(textures, flowerbed2Textures.apply(flowerbed, stem));
      Collections.addAll(quadList, flowerbed2);
      Collections.addAll(tints, flowerbed2Tints);
    }
    if (flowerAmount >= 3) {
      Collections.addAll(textures, flowerbed3Textures.apply(flowerbed, stem));
      Collections.addAll(quadList, flowerbed3);
      Collections.addAll(tints, flowerbed3Tints);
    }
    if (flowerAmount == 4) {
      Collections.addAll(textures, flowerbed4Textures.apply(flowerbed, stem));
      Collections.addAll(quadList, flowerbed4);
      Collections.addAll(tints, flowerbed4Tints);
    }

    this.textures = textures.toArray(new Texture[0]);
    Quad[] quads = quadList.toArray(new Quad[0]);
    this.tints = tints.toArray(new Tint[0]);

    switch (facing) {
      case "east":
        quads = Model.rotateY(quads);
        break;
      case "south":
        quads = Model.rotateY(Model.rotateY(quads));
        break;
      case "west":
        quads = Model.rotateNegY(quads);
        break;
    }
    this.quads = quads;
  }

  @Override
  public Quad[] getQuads() {
    return quads;
  }

  @Override
  public Texture[] getTextures() {
    return textures;
  }

  @Override
  public Tint[] getTints() {
    return tints;
  }
}
