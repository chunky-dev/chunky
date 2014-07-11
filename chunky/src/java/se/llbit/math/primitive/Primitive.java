/**
 * I consider this code to be so general that it should be public domain.
 * As far as I am concerned anyone can use this for anything they want,
 * but credits are appreciated if you do use my code =)
 * 2013 Jesper Öqvist <jesper@llbit.se>
 */
package se.llbit.math.primitive;

import se.llbit.math.AABB;
import se.llbit.math.Ray;

/**
 * An intersectable primitive piece of geometry
 * @author Jesper Öqvist <jesper.oqvist@cs.lth.se>
 */
public interface Primitive {
	/**
	 * Intersect the ray with this geometry.
	 * @param ray
	 * @param isect intersection object to store the intersection in
	 * @return {@code true} if there was an intersection
	 */
	boolean intersect(Ray ray);

	/**
	 * @return axis-aligned bounding box for the primitive
	 */
	AABB bounds();
}
