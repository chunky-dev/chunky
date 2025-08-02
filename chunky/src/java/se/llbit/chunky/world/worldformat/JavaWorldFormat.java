package se.llbit.chunky.world.worldformat;

import se.llbit.chunky.world.JavaWorld;
import se.llbit.chunky.world.World;

import java.io.IOException;
import java.nio.file.Path;

public class JavaWorldFormat implements WorldFormat {
  @Override
  public World loadWorld(Path path, String dimension) throws IOException {
    return JavaWorld.loadWorld(path.toFile(), Integer.parseInt(dimension), World.LoggedWarnings.SILENT);
  }

  @Override
  public boolean isValid(Path path) {
    return JavaWorld.isWorldDir(path.toFile());
  }
}
