package se.llbit.chunky.world.bedrock;

import se.llbit.chunky.world.World;
import se.llbit.chunky.world.worldformat.WorldFormat;
import se.llbit.util.annotation.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class BedrockWorldFormat implements WorldFormat {
  @Override
  public String getName() {
    return "Bedrock";
  }

  @Override
  public String getDescription() {
    return "The Minecraft world format for Bedrock worlds";
  }

  @Override
  public String getId() {
    return "BEDROCK_LEVELDB";
  }

  @Override
  public boolean isValid(Path path) {
    return Files.isRegularFile(path.resolve("level.dat"))
      && Files.isDirectory(path.resolve("db"));
  }

  @NotNull
  @Override
  public Optional<World.Info> getWorldInfo(@NotNull Path path) {
    if (!isValid(path)) {
      return Optional.empty();
    }

    String name = path.getFileName().toString();
    if (Files.exists(path.resolve("levelname.txt"))) {
      try {
        name = Files.readAllLines(path.resolve("levelname.txt")).stream().findFirst().orElse(name);
      } catch (IOException ignored) {
      }
    }
    return Optional.of(new World.Info(name, path, 0, 0, "Survival", this));
  }

  @NotNull
  @Override
  public World loadWorld(@NotNull World.Info info) {
    return new BedrockWorld(info);
  }
}
