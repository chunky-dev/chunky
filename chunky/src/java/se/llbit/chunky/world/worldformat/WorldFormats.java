package se.llbit.chunky.world.worldformat;

import se.llbit.chunky.world.EmptyWorld;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.java.JavaWorldFormat;
import se.llbit.log.Log;
import se.llbit.util.annotation.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class WorldFormats {
  /**
   * Uses an ordered hashmap to guarantee a consistent encounter order (and that {@link JavaWorldFormat} is checked first)
   */
  private static final LinkedHashMap<String, WorldFormat> worldFormatsById = new LinkedHashMap<>();

  public static void addWorldFormat(WorldFormat worldFormat) {
    worldFormatsById.put(worldFormat.getId(), worldFormat);
  }

  public static Map<String, WorldFormat> getWorldFormats() {
    return Collections.unmodifiableMap(worldFormatsById);
  }

  public static Optional<WorldFormat> getWorldFormat(String id) {
    return Optional.ofNullable(worldFormatsById.get(id));
  }

  static {
    addWorldFormat(new JavaWorldFormat());
  }

  @NotNull
  public static Collection<World.Info> getInfos(Path path) {
    return getWorldFormats().values().stream()
      .filter(format -> format.isValid(path))
      .map(format -> format.getWorldInfo(path))
      .flatMap(Optional::stream)
      .collect(Collectors.toList());
  }

  @NotNull
  public static World createWorld(@NotNull World.Info info) {
    try {
      return info.worldFormat().loadWorld(info);
    } catch (IOException e) {
      Log.error(String.format("An error occurred when trying to load a world using format `%s` from %s",
        info.worldFormat().getName(), info.path().toAbsolutePath()), e
      );
    }
    return EmptyWorld.INSTANCE;
  }
}
