package se.llbit.chunky.world;

import java.util.Collections;

public class EmptyDimension extends Dimension {
  public static final EmptyDimension INSTANCE = new EmptyDimension();

  private EmptyDimension() {
    super(EmptyWorld.INSTANCE, Dimension.Identifier.OVERWORLD, null, Collections.emptySet());
  }

  @Override
  public String toString() {
    return "[empty dimension]";
  }
}
