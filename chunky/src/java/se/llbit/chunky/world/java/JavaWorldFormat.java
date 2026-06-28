package se.llbit.chunky.world.java;

import se.llbit.chunky.world.World;
import se.llbit.chunky.world.worldformat.WorldFormat;

import java.io.IOException;
import java.nio.file.Path;

public class JavaWorldFormat implements WorldFormat {
  @Override
  public String getName() {
    return "Java (Anvil)";
  }

  @Override
  public String getDescription() {
    return "The Minecraft world format for Java worlds since 1.2.1 (12w07a)";
  }

  @Override
  public String getId() {
    return "JAVA_ANVIL";
  }

  @Override
  public boolean isValid(Path path) {
    return JavaWorld.isWorldDirectory(path.toFile());
  }

  @Override
  public World loadWorld(Path path) throws IOException {
    return JavaWorld.loadWorld(path.toFile(), World.LoggedWarnings.SILENT);
  }
}
