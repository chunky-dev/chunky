package se.llbit.chunky.block;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import org.junit.Test;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

public class SkullTextureTest {

  @Test
  public void testValidJson() {
    CompoundTag validOwnerTag = createSkullTag("Owner", Base64.getEncoder().encodeToString(
        "{ \"textures\": { \"SKIN\": { \"url\": \"http://textures.minecraft.net/texture/1acde954db327685201f785a6b248b73fdc8982c2aed430f697cdebec9b7e14\" } } }"
            .getBytes(StandardCharsets.UTF_8)));
    assertEquals(
        "http://textures.minecraft.net/texture/1acde954db327685201f785a6b248b73fdc8982c2aed430f697cdebec9b7e14",
        Head.getTextureUrl(validOwnerTag));
  }

  @Test
  public void testSkullOwner() {
    // player heads use SkullOwner, which should work too
    CompoundTag validOwnerTag = createSkullTag("SkullOwner", Base64.getEncoder().encodeToString(
        "{ \"textures\": { \"SKIN\": { \"url\": \"http://textures.minecraft.net/texture/1acde954db327685201f785a6b248b73fdc8982c2aed430f697cdebec9b7e14\" } } }"
            .getBytes(StandardCharsets.UTF_8)));
    assertEquals(
        "http://textures.minecraft.net/texture/1acde954db327685201f785a6b248b73fdc8982c2aed430f697cdebec9b7e14",
        Head.getTextureUrl(validOwnerTag));
  }

  @Test
  public void testInvalidJson() { // test for #680, #681
    CompoundTag validOwnerTag = createSkullTag("Owner", Base64.getEncoder().encodeToString(
        "{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/1acde954db327685201f785a6b248b73fdc8982c2aed430f697cdebec9b7e14\"}}}"
            .getBytes(StandardCharsets.UTF_8)));
    assertEquals(
        "http://textures.minecraft.net/texture/1acde954db327685201f785a6b248b73fdc8982c2aed430f697cdebec9b7e14",
        Head.getTextureUrl(validOwnerTag));
  }

  @Test
  public void testAlexSkull() { // test for #749
    CompoundTag validJsonAlexTag = createSkullTag("Owner", Base64.getEncoder().encodeToString(
        "{ \"textures\": { \"SKIN\": {\"metadata\": {\"model\": \"slim\"}, \"url\": \"http://textures.minecraft.net/texture/1acde954db327685201f785a6b248b73fdc8982c2aed430f697cdebec9b7e14\" } } }"
            .getBytes(StandardCharsets.UTF_8)));
    assertEquals(
        "http://textures.minecraft.net/texture/1acde954db327685201f785a6b248b73fdc8982c2aed430f697cdebec9b7e14",
        Head.getTextureUrl(validJsonAlexTag));

    CompoundTag invalidJsonAlexTag = createSkullTag("Owner", Base64.getEncoder().encodeToString(
        "{textures:{SKIN:{metadata:{model:\"slim\"},url:\"http://textures.minecraft.net/texture/1acde954db327685201f785a6b248b73fdc8982c2aed430f697cdebec9b7e14\"}}}"
            .getBytes(StandardCharsets.UTF_8)));
    assertEquals(
        "http://textures.minecraft.net/texture/1acde954db327685201f785a6b248b73fdc8982c2aed430f697cdebec9b7e14",
        Head.getTextureUrl(invalidJsonAlexTag));
  }

  @Test
  public void testMetadataAfterUrl() { // test for #969
    CompoundTag validJsonAlexTag = createSkullTag("Owner", Base64.getEncoder().encodeToString(
        "{\"timestamp\":1425828978186,\"profileId\":\"00000000000000000000000000000000\",\"profileName\":\"chunky\",\"isPublic\":true,\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/8e9fa6f8f2a1141524ff37c4df642dc2da29a6c05a35c38f43fe4919b84b34f\",\"metadata\":{\"model\":\"slim\"}}}}"
            .getBytes(StandardCharsets.UTF_8)));
    assertEquals(
        "http://textures.minecraft.net/texture/8e9fa6f8f2a1141524ff37c4df642dc2da29a6c05a35c38f43fe4919b84b34f",
        Head.getTextureUrl(validJsonAlexTag));
  }

  @Test
  public void testBadBase64Padding() { // test for #900
    CompoundTag validOwnerTag = createSkullTag("Owner",
        // the following base64 string has intentional wrong padding
        "eyAidGV4dHVyZXMiOiB7ICJTS0lOIjogeyJtZXRhZGF0YSI6IHsibW9kZWwiOiAic2xpbSJ9LCAidXJsIjogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFjZGU5NTRkYjMyNzY4NTIwMWY3ODVhNmIyNDhiNzNmZGM4OTgyYzJhZWQ0MzBmNjk3Y2RlYmVjOWI3ZTE0IiB9IH0gfQ=");
    assertEquals(
        "http://textures.minecraft.net/texture/1acde954db327685201f785a6b248b73fdc8982c2aed430f697cdebec9b7e14",
        Head.getTextureUrl(validOwnerTag));
  }

  @Test
  public void testLinebreaks() { // test for #764
    CompoundTag validOwnerTag = createSkullTag("Owner", Base64.getEncoder().encodeToString(
        "{ \"textures\":\n{ \"SKIN\": {\n  \"metadata\": {\n\"model\": \"slim\"},\n \"url\":\n \"http://textures.minecraft.net/texture/1acde954db327685201f785a6b248b73fdc8982c2aed430f697cdebec9b7e14\" } } }"
            .getBytes(StandardCharsets.UTF_8)));
    assertEquals(
        "http://textures.minecraft.net/texture/1acde954db327685201f785a6b248b73fdc8982c2aed430f697cdebec9b7e14",
        Head.getTextureUrl(validOwnerTag));
  }

  private static CompoundTag createSkullTag(String rootKey, String value) {
    CompoundTag skull = new CompoundTag();
    CompoundTag ownerTag = new CompoundTag();
    CompoundTag properties = new CompoundTag();
    CompoundTag valueTag = new CompoundTag();
    valueTag.add("Value", new StringTag(value));
    properties.add("textures", new ListTag(Tag.TAG_COMPOUND, Collections.singletonList(
        valueTag
    )));
    ownerTag.add("Properties", properties);
    skull.add(rootKey, ownerTag);
    return skull;
  }
}