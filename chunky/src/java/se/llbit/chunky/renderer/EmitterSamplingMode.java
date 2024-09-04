package se.llbit.chunky.renderer;

import se.llbit.util.Registerable;

public enum EmitterSamplingMode implements Registerable {
  SAMPLE_THROUGH_OPACITY("Sample through opacity", "Sample emitters through translucent textures"),
  SAMPLE_ONLY("Sample only", "Sample emitters on diffuse reflections."),
  OFF("Diffuse", "Diffusely intersect on all interactions.");

  private final String name;
  private final String description;

  EmitterSamplingMode(String name, String description) {
    this.name = name;
    this.description = description;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getId() {
    return this.name();
  }
}
