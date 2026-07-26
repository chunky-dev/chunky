package se.llbit.chunky.world.worldformat;

import se.llbit.chunky.world.Dimension;
import se.llbit.chunky.world.java.JavaWorld;
import se.llbit.chunky.world.World;

import java.io.IOException;
import java.nio.file.Path;

public class JavaWorldFormat implements WorldFormat {
  @Override
  public boolean isValid(Path path) {
    return JavaWorld.isWorldDir(path.toFile());
  }

  @Override
  public World loadWorld(Path path, Dimension.Identifier dimension) throws IOException {
    return JavaWorld.loadWorld(path.toFile(), dimension, World.LoggedWarnings.SILENT);
  }
}
