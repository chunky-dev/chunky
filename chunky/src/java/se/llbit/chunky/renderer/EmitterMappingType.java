package se.llbit.chunky.renderer;

import se.llbit.util.Registerable;

public enum EmitterMappingType implements Registerable {
  BRIGHTEST_CHANNEL("Brightest Channel", "Emitted light (R', G', B') = (R*M^P, G*M^P, B*M^P) where M = max(R, G, B) and P is the specified power. Emitted light will always match pixel color."),
  INDEPENDENT_CHANNELS("Independent Channels", "Emitted light (R', G', B') = (R^P, G^P, B^P) where P is the specified power. Saturation of emitted light increases with P - possibly less realistic in some situations.");
  private final String displayName;
  private final String description;
  EmitterMappingType(String displayName, String description) {
    this.displayName = displayName;
    this.description = description;
  }
  @Override
  public String getName() {
    return this.displayName;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public String getId() {
    return this.name();
  }
}
