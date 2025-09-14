package se.llbit.chunky.block.jsonmodels;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.model.QuadModel;
import se.llbit.chunky.model.Tint;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Quad;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;

import java.util.Collection;
import java.util.LinkedList;

public class QuadBlock extends AbstractModelBlock {
  private final boolean isEntity;
  public boolean supportsOpacity = true; // some blocks only support full or zero opacity and round alpha values to 0 or 1

  public QuadBlock(String name, Texture texture, Quad[] quads, Texture[] textures, Tint[] tints, boolean isEntity) {
    super(name, texture);
    localIntersect = true;
    opaque = false;
    solid = false;
    invisible = isEntity;
    this.isEntity = isEntity;
    model = new Model(quads, textures, tints);
  }

  @Override
  public boolean isBlockEntity() {
    // TODO we could further optimize this by only putting the out-of-bounds quads into the BVH and rendering the rest (if any) as block
    return isEntity;
  }

  @Override
  public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    return new Entity(position) {
      @Override
      public Collection<Primitive> primitives(Vector3 offset) {
        Collection<Primitive> faces = new LinkedList<>();
        Transform transform =
          Transform.NONE.translate(
            position.x + offset.x, position.y + offset.y, position.z + offset.z);
        for (int i = 0; i < ((Model) model).quads.length; i++) {
          Quad quad = ((Model) model).quads[i];
          Texture texture = ((Model) model).textures[i];
          Material material = new TextureMaterial(texture);
          material.emittance = emittance;
          material.specular = specular;
          material.ior = ior;
          quad.addTriangles(faces, material, transform);
        }
        return faces;
      }

      @Override
      public JsonValue toJson() {
        // TODO
        return new JsonObject();
      }
    };
  }

  private static class Model extends QuadModel {
    final Quad[] quads;
    final Texture[] textures;
    final Tint[] tints;

    Model(Quad[] quads, Texture[] textures) {
      this(quads, textures, null);
    }

    Model(Quad[] quads, Texture[] textures, Tint[] tints) {
      this.quads = quads;
      this.textures = textures;
      this.tints = tints;
    }

    @Override
    public Quad[] getQuads() {
      return quads;
    }

    @Override
    public Texture[] getTextures() {
      return textures;
    }

    @Override
    public Tint[] getTints() {
      return tints;
    }
  }
}
