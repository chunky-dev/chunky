package se.llbit.chunky.nbt;

import org.junit.Test;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.StringTag;

import static org.junit.Assert.assertEquals;

/**
 * Tests to ensure that the NBT library works.
 */
public class NBTTest {
  @Test public void testEqualTags() {
    CompoundTag tag1 = new CompoundTag();
    tag1.add("Name", new StringTag("minecraft:stone"));
    CompoundTag tag2 = new CompoundTag();
    tag2.add("Name", new StringTag("minecraft:stone"));
    assertEquals(tag1.hashCode(), tag2.hashCode());
    assertEquals(tag1, tag2);
  }
}
