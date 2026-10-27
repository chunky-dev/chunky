package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.MinecraftBlockTranslucent;
import se.llbit.chunky.entity.BannerDesign;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.StandingBanner;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.JsonArray;
import se.llbit.json.JsonObject;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.SpecificTag;

/**
 * A banner from Minecraft 1.12 or earlier.
 * <p>
 * The block itself is invisible and the banner is rendered as an entity so this block doesn't get
 * finalized but just creates the corresponding {@link StandingBanner}.
 */
public class LegacyBanner extends MinecraftBlockTranslucent {

  private static final BannerDesign.Color[] COLOR_MAP = {
    BannerDesign.Color.BLACK,
    BannerDesign.Color.RED,
    BannerDesign.Color.GREEN,
    BannerDesign.Color.BROWN,
    BannerDesign.Color.BLUE,
    BannerDesign.Color.PURPLE,
    BannerDesign.Color.ORANGE,
    BannerDesign.Color.SILVER,
    BannerDesign.Color.GRAY,
    BannerDesign.Color.PINK,
    BannerDesign.Color.LIME,
    BannerDesign.Color.YELLOW,
    BannerDesign.Color.LIGHT_BLUE,
    BannerDesign.Color.MAGENTA,
    BannerDesign.Color.CYAN,
    BannerDesign.Color.WHITE
  };

  private final int rotation;

  public LegacyBanner(String name, CompoundTag tag) {
    super(name, Texture.whiteWool);
    localIntersect = true;
    rotation = tag.get("Data").intValue(0);
  }

  @Override
  public boolean isBlockEntity() {
    return true;
  }

  @Override
  public Entity createBlockEntity(Vector3 position, CompoundTag entityTag) {
    return new StandingBanner(position, rotation, parseDesign(entityTag));
  }

  /**
   * Parse a banner design from the given Minecraft 1.12 or older banner entity tag and convert the
   * colors to 1.13+ values.
   *
   * @param entityTag Banner entity tag
   * @return Parsed design
   */
  public static JsonObject parseDesign(CompoundTag entityTag) {
    JsonObject design = new JsonObject();
    int base = COLOR_MAP[(entityTag.get("Base").intValue(15)) & 0b111].id;
    JsonArray patterns = new JsonArray();
    ListTag listTag = entityTag.get("Patterns").asList();
    for (SpecificTag tag : listTag) {
      CompoundTag patternTag = tag.asCompound();
      int color = COLOR_MAP[patternTag.get("Color").intValue() & 0b111].id;
      String pattern = patternTag.get("Pattern").stringValue();
      JsonObject patternJson = new JsonObject();
      patternJson.add("pattern", pattern);
      patternJson.add("color", color);
      patterns.add(patternJson);
    }
    design.add("base", base);
    design.add("patterns", patterns);
    return design;
  }
}
