package se.llbit.chunky.model.minecraft;

import se.llbit.chunky.model.Model;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.model.builder.QuadModelBuilder;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.function.BiFunction;

public class BarsModel extends QuadModel {
  private static final Quad[] barsPostEndsQuads = new Quad[]{
    new Quad(
      new Vector3(7 / 16.0, 0.001 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 0.001 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 0.001 / 16.0, 7 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 0.001 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 0.001 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 0.001 / 16.0, 9 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 15.999 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 15.999 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 15.999 / 16.0, 7 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 15.999 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 15.999 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 15.999 / 16.0, 9 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 7 / 16.0, 9 / 16.0)
    )
  };

  private static final Quad[] barsPostQuads = new Quad[]{
    new Quad(
      new Vector3(8 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(8 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(8 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(8 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(8 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };

  private static final Quad[] barsCapQuads = new Quad[]{
    new Quad(
      new Vector3(8 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(8 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(8 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(7 / 16.0, 8 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(8 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(8 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector4(8 / 16.0, 7 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };

  private static final Quad[] barsCapAltQuads = new Quad[]{
    new Quad(
      new Vector3(8 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(8 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(8 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector4(9 / 16.0, 8 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(8 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(8 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(8 / 16.0, 9 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(9 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 7 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 16 / 16.0, 0 / 16.0)
    )
  };

  private static final BiFunction<Texture, Texture, Texture[]> barsSideTex = (bars, edge) -> new Texture[]{
    bars, bars, /* edge, */ edge, edge, edge, edge
  };

  private static final Quad[] barsSideQuads = new Quad[]{
    new Quad(
      new Vector3(8 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(8 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector4(8 / 16.0, 16 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(8 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(8 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(16 / 16.0, 8 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    /*
    new Quad(
      new Vector3(7 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 0 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 0 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    */
    new Quad(
      new Vector3(7 / 16.0, 0.001 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 0.001 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 0.001 / 16.0, 0 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 9 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 0.001 / 16.0, 0 / 16.0),
      new Vector3(9 / 16.0, 0.001 / 16.0, 0 / 16.0),
      new Vector3(7 / 16.0, 0.001 / 16.0, 7 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 9 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 15.999 / 16.0, 7 / 16.0),
      new Vector3(9 / 16.0, 15.999 / 16.0, 7 / 16.0),
      new Vector3(7 / 16.0, 15.999 / 16.0, 0 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 9 / 16.0, 16 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 15.999 / 16.0, 0 / 16.0),
      new Vector3(9 / 16.0, 15.999 / 16.0, 0 / 16.0),
      new Vector3(7 / 16.0, 15.999 / 16.0, 7 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 9 / 16.0, 16 / 16.0)
    )
  };

  private static final BiFunction<Texture, Texture, Texture[]> barsSideAltTex = (bars, edge) -> new Texture[]{
    bars, bars, edge, edge, /* edge, */ edge, edge, edge, edge
  };

  private static final Quad[] barsSideAltQuads = new Quad[]{
    new Quad(
      new Vector3(8 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(8 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(0 / 16.0, 8 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(8 / 16.0, 16 / 16.0, 8 / 16.0),
      new Vector3(8 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(8 / 16.0, 0 / 16.0, 8 / 16.0),
      new Vector4(8 / 16.0, 0 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(9 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 9 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 0 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 0 / 16.0, 7 / 16.0)
    ),
    /*
    new Quad(
      new Vector3(9 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(7 / 16.0, 16 / 16.0, 16 / 16.0),
      new Vector3(9 / 16.0, 0 / 16.0, 16 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 16 / 16.0, 0 / 16.0)
    ),
    */
    new Quad(
      new Vector3(7 / 16.0, 0.001 / 16.0, 16 / 16.0),
      new Vector3(9 / 16.0, 0.001 / 16.0, 16 / 16.0),
      new Vector3(7 / 16.0, 0.001 / 16.0, 9 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 0 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 0.001 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 0.001 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 0.001 / 16.0, 16 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 0 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 15.999 / 16.0, 16 / 16.0),
      new Vector3(9 / 16.0, 15.999 / 16.0, 16 / 16.0),
      new Vector3(7 / 16.0, 15.999 / 16.0, 9 / 16.0),
      new Vector4(7 / 16.0, 9 / 16.0, 0 / 16.0, 7 / 16.0)
    ),
    new Quad(
      new Vector3(7 / 16.0, 15.999 / 16.0, 9 / 16.0),
      new Vector3(9 / 16.0, 15.999 / 16.0, 9 / 16.0),
      new Vector3(7 / 16.0, 15.999 / 16.0, 16 / 16.0),
      new Vector4(9 / 16.0, 7 / 16.0, 0 / 16.0, 7 / 16.0)
    )
  };

  private final Texture[] textures;
  private final Quad[] quads;

  public BarsModel(boolean north, boolean east, boolean south, boolean west, Texture edge, Texture bars) {
    QuadModelBuilder builder = new QuadModelBuilder();
    builder.addModel(barsPostEndsQuads, edge);
    if (!north && !east && !south && !west) {
      builder.addModel(barsPostQuads, bars);
    }
    if (!east && north && !south && !west) {
      builder.addModel(barsCapQuads, bars);
    }
    if (east && !north && !south && !west) {
      builder.addModel(Model.rotateY(barsCapQuads), bars);
    }
    if (!east && !north && south && !west) {
      builder.addModel(barsCapAltQuads, bars);
    }
    if (!east && !north && !south && west) {
      builder.addModel(Model.rotateY(barsCapAltQuads), bars);
    }
    if (north) {
      builder.addModel(barsSideQuads, barsSideTex.apply(bars, edge));
    }
    if (east) {
      builder.addModel(Model.rotateY(barsSideQuads), barsSideTex.apply(bars, edge));
    }
    if (south) {
      builder.addModel(barsSideAltQuads, barsSideAltTex.apply(bars, edge));
    }
    if (west) {
      builder.addModel(Model.rotateY(barsSideAltQuads), barsSideAltTex.apply(bars, edge));
    }
    quads = builder.getQuads();
    textures = builder.getTextures();
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
