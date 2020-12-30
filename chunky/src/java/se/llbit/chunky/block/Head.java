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

  private static final Pattern SKIN_URL_FROM_OBJECT = Pattern
      .compile("\"?SKIN\"?\\s*:\\s*\\{.+?\"?url\"?\\s*:\\s*\"(.+?)\"");
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
    return type == Kind.PLAYER;
  }

  @Override
  public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    String textureUrl = getTextureUrl(entityTag);
    return textureUrl != null ? new HeadEntity(position, textureUrl, rotation, 1)
        : new SkullEntity(position, type, rotation, 1);
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
        String decoded = new String(Base64.getDecoder().decode(textureBase64));
        // the decoded string might not be valid json (sometimes keys are not quoted)
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
}
