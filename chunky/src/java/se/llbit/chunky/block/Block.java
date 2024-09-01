package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.texture.AbstractTexture;
import se.llbit.chunky.world.Material;
import se.llbit.json.JsonString;
import se.llbit.json.JsonValue;
import se.llbit.math.AABB;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;

import java.util.Random;

public abstract class Block extends Material {
  private final static AABB block =  new AABB(0, 1, 0, 1,0, 1);

  /**
   * Set to true if there is a local intersection model for this block. If this is set to
   * <code>false</code> (default), this block is assumed to be an opaque cube block and {@link
   * #intersect(Ray, Scene)} will never be called.
   */
  public boolean localIntersect = false;

  /**
   * Invisible blocks are not rendered as regular voxels (they are not added to the voxel octree).
   * This is used for blocks that are rendered as entities, and blocks that are not implemented
   * yet.
   */
  public boolean invisible = false;

  public Block(String name, AbstractTexture texture) {
    super(name, texture);
  }

  /**
   * Get the number of faces on this block.
   */
  public int faceCount() {
    return 6;
  }

  /**
   * Sample a random point on this block. Coordinates are normalized to be in [0, 1].
   * @param loc  Location vector where the point is stored.
   * @param rand Random number source.
   */
  public void sample(int face, Vector3 loc, Random rand) {
    block.sampleFace(face, loc, rand);
  }

  /**
   * Get the surface area of this face of the block.
   */
  public double surfaceArea(int face) {
    return block.faceSurfaceArea(face);
  }

  /**
   * Intersect the given ray in the given scene with this block and update the Ray's color, distance
   * and origin accordingly. Note that the alpha component of the ray color must be positive if and
   * only if it hits (i.e. this method returns true) and zero otherwise.
   *
   * @param ray   Ray
   * @param scene Scene
   * @return True if the ray hit this block, false if not
   */
  public boolean intersect(Ray ray, Scene scene) {
    ray.t = Double.POSITIVE_INFINITY;
    if (block.intersect(ray)) {
      float[] color = texture.getColor(ray.u, ray.v);
      if (color[3] > Ray.EPSILON) {
        ray.color.set(color);
        ray.distance += ray.tNext;
        ray.o.scaleAdd(ray.tNext, ray.d);
        return true;
      }
    }
    return false;
  }

  @Override
  public JsonValue toJson() {
    return new JsonString(name);
  }

  public String description() {
    return "";
  }

  @Override
  public String toString() {
    return name;
  }

  public boolean isBlockEntity() {
    return false;
  }

  public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    throw new Error("This block type can not be converted to a block entity: "
        + getClass().getSimpleName());
  }

  public boolean isEntity() {
    return false;
  }

  /**
   * If this returns true, the block won't be removed from the octree even if this is an entity
   * (i.e. {@link #isEntity()} returns true). This can be used for blocks that also contain
   * entities, e.g. candle (where the candle flame is an entity).
   */
  public boolean isBlockWithEntity() {
    return false;
  }

  public Entity toEntity(Vector3 position) {
    throw new Error("This block type can not be converted to an entity: "
        + getClass().getSimpleName());
  }

  /**
   * If this returns true, the {@link #getNewTagWithBlockEntity(Tag, CompoundTag)} method will be
   * invoked if a block entity is found for this block.
   * This is used to handle legacy blocks that used to have information stored in block entities
   * but are blocks now (e.g. colored beds).
   */
  public boolean isModifiedByBlockEntity() {
    return false;
  }

  /**
   * Given this block, its original tag and the block entity tag, create a new tag for the modified
   * block.
   * This is used to handle legacy blocks that used to have information stored in block entities
   * but are blocks now (e.g. colored beds).
   * @param blockTag Tag of this block (not to be modified)
   * @param entityTag Block entity data
   * @return A new tag that will be used to create a new block that will replace this block
   */
  public Tag getNewTagWithBlockEntity(Tag blockTag, CompoundTag entityTag) {
    return null;
  }

  /**
   * Does this block use biome tint for its rendering
   */
  public boolean isBiomeDependant() {
    return isWaterFilled();
  }
}
