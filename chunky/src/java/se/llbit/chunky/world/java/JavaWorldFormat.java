package se.llbit.chunky.world.java;

import se.llbit.chunky.world.World;
import se.llbit.chunky.world.worldformat.WorldFormat;
import se.llbit.util.annotation.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class JavaWorldFormat implements WorldFormat {
  public static final String NAME = "Java (Anvil)";
  public static final String ID = "JAVA_ANVIL";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getDescription() {
    return "The Minecraft world format for Java worlds since 1.2.1 (12w07a)";
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public boolean isValid(@NotNull Path path) {
    if (Files.isDirectory(path)) {
      Path levelDat = path.resolve("level.dat");
      return Files.exists(levelDat) && Files.isRegularFile(levelDat);
    }
    return false;
  }

  @NotNull
  @Override
  public Optional<World.Info> getWorldInfo(@NotNull Path path) {
    if (!Files.isDirectory(path)) {
      return Optional.empty();
    }
    Path levelDat = path.resolve("level.dat");
    if (!Files.exists(levelDat) || !Files.isRegularFile(levelDat)) {
      return Optional.empty();
    }

    return JavaWorld.loadWorldInfo(path, World.LoggedWarnings.SILENT, this);
  }

  @NotNull
  @Override
  public World loadWorld(@NotNull World.Info info) {
    return JavaWorld.loadWorld(info, World.LoggedWarnings.NORMAL);
  }
}
