package se.llbit.chunky.entity;

/**
 * Interface for entities that can be saddled, like a Pig, Horse, etc.
 */
public interface Saddleable {

  /**
   * @param saddled whether the entity should have a saddle on
   */
  void setIsSaddled(boolean saddled);

  /**
   * @return whether the entity has a saddle on
   */
  boolean isSaddled();
}
