package se.llbit.chunky.world.biome;

import se.llbit.log.Log;
import se.llbit.util.NotNull;

import java.util.List;
import java.util.Objects;

public class ArrayBiomePalette implements BiomePalette {
  public static final int ARRAY_BIOMEPALETTE_SIZE = 5;

  private Biome[] palette;
  private int size = 0;

  public ArrayBiomePalette() {
    palette = new Biome[ARRAY_BIOMEPALETTE_SIZE];
  }

  public ArrayBiomePalette(@NotNull List<Biome> palette) {
    assert palette.stream().noneMatch(Objects::isNull);

    this.palette = palette.toArray(new Biome[0]);
  }

  @Override
  public Biome get(int id) {
    assert id >= 0 && id < size;

    return this.palette[id];
  }

  @Override
  public int put(@NotNull Biome biome) {
    assert biome != null;

    if(size == this.palette.length) {
      this.grow();
    }

    for (int i = 0, len = this.palette.length; i < len; i++) {
      if(i >= size) { //biome must not be in palette, so add it
        this.palette[i] = biome;
        ++size;
        return i;
      }

      if(this.palette[i] == biome) { //found biome in palette, return it
        return i;
      }
    }
    Log.warn("Biome not present in Palette, defaulting to first biome in the palette");
    assert false; //should NEVER be reached, if reached in debug, crash
    return 0;
  }

  private void grow() {
    Biome[] original = this.palette;
    this.palette = new Biome[((int) Math.ceil(original.length * 1.5))]; // grow by 1.5 times size
    System.arraycopy(original, 0, palette, 0, original.length);
  }
}
