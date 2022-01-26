package se.llbit.chunky.world.biome;

import se.llbit.log.Log;
import se.llbit.util.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArrayBiomePalette implements BiomePalette {
  public static final int ARRAY_BIOMEPALETTE_SIZE = 5;

  private final List<Biome> palette;

  public ArrayBiomePalette() {
    palette = new ArrayList<>(ARRAY_BIOMEPALETTE_SIZE);
  }

  public ArrayBiomePalette(@NotNull List<Biome> palette) {
    assert palette.stream().noneMatch(Objects::isNull);

    this.palette = new ArrayList<>(palette);
  }

  @Override
  public Biome get(int id) {
    assert id >= 0 && id < palette.size() : "id " + id + " out of bounds for palette";

    return this.palette.get(id);
  }

  @Override
  public int put(@NotNull Biome biome) {
    assert biome != null;

    for (int i = 0, len = this.palette.size(); i <= len; i++) {
      if(i == palette.size()) { //biome must not be in palette, so add it
        this.palette.add(biome);
        return i;
      }

      if(this.palette.get(i) == biome) { //found biome in palette, return it
        return i;
      }
    }
    Log.warn("Biome not present in Palette, defaulting to first biome in the palette");
    assert false; //should NEVER be reached, if reached in debug, crash
    return 0;
  }

  @Override
  public int size() {
    return palette.size();
  }
}
