package se.llbit.chunky.chunk;

import org.junit.jupiter.api.Test;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.StringTag;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BlockPaletteTest {
  // Test that the block palette reuses existing blocks with the same tag data.
  @Test
  public void testBlockReuse() {
    CompoundTag t1 = new CompoundTag();
    t1.add("Name", new StringTag("some block"));
    CompoundTag t2 = new CompoundTag();
    t2.add("Name", new StringTag("some block"));
    BlockPalette palette = new BlockPalette();
    assertEquals(palette.put(t1), palette.put(t2),
      "Block palette does not reuse block IDs for duplicate tags");
  }

  // Test that the block palette reuses the existing air id.
  @Test public void testAir() {
    CompoundTag air = new CompoundTag();
    air.add("Name", new StringTag("minecraft:air"));
    BlockPalette palette = new BlockPalette();
    assertEquals(palette.airId, palette.put(air));
  }

  // Test that the block palette reuses the existing water id.
  @Test public void testWater() {
    CompoundTag water = new CompoundTag();
    water.add("Name", new StringTag("minecraft:water"));
    BlockPalette palette = new BlockPalette();
    assertEquals(palette.waterId, palette.put(water));
  }
}
