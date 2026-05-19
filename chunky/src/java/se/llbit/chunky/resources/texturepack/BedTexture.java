package se.llbit.chunky.resources.texturepack;

import se.llbit.chunky.resources.Texture;

public class BedTexture {
  public static class Textures {
    public final Texture headUp = new Texture();
    public final Texture headDown = new Texture();
    public final Texture headNorth = new Texture();
    public final Texture headEast = new Texture();
    public final Texture headWest = new Texture();
    public final Texture footUp = new Texture();
    public final Texture footDown = new Texture();
    public final Texture footSouth = new Texture();
    public final Texture footEast = new Texture();
    public final Texture footWest = new Texture();
  }

  private BedTexture() {
  }

  /**
   * Create a texture loader for the given bed color that uses the 26.2 textures by default and falls back to
   * older texture formats if they don't exist.
   *
   * @param color Bed color
   * @return Texture loader for these bed colors
   */
  public static TextureLoader createTextureLoader(String color, BedTexture.Textures textures) {
    TextureLoader chaosCubedTextureLoader = new AllTextures(
      new SimpleTexture("assets/minecraft/textures/block/bed_head_north", textures.headNorth),
      new SimpleTexture("assets/minecraft/textures/block/bed_down", textures.headDown),
      new SimpleTexture("assets/minecraft/textures/block/" + color + "_bed_head_east", textures.headEast),
      new SimpleTexture("assets/minecraft/textures/block/" + color + "_bed_head_west", textures.headWest),
      new SimpleTexture("assets/minecraft/textures/block/" + color + "_bed_head_up", textures.headUp),
      new SimpleTexture("assets/minecraft/textures/block/" + color + "_bed_foot_south", textures.footSouth),
      new SimpleTexture("assets/minecraft/textures/block/" + color + "_bed_foot_east", textures.footEast),
      new SimpleTexture("assets/minecraft/textures/block/" + color + "_bed_foot_west", textures.footWest),
      new SimpleTexture("assets/minecraft/textures/block/" + color + "_bed_foot_up", textures.footUp));

    if ("red".equals(color)) { // only red existed before 1.12
      return new AlternateTextures(
        chaosCubedTextureLoader,
        new BedTextureAdapter26_2(textures, targetTexture -> new AlternateTextures(
          new SimpleTexture("assets/minecraft/textures/entity/bed/red", targetTexture),
          new BedTextureAdapter1_12(targetTexture)
        )));
    }
    if ("light_gray".equals(color)) { // light_gray was silver before 1.13
      return new AlternateTextures(
        chaosCubedTextureLoader,
        new BedTextureAdapter26_2(textures, targetTexture -> new AlternateTextures(
          new SimpleTexture("assets/minecraft/textures/entity/bed/light_gray", targetTexture),
          new SimpleTexture("assets/minecraft/textures/entity/bed/silver", targetTexture)
        )));
    }
    return new AlternateTextures(
      chaosCubedTextureLoader,
      new BedTextureAdapter26_2(textures, targetTexture ->
        new SimpleTexture("assets/minecraft/textures/entity/bed/" + color, targetTexture)
      ));
  }
}
