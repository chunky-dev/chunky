package se.llbit.chunky.entity;

import se.llbit.chunky.block.Block;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.Tag;

import java.util.Collection;

public class BlockDisplayEntity extends Entity {
  private final Block block;
  private final CompoundTag tag;

  public BlockDisplayEntity(Vector3 position, Block block, CompoundTag tag) {
    super(position);
    this.block = block;
    this.tag = tag;
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    // TODO add support for matrix form (ie. 16 values in row-major order)

    Transform transform = getTransform(tag.get("transformation"))
      .translate(position)
      .translate(offset);

    return block.getPrimitives(transform);
  }

  private Transform getTransform(Tag transformation) {
    return Transform.NONE
      .rotateQuaternion( // TODO support axis-angle form (ie. angle of rotation and axis vector)
        new Vector4(
          transformation.get("right_rotation").get(1).floatValue(),
          transformation.get("right_rotation").get(2).floatValue(),
          -transformation.get("right_rotation").get(3).floatValue(), // fix rotation direction
          -transformation.get("right_rotation").get(0).floatValue() // fix rotation direction
        )
      )
      .scale(
        transformation.get("scale").get(0).floatValue(1),
        transformation.get("scale").get(1).floatValue(1),
        transformation.get("scale").get(2).floatValue(1)
      )
      .rotateQuaternion( // TODO support axis-angle form (ie. angle of rotation and axis vector)
        new Vector4(
          transformation.get("left_rotation").get(1).floatValue(),
          transformation.get("left_rotation").get(2).floatValue(),
          -transformation.get("left_rotation").get(3).floatValue(), // fix rotation direction
          -transformation.get("left_rotation").get(0).floatValue() // fix rotation direction
        )
      )
      .translate(
        transformation.get("translation").get(0).floatValue(0),
        transformation.get("translation").get(1).floatValue(0),
        transformation.get("translation").get(2).floatValue(0)
      );
  }

  @Override
  public JsonValue toJson() {
    // TODO can we even serialize this or do we put the tag into a block palette?
    return new JsonObject();
  }
}
