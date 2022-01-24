package se.llbit.chunky.world.biome;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;

import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import se.llbit.util.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
* currently unused as no world has enough biomes for this to even be relevant
* would probably need to be >10000 biomes to even consider it
*/
public class MapBiomePalette implements BiomePalette {
  private final Reference2IntMap<Biome> biomeMap;
  private final List<Biome> palette;

  public MapBiomePalette() {
    this.biomeMap = new Reference2IntOpenHashMap<>();
    this.palette = new ArrayList<>();
  }

  public MapBiomePalette(@NotNull List<Biome> palette) {
    assert palette.stream().noneMatch(Objects::isNull);

    this.palette = new ArrayList<>(palette);
    this.biomeMap = new Reference2IntOpenHashMap<>(palette.size());

    for (int i = 0, size = palette.size(); i < size; i++) {
      this.biomeMap.put(palette.get(i), i);
    }
  }

  @Override
  public Biome get(int id) {
    assert id >= 0 && id < this.palette.size();

    return this.palette.get(id);
  }

  @Override
  public int put(@NotNull Biome biome) {
    return biomeMap.computeIfAbsent(biome, b -> {
      this.palette.add(b);
      return biomeMap.size();
    });
  }

  @Override
  public int size() {
    return palette.size();
  }
}
