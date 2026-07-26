package se.llbit.chunky.world;

import java.util.Collections;

public class EmptyDimension extends Dimension {
  EmptyDimension() {
    super(EmptyWorld.INSTANCE, Dimension.Identifier.OVERWORLD, null, Collections.emptySet());
  }

  @Override
  public String toString() {
    return "[empty dimension]";
  }
}
