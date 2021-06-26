package se.llbit.util;

/**
 * This interface specifies a Registerable object that can be switched by the user.
 * This would be, for example, different Octree implementations.
 */
public interface Registerable {
  /**
   * Get the pretty name of this object.
   * For example, "Chunky Path Tracer".
   */
  String getName();

  /**
   * Get the description of this object.
   * For example, "A photorealistic Path Tracing renderer."
   */
  String getDescription();

  /**
   * Get the unique identifier of this object.
   * For example, "PathTracingRenderer".
   */
  String getId();
}
