package se.llbit.chunky.world.worldformat;

import se.llbit.chunky.world.World;
import se.llbit.util.Registerable;

import java.io.IOException;
import java.nio.file.Path;

/** For worlds that have multiple dimensions, and fully support the map view */
public interface WorldFormat extends Registerable {
  /**
   * This method will be called on every possible world directory (typically this is every directory in `.minecraft/saves`).
   *
   * @param path The path to the world.
   * @return Whether this is a valid world under this world format.
   */
  boolean isValid(Path path);

  /**
   * Load the world at the given path
   * @param path The path to the world.
   * @return The loaded world
   * @throws IOException When something goes wrong when loading the world.
   */
  World loadWorld(Path path) throws IOException;
}