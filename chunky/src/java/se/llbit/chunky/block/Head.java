package se.llbit.chunky.block;

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
import se.llbit.util.MCDownloader;

public class Head extends MinecraftBlockTranslucent {

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
        return MCDownloader.getSkinUrlFromEncodedTextures(textureBase64);
      } catch (IllegalArgumentException e) {
        // base64 decoding error
        Log.warn("Could not get skull texture", e);
      }
    }
    return null;
  }
}
