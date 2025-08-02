package se.llbit.chunky.world.material;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.json.JsonObject;
import se.llbit.math.ColorUtil;
import se.llbit.math.Ray;

public class DyedTextureMaterial extends Material {

  public enum DyeColor {
    WHITE(0, 0xF9FFFE),
    ORANGE(1, 0xF9801D),
    MAGENTA(2, 0xC74EBD),
    LIGHT_BLUE(3, 0x3AB3DA),
    YELLOW(4, 0xFED83D),
    LIME(5, 0x80C71F),
    PINK(6, 0xF38BAA),
    GRAY(7, 0x474F52),
    LIGHT_GRAY(8, 0x9D9D97),
    CYAN(9, 0x169C9C),
    PURPLE(10, 0x8932B8),
    BLUE(11, 0x3C44AA),
    BROWN(12, 0x835432),
    GREEN(13, 0x5E7C16),
    RED(14, 0xB02E26),
    BLACK(15, 0x1D1D21);

    public final int id;
    public final int colorDecimal;

    private static final DyeColor[] colorByIdMap = new DyeColor[]{
      WHITE,
      ORANGE,
      MAGENTA,
      LIGHT_BLUE,
      YELLOW,
      LIME,
      PINK,
      GRAY,
      LIGHT_GRAY,
      CYAN,
      PURPLE,
      BLUE,
      BROWN,
      GREEN,
      RED,
      BLACK,
    };

    public static DyeColor get(int id) {
      return colorByIdMap[id & 0xF];
    }

    DyeColor(int id, int colorVal) {
      this.id = id;
      colorDecimal = colorVal;
    }
  }
  private int color;
  private final float[] colorRGBA = new float[4];

  public DyedTextureMaterial(DyeColor color, Texture texture) {
    this(color.colorDecimal, texture);
  }

  public DyedTextureMaterial(int color, Texture texture) {
    super("dyed_texture", texture);
    updateColor(color);
  }

  public void updateColor(DyeColor color) {
    updateColor(color.colorDecimal);
  }

  public void updateColor(int color) {
    this.color = color;
    ColorUtil.getRGBAComponents(color, colorRGBA);
    ColorUtil.toLinear(colorRGBA);
  }

  public int getColorInt() {
    return color;
  }

  @Override
  public void getColor(Ray ray) {
    super.getColor(ray);
    if (ray.color.w > Ray.EPSILON) {
      ray.color.x *= colorRGBA[0];
      ray.color.y *= colorRGBA[1];
      ray.color.z *= colorRGBA[2];
    }
  }

  @Override
  public float[] getColor(double u, double v) {
    float[] color = super.getColor(u, v);
    if (color[3] > Ray.EPSILON) {
      color = color.clone();
      color[0] *= colorRGBA[0];
      color[1] *= colorRGBA[1];
      color[2] *= colorRGBA[2];
    }
    return color;
  }

  @Override
  public void loadMaterialProperties(JsonObject json) {
    super.loadMaterialProperties(json);
    updateColor(json.get("color").asInt(DyeColor.WHITE.colorDecimal));
  }

  public void saveMaterialProperties(JsonObject json) {
    json.add("ior", this.ior);
    json.add("specular", this.specular);
    json.add("emittance", this.emittance);
    json.add("roughness", this.roughness);
    json.add("metalness", this.metalness);
    json.add("color", this.color);
  }
}