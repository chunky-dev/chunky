package se.llbit.chunky.world.worldformat;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import se.llbit.chunky.world.EmptyWorld;
import se.llbit.chunky.world.World;
import se.llbit.log.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/** For worlds that have multiple dimensions, and fully support the map view */
public interface WorldFormat {
  // TODO: Registerable
  Collection<WorldFormat> worldFormats = List.of(new JavaWorldFormat());

  // Should this go somewhere else?
  static Optional<World> loadWorld(File dir) {
    Map<String, World> worldsByFormat = new Object2ObjectOpenHashMap<>();

    for (WorldFormat worldFormat : WorldFormat.worldFormats) {
      if (worldFormat.isValid(dir.toPath())) {
        try {
          World world = worldFormat.loadWorld(dir.toPath());
          if (world != EmptyWorld.INSTANCE) {
            worldsByFormat.put(worldFormat.name(), world);
          }
        } catch (IOException e) {
          Log.error(String.format("An error occurred when trying to load a world using format `%s` from %s", worldFormat.name(), dir.getAbsolutePath()), e);
        }
      }
    }
    if (worldsByFormat.size() > 1) {
      // Maybe allow the user to select which?
      // This method is called from a variety of different popup/menu situations, is this ^ possible?
      Log.warn(String.format("The directory %s has multiple valid world formats: %s", dir.getAbsolutePath(), String.join(", ", worldsByFormat.keySet())));
    }
    return worldsByFormat.values().stream().findFirst();
  }

  /**
   * @return The user-recognisable name of the world format. Shown to the user if this format has issues or throws.
   */
  String name();

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