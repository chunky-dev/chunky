package se.llbit.chunky.renderer;

import se.llbit.math.Vector3;
import se.llbit.math.primitive.Primitive;

import java.util.Collection;

public interface HasPrimitives {
  Collection<Primitive> primitives(Vector3 offset);
}
