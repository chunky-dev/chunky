package se.llbit.chunky.block.legacy.blocks;

import se.llbit.chunky.block.Block;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.WallBanner;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

/**
 * A wall banner from Minecraft 1.12 or earlier.
 * <p>
 * The block itself is invisible and the wall banner is rendered as an entity so this block doesn't
 * get finalized but just creates the corresponding {@link WallBanner}.
 */
public class LegacyWallBanner extends MinecraftBlockTranslucent {

  private final int facing;

  public LegacyWallBanner(String name, CompoundTag tag) {
    super(name, Texture.whiteWool);
    localIntersect = true;
    invisible = true;
    facing = tag.get("Data").intValue(2);
  }

  @Override
  public boolean isBlockEntity() {
    return true;
  }

  @Override
  public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    return new WallBanner(position, facing, LegacyBanner.parseDesign(entityTag));
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return false;
  }
}
