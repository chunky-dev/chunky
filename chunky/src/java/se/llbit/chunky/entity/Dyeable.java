package se.llbit.chunky.entity;

import se.llbit.chunky.world.material.DyedTextureMaterial;

/**
 * Interface for entities that can be dyed or have a customizable color like Sheep and Shulkers
 */
public interface Dyeable {
  /**
   * @return The DyedTextureMaterial that the entity is using.
   */
  DyedTextureMaterial getMaterial();
}
