package se.llbit.chunky.block;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.HeadEntity;
import se.llbit.chunky.entity.SkullEntity;
import se.llbit.chunky.entity.SkullEntity.Kind;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.EntityTexture;
import se.llbit.log.Log;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;

public class Head extends MinecraftBlockTranslucent {

  // the decoded string might not be valid json (sometimes keys are not quoted)
  // so we use a regex to extract the skin url
  private static final Pattern SKIN_URL_FROM_OBJECT = Pattern
      .compile("\"?SKIN\"?\\s*:\\s*\\{.+?\"?url\"?\\s*:\\s*\"(.+?)\"", Pattern.DOTALL);
  private final String description;
  private final int rotation;
  private final SkullEntity.Kind type;

  public Head(String name, EntityTexture texture, SkullEntity.Kind type, int rotation) {
    super(name, texture);
    localIntersect = true;
    invisible = true;
    description = "rotation=" + rotation;
    this.type = type;
    this.rotation = rotation;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public boolean isEntity() {
    return type != Kind.PLAYER;
  }

  @Override
  public Entity toEntity(Vector3 position) {
    return new SkullEntity(position, type, rotation, 1);
  }

  @Override
  public boolean isBlockEntity() {
    return true;
  }

  @Override
  public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    if (type == Kind.PLAYER) {
      String textureUrl = getTextureUrl(entityTag);
      return textureUrl != null ? new HeadEntity(position, textureUrl, rotation, 1)
          : new SkullEntity(position, type, rotation, 1);
    } else {
      return null;
    }
  }

  public static String getTextureUrl(CompoundTag entityTag) {
    Tag ownerTag = entityTag.get("Owner"); // used by skulls
    if (!ownerTag.isCompoundTag()) {
      ownerTag = entityTag.get("SkullOwner"); // used by player heads
    }
    String textureBase64 = ownerTag.get("Properties").get("textures").get(0)
        .get("Value").stringValue();
    if (!textureBase64.isEmpty()) {
      try {
        String decoded = new String(Base64.getDecoder().decode(fixBase64Padding(textureBase64)));
        Matcher matcher = SKIN_URL_FROM_OBJECT.matcher(decoded);
        if (matcher.find()) {
          return matcher.group(1);
        } else {
          Log.warn("Could not get skull texture from json: " + decoded);
        }
      } catch (IllegalArgumentException e) {
        // base64 decoding error
        Log.warn("Could not get skull texture", e);
      }
    }
    return null;
  }

  /**
   * Get the given Base64 string with proper padding. Sometimes the base64-encoded texture isn't
   * padded properly which would cause an IllegalArgumentException (wrong 4-byte ending unit)
   * because Java can't handle that.
   *
   * @param base64String Base64 string with or without proper padding
   * @return Base64 string with proper padding
   */
  private static String fixBase64Padding(String base64String) {
    // the length of a base64 string must be a multiple of 4
    int missingPadding = (4 - (base64String.length() % 4)) % 4;
    if (missingPadding == 0) {
      return base64String;
    }

    StringBuilder fixedBase64 = new StringBuilder(base64String);
    for (int i = 0; i < missingPadding; i++) {
      fixedBase64.append("=");
    }
    return fixedBase64.toString();
  }
}
