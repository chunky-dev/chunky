package se.llbit.chunky.world.worldformat;

import java.nio.file.Path;

public interface Loadable {
  boolean isValid(Path path);
}



