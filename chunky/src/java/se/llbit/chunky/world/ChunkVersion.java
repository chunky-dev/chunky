package se.llbit.chunky.world;

public enum ChunkVersion {
  /**
   * Unknown chunk version
   */
  UNKNOWN,
  /**
   * Pre-flattening chunk (1.12 or older).
   */
  PRE_FLATTENING,
  /**
   * Post-flattening chunk (1.13 or later).
   */
  POST_FLATTENING
}
