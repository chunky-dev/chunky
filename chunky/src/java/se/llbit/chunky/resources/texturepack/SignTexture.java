package se.llbit.chunky.resources.texturepack;

import se.llbit.chunky.resources.Texture;

public class SignTexture {
  private SignTexture() {
  }

  /**
   * Create a texture loader for the given sign material that uses the 26.2 textures by default and falls back to
   * older texture formats if they don't exist.
   *
   * @param material Sign material
   * @return Texture loader for the given sign material
   */
  public static TextureLoader createSignTextureLoader(String material, Texture texture) {
    TextureLoader chaosCubedAdapter = "oak".equals(material)
      ? new SignTextureAdapter26_2(texture,
      t -> new AlternateTextures(
        new SimpleTexture("assets/minecraft/textures/entity/signs/oak", t),// MC 1.14
        new SimpleTexture("assets/minecraft/textures/entity/sign", t),// MC 1.6
        new SimpleTexture("item/sign", t))) // before MC 1.6
      : new SignTextureAdapter26_2(texture,
      t -> new SimpleTexture("assets/minecraft/textures/entity/signs/" + material, t));

    return new AlternateTextures(
      new SimpleTexture("assets/minecraft/textures/block/" + material + "_sign", texture),
      chaosCubedAdapter);
  }

  /**
   * Create a texture loader for the given hanging sign material that uses the 26.2 textures by default and falls back to
   * older texture formats if they don't exist.
   *
   * @param material Hanging sign material
   * @return Texture loader for the given hanging sign material
   */
  public static TextureLoader createHangingSignTextureLoader(String material, Texture texture) {
    return new AlternateTextures(
      new SimpleTexture("assets/minecraft/textures/block/" + material + "_hanging_sign", texture),
      new HangingSignTextureAdapter26_2(texture,
        t -> new SimpleTexture("assets/minecraft/textures/entity/signs/hanging/" + material, t)));
  }
}
