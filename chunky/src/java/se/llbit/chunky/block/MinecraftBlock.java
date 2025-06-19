package se.llbit.chunky.block;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Box;
import se.llbit.math.primitive.Primitive;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A simple opaque block with a single texture.
 */
public class MinecraftBlock extends Block {
  public static final Material STONE = new MinecraftBlock("stone", Texture.stone);

  public MinecraftBlock(String name, Texture texture) {
    super("minecraft:" + name, texture);
    opaque = true;
    solid = true;
  }

  @Override
  public Collection<Primitive> getPrimitives(Transform transform) {
    // TODO check uv mapping and flip/rotate textures if needed
    // TODO apply this block's material properties to the primitives

    Collection<Primitive> primitives = new LinkedList<>();
    Box box = new Box(0, 1, 0, 1, 0, 1);
    box.transform(transform);
    box.addFrontFaces(primitives, texture, new Vector4(0, 1, 0, 1));
    box.addBackFaces(primitives, texture, new Vector4(0, 1, 0, 1));
    box.addLeftFaces(primitives, texture, new Vector4(0, 1, 0, 1));
    box.addRightFaces(primitives, texture, new Vector4(0, 1, 0, 1));
    box.addTopFaces(primitives, texture, new Vector4(0, 1, 0, 1));
    box.addBottomFaces(primitives, texture, new Vector4(0, 1, 0, 1));
    return primitives;
  }
}
