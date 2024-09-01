package se.llbit.chunky.entity;

import se.llbit.chunky.resources.*;
import se.llbit.chunky.resources.texture.BitmapTexture;
import se.llbit.chunky.resources.texturepack.SimpleTexture;
import se.llbit.log.Log;
import se.llbit.math.ColorUtil;

import java.util.HashMap;
import java.util.Map;

public class BannerDesign {
  private static final Map<String, Pattern> patternByName = new HashMap<>();
  private static final Map<String, Pattern> patternByLegacyName = new HashMap<>();

  static {
    resetPatterns();
  }

  private static void registerPattern(String namespacedName, String legacyName) {
    Pattern pattern = new Pattern(namespacedName);
    patternByName.put(namespacedName, pattern);
    patternByLegacyName.put(legacyName, pattern);
  }

  public static void registerPattern(String namespacedName, Pattern pattern) {
    patternByName.put(namespacedName, pattern);
  }

  public static boolean containsPattern(String namespacedName) {
    return patternByName.containsKey(namespacedName);
  }

  public static Pattern getPattern(String patternName) {
    Pattern pattern = patternByName.get(patternName);
    if (pattern == null) {
      pattern = patternByLegacyName.get(patternName);
    }
    return pattern;
  }

  public static void resetPatterns() {
    patternByName.clear();
    patternByLegacyName.clear();

    // hard-coded pre-24w10a patterns with legacy aliases
    registerPattern("minecraft:stripe_bottom", "bs");
    registerPattern("minecraft:stripe_top", "ts");
    registerPattern("minecraft:stripe_left", "ls");
    registerPattern("minecraft:stripe_right", "rs");
    registerPattern("minecraft:stripe_center", "cs");
    registerPattern("minecraft:stripe_middle", "ms");
    registerPattern("minecraft:stripe_downright", "drs");
    registerPattern("minecraft:stripe_downleft", "dls");
    registerPattern("minecraft:small_stripes", "ss");
    registerPattern("minecraft:cross", "cr");
    registerPattern("minecraft:straight_cross", "sc");
    registerPattern("minecraft:diagonal_left", "ld");
    registerPattern("minecraft:diagonal_right", "rud"); // swapped in legacy mc, see rd
    registerPattern("minecraft:diagonal_up_left", "lud");
    registerPattern("minecraft:diagonal_up_right", "rd"); // swapped in legacy mc, see rud
    registerPattern("minecraft:half_vertical", "vh");
    registerPattern("minecraft:half_vertical_right", "vhr");
    registerPattern("minecraft:half_horizontal", "hh");
    registerPattern("minecraft:half_horizontal_bottom", "hhb");
    registerPattern("minecraft:square_bottom_left", "bl");
    registerPattern("minecraft:square_bottom_right", "br");
    registerPattern("minecraft:square_top_left", "tl");
    registerPattern("minecraft:square_top_right", "tr");
    registerPattern("minecraft:triangle_bottom", "bt");
    registerPattern("minecraft:triangle_top", "tt");
    registerPattern("minecraft:triangles_bottom", "bts");
    registerPattern("minecraft:triangles_top", "tts");
    registerPattern("minecraft:circle", "mc");
    registerPattern("minecraft:rhombus", "mr");
    registerPattern("minecraft:border", "bo");
    registerPattern("minecraft:curly_border", "cbo");
    registerPattern("minecraft:bricks", "bri");
    registerPattern("minecraft:gradient", "gra");
    registerPattern("minecraft:gradient_up", "gru");
    registerPattern("minecraft:creeper", "cre");
    registerPattern("minecraft:skull", "sku");
    registerPattern("minecraft:flower", "flo");
    registerPattern("minecraft:mojang", "moj");
    registerPattern("minecraft:piglin", "pig");
  }

  public enum Color {
    WHITE(0, 0xFFFFFF),
    ORANGE(1, 0xD76F19),
    MAGENTA(2, 0xCF51C5),
    LIGHT_BLUE(3, 0x39AFD5),
    YELLOW(4, 0xE6C438),
    LIME(5, 0x89D520),
    PINK(6, 0xCF7691),
    GRAY(7, 0x6E7B80),
    SILVER(8, 0xCCCCCC),
    CYAN(9, 0x1CC6C6),
    PURPLE(10, 0x9536C9),
    BLUE(11, 0x454FC4),
    BROWN(12, 0x96613A),
    GREEN(13, 0x81AA1E),
    RED(14, 0xCC352C),
    BLACK(15, 0x000000);

    public final int id;
    public final float[] rgbaColor;

    private static final Map<String, Color> textColorMap = new HashMap<>();
    private static final Color[] textColorByIdMap = new Color[]{
      WHITE,
      ORANGE,
      MAGENTA,
      LIGHT_BLUE,
      YELLOW,
      LIME,
      PINK,
      GRAY,
      SILVER,
      CYAN,
      PURPLE,
      BLUE,
      BROWN,
      GREEN,
      RED,
      BLACK
    };

    static {
      textColorMap.put("white", WHITE);
      textColorMap.put("orange", ORANGE);
      textColorMap.put("magenta", MAGENTA);
      textColorMap.put("light_blue", LIGHT_BLUE);
      textColorMap.put("yellow", YELLOW);
      textColorMap.put("lime", LIME);
      textColorMap.put("pink", PINK);
      textColorMap.put("gray", GRAY);
      textColorMap.put("silver", SILVER);
      textColorMap.put("cyan", CYAN);
      textColorMap.put("purple", PURPLE);
      textColorMap.put("blue", BLUE);
      textColorMap.put("brown", BROWN);
      textColorMap.put("green", GREEN);
      textColorMap.put("red", RED);
      textColorMap.put("black", BLACK);
    }

    Color(int id, int color) {
      this.id = id;
      this.rgbaColor = new float[4];
      ColorUtil.getRGBAComponents(color, this.rgbaColor);
    }

    public static Color get(String color) {
      return textColorMap.getOrDefault(color, Color.BLACK);
    }

    public static Color get(int id) {
      return textColorByIdMap[id & 0xF];
    }
  }

  public static class Pattern {
    private final String pattern;

    public Pattern(String pattern) {
      this.pattern = pattern;
    }

    public BitmapImage getBitmap() {
      String[] namespaceFilename = pattern.split(":");
      String texId = "assets/" + namespaceFilename[0] + "/textures/entity/banner/" + namespaceFilename[1];
      BitmapTexture texture = TextureCache.get(texId);
      if (texture == null) {
        texture = new BitmapTexture();
        TextureCache.put(texId, texture);

        if (!ResourcePackLoader.loadResources(
          ResourcePackTextureLoader.singletonLoader(pattern, new SimpleTexture(texId, texture)))
        ) {
          // not completed singleton load --> failure
          Log.infof("Failed to load banner pattern: %s", texId);
        }
      }
      return texture.getBitmap();
    }
  }
}
