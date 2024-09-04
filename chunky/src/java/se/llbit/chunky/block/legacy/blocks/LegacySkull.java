package se.llbit.chunky.block.legacy.blocks;

import static se.llbit.chunky.block.minecraft.Head.getTextureUrl;

import se.llbit.chunky.block.MinecraftBlockTranslucent;
import se.llbit.chunky.block.minecraft.EmptyModelBlock;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.HeadEntity;
import se.llbit.chunky.entity.SkullEntity;
import se.llbit.chunky.entity.SkullEntity.Kind;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray;
import se.llbit.math.Ray2;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

/**
 * A skull or player head from Minecraft 1.12 or earlier.
 * <p>
 * The block itself is invisible and the skull is rendered as an entity so this block doesn't get
 * finalized but just creates the corresponding {@link HeadEntity} or {@link SkullEntity} instead.
 */
public class LegacySkull extends EmptyModelBlock {

  private final int placement;

  public LegacySkull(String name, CompoundTag tag) {
    super(name, Texture.steve);
    this.placement = tag.get("Data").intValue(0);
    invisible = true;
  }

  @Override
  public boolean isBlockEntity() {
    return true;
  }

  @Override
  public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    Kind kind = getSkullKind(entityTag.get("SkullType").byteValue(0));
    int rotation = entityTag.get("Rot").byteValue(0);
    if (kind == Kind.PLAYER) {
      String textureUrl = getTextureUrl(entityTag);
      if (textureUrl != null) {
        return new HeadEntity(position, textureUrl, rotation, placement);
      }
    }
    return new SkullEntity(position, kind, rotation, placement);
  }

  private static Kind getSkullKind(int skullType) {
    switch (skullType) {
      case 0:
        return Kind.SKELETON;
      case 1:
        return Kind.WITHER_SKELETON;
      case 2:
        return Kind.ZOMBIE;
      case 3:
      default:
        return Kind.PLAYER;
      case 4:
        return Kind.CREEPER;
      case 5:
        return Kind.DRAGON;
    }
  }
}
