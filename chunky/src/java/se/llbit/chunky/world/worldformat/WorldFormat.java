package se.llbit.chunky.world.worldformat;

import se.llbit.chunky.world.World;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/** For worlds that have multiple dimensions, and fully support the map view */
public interface WorldFormat {
  // TODO: Registerable
  Collection<WorldFormat> worldFormats = List.of(new JavaWorldFormat());

  World loadWorld(Path path, String dimension) throws IOException;
}
