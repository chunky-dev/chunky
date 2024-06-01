package se.llbit.chunky.world;

import se.llbit.util.Registerable;

public enum MaterialType implements Registerable {

  LAMBERTIAN("Lambertian", ""),
  DIELECTRIC("Dielectric", ""),
  METAL("Metal", "");

  private final String displayName;
  private final String description;

  MaterialType(String displayName, String description) {
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
