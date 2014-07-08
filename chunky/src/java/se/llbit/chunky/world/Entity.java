package se.llbit.chunky.world;

import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.math.Ray;
import se.llbit.math.Vector3d;

abstract public class Entity {
	protected final Vector3d position;

	protected Entity(Vector3d position) {
		this.position = new Vector3d(position);
	}

	abstract public boolean intersect(Scene scene, Ray ray);
}
