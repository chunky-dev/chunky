package se.llbit.chunky.world.worldformat;

import se.llbit.chunky.world.World;
import se.llbit.util.Registerable;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A {@link WorldFormat} represents a path on disk that can be loaded into chunky as a {@link World}
 *
 * <p>Implementations should be <b><u>stateless</u></b>, and never cache world validity.</p>
 *
 * <p>Implementations will be queried for many different paths via {@link #isValid(Path)}.
 * The same {@link Path} is likely to be checked more than once over the lifetime of the {@link WorldFormat}</p>
 *
 * <p>Impls. are guaranteed that paths given to {@link #loadWorld(Path)} will already be validated
 * through their {@link #isValid(Path)}</p>
 */
public interface WorldFormat extends Registerable {
  /**
   * Determine whether a {@link Path} is valid for this {@link WorldFormat}
   *
   * <p>This method will be called on every possible world directory
   * (typically this is every directory in `.minecraft/saves`).</p>
   *
   * @param path The path to the world.
   * @return Whether this is a valid world under this world format.
   */
  boolean isValid(Path path);

  /**
   * Load the world at the given path.
   *
   * <p>Calls to this method do not indicate that any blocks will be loaded from the world. As such implementations
   * should do <b><u>minimal</u></b> work to load the metadata for a world.</p>
   *
   * @param path The path to the world.
   * @return The loaded world
   * @throws IOException When something goes wrong when loading the world.
   */
  World loadWorld(Path path) throws IOException;
}