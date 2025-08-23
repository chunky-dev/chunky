package se.llbit.chunky.world.worldformat;

import se.llbit.chunky.world.JavaWorld;
import se.llbit.chunky.world.World;

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
    return JavaWorld.isWorldDir(path.toFile());
  }

  @Override
  public World loadWorld(Path path) {
    return JavaWorld.loadWorld(path.toFile(), World.LoggedWarnings.SILENT);
  }
}
