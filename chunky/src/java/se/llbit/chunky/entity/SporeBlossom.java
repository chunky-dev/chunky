package se.llbit.chunky.entity;

import java.util.Collection;
import se.llbit.chunky.model.minecraft.SporeBlossomModel;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.primitive.Primitive;
import se.llbit.util.JsonUtil;

public class SporeBlossom extends Entity {

  public SporeBlossom(Vector3 position) {
    super(position);
  }

  public SporeBlossom(JsonObject json) {
    super(JsonUtil.vec3FromJsonObject(json.get("position")));
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    return SporeBlossomModel.primitives(Transform.NONE.translate(position).translate(offset));
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "sporeBlossom");
    json.add("position", position.toJson());
    return json;
  }

  public static Entity fromJson(JsonObject json) {
    return new SporeBlossom(json);
  }
}
