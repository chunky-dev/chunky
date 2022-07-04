package se.llbit.util.mojangapi;

import com.google.gson.JsonParseException;
import se.llbit.chunky.renderer.scene.PlayerModel;
import se.llbit.log.Log;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class MinecraftSkin {
  public long timestamp;
  public String profileId;
  public String profileName;
  public MinecraftProfile.Textures textures;

  public String getSkinUrl() {
    if (textures == null || textures.skin == null) {
      return null;
    }
    return textures.skin.url;
  }

  public PlayerModel getPlayerModel() {
    if (textures != null && textures.skin != null && textures.skin.metadata != null) {
      if (textures.skin.metadata.model.equalsIgnoreCase("slim")) {
        return PlayerModel.ALEX;
      } else if (textures.skin.metadata.model.equalsIgnoreCase("classic")) {
        return PlayerModel.STEVE;
      }
    }

    if (profileId != null) {
      // Select default model based on UUID, as in Minecraft (Steve for even hashes)
      try {
        UUID uuid = UUID.fromString(profileId);
        return (uuid.hashCode() & 1) == 0 ? PlayerModel.STEVE : PlayerModel.ALEX;
      } catch (IllegalArgumentException e) {
        // ignore
      }
    }

    return PlayerModel.DEFAULT;
  }

  /**
   * Get the skin URL from the given base64-encoded texture string. This format is used in player
   * profiles and in entity tags of player heads.
   *
   * @param textureBase64 Base64-encoded texture string
   * @return Skin information
   */
  public static Optional<MinecraftSkin> getSkinFromEncodedTextures(String textureBase64) {
    String decoded = new String(Base64.getDecoder().decode(MojangApi.fixBase64Padding(textureBase64)));
    try {
      try {
        return Optional.of(MojangApi.GSON.fromJson(decoded, MinecraftSkin.class));
      } catch (JsonParseException e) {
        Log.warn("Could not get skin from json: " + decoded, e);
      }
    } catch (IllegalArgumentException e) {
      // base64 decoding error
      Log.warn("Could not get skull texture", e);
    }
    return Optional.empty();
  }
}
