package se.llbit.chunky.block.jsonmodels;

import se.llbit.math.Quad;
import se.llbit.math.Transform;

public class TintedQuad extends Quad {

  private final int tintindex;

  public TintedQuad(Quad quad, int tintindex) {
    super(quad, Transform.NONE);
    this.tintindex = tintindex;
  }

  public int getTintIndex() {
    return tintindex;
  }
}
