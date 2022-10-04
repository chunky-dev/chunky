package se.llbit.chunky.block.jsonmodels;

import se.llbit.chunky.model.Tint;
import se.llbit.math.Quad;
import se.llbit.math.Transform;

public class TintedQuad extends Quad {

  public final Tint tint;

  public TintedQuad(Quad quad, Tint tint) {
    super(quad, Transform.NONE);
    this.tint = tint;
  }
}
