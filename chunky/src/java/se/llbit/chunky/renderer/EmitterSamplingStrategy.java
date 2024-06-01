package se.llbit.chunky.renderer;

import se.llbit.util.Registerable;

public enum EmitterSamplingStrategy implements Registerable {
  NONE("None", "No emitter sampling."),
  ONE("One", "Sample a single face."),
  ONE_BLOCK("One Block", "Sample all the faces on a single emitter block."),
  ALL("All", "Sample all faces on all emitter blocks.");

  private final String name;
  private final String description;

  EmitterSamplingStrategy(String name, String description) {
    this.name = name;
    this.description = description;
  }

  @Override
  public String getName() {
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
