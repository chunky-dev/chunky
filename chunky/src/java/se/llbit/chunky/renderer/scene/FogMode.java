package se.llbit.chunky.renderer.scene;

import se.llbit.util.Registerable;

public enum FogMode implements Registerable {
  NONE("None", "No fog is present."),
  UNIFORM("Uniform", "Fog is distributed uniformly throughout the scene."),
  LAYERED("Layered", "Fog is distributed throughout the scene in layers.");

  private final String displayName;
  private final String description;

  FogMode(String displayName, String description) {
    this.displayName = displayName;
    this.description = description;
  }

  @Override
  public String getName() {
    return this.displayName;
  }

  @Override
  public String toString() {
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
