package se.llbit.chunky.world.biome;

public interface BiomePalette {
  Biome get(int id);

  int put(Biome biome);

  int size();
}
