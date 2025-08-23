package se.llbit.chunky.world.worldformat;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import se.llbit.chunky.world.EmptyWorld;
import se.llbit.chunky.world.World;
import se.llbit.log.Log;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WorldFormats {
  private static final Map<String, WorldFormat> worldFormatsById = new Object2ObjectOpenHashMap<>();

  public static void addWorldFormat(WorldFormat worldFormat) {
    worldFormatsById.put(worldFormat.getId(), worldFormat);
  }

  public static Map<String, WorldFormat> getWorldFormats() {
    return Collections.unmodifiableMap(worldFormatsById);
  }

  public static WorldFormat getWorldFormat(String id) {
    return worldFormatsById.get(id);
  }

  static {
    addWorldFormat(new JavaWorldFormat());
  }

  public static Optional<World> createWorld(File dir) {
    Map<String, World> providedWorlds = new Object2ObjectOpenHashMap<>();

    getWorldFormats().forEach((id, format) -> {
      if (format.isValid(dir.toPath())) {
        try {
          World world = format.loadWorld(dir.toPath());
          if (world != EmptyWorld.INSTANCE) {
            providedWorlds.put(format.getId(), world);
          }
        } catch (IOException e) {
          Log.error(String.format("An error occurred when trying to load a world using format `%s` from %s", format.getName(), dir.getAbsolutePath()), e);
        }
      }
    });

    if (providedWorlds.size() > 1) {
      // Maybe allow the user to select which?
      // This method is called from a variety of different popup/menu situations, is this ^ possible?
      Log.warn(String.format("The directory %s has multiple valid world formats: %s", dir.getAbsolutePath(), String.join(", ", providedWorlds.keySet())));
    }
    return providedWorlds.values().stream().findFirst();
  }
}
