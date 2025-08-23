package se.llbit.chunky.world.worldformat;

import se.llbit.chunky.world.Dimension;
import se.llbit.chunky.world.World;
import se.llbit.chunky.world.java.JavaWorld;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface WorldFormat {
  // TODO: Registerable
  Collection<WorldFormat> worldFormats = List.of(new JavaWorldFormat());

  boolean isValid(Path path);

  World loadWorld(Path path, Dimension.Identifier dimension) throws IOException;
}
