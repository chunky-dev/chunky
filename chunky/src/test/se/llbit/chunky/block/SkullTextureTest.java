package se.llbit.chunky.block;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import org.junit.Test;
import se.llbit.chunky.block.block.Head;
import se.llbit.chunky.renderer.scene.PlayerModel;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;
import se.llbit.util.mojangapi.MinecraftSkin;

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

  @Test
  public void testCape() { // test for #1001
    CompoundTag capeTag = createSkullTag("Owner", Base64.getEncoder().encodeToString(
        ("{\n"
            + "  \"timestamp\" : 1633816089260,\n"
            + "  \"profileId\" : \"94d67f2fd039419b8958abe6b25916b0\",\n"
            + "  \"profileName\" : \"leMaik\",\n"
            + "  \"textures\" : {\n"
            + "    \"SKIN\" : {\n"
            + "      \"url\" : \"http://textures.minecraft.net/texture/3b60a1f6d562f52aaebbf1434f1de147933a3affe0e764fa49ea057536623cd3\",\n"
            + "      \"metadata\" : {\n"
            + "        \"model\" : \"slim\"\n"
            + "      }\n"
            + "    },\n"
            + "    \"CAPE\" : {\n"
            + "      \"url\" : \"http://textures.minecraft.net/texture/2340c0e03dd24a11b15a8b33c2a7e9e32abb2051b2481d0ba7defd635ca7a933\"\n"
            + "    }\n"
            + "  }\n"
            + "}").getBytes(StandardCharsets.UTF_8)));
    assertEquals(
        "http://textures.minecraft.net/texture/3b60a1f6d562f52aaebbf1434f1de147933a3affe0e764fa49ea057536623cd3",
        Head.getTextureUrl(capeTag));
  }

  @Test
  public void testNoSkins() { // test for #1331
    MinecraftSkin skin = MinecraftSkin.getSkinFromEncodedTextures(Base64.getEncoder().encodeToString(
      ("{\n"
        + "  \"timestamp\" : 1633816089260,\n"
        + "  \"profileId\" : \"94d67f2fd039419b8958abe6b25916b0\",\n"
        + "  \"profileName\" : \"leMaik\",\n"
        + "  \"textures\" : {}\n"
        + "}").getBytes(StandardCharsets.UTF_8))).get();
    assertNull(skin.getSkinUrl());
    assertEquals(PlayerModel.STEVE, skin.getPlayerModel());
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