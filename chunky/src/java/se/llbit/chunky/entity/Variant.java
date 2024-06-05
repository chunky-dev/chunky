package se.llbit.chunky.entity;

/**
 * Interface for entities that have several variants. Wolves, Mooshrooms, Cats, etc.
 */
public interface Variant {

  /**
   * @return an array of available variant names
   */
  String[] variants();

  /**
   * @return the current variant of the entity
   */
  String getVariant();

  void setVariant(String variant);
}
