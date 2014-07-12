/* Copyright (c) 2012-2014 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.math;

/**
 * A transformation.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class Transform {
	private static class TransformPair extends Transform {
		private final Transform a;
		private final Transform b;

		protected TransformPair(Transform a, Transform b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public void apply(Vector3d v) {
			a.apply(v);
			b.apply(v);
		}

		@Override
		public void applyRotScale(Vector3d v) {
			a.applyRotScale(v);
			b.applyRotScale(v);
		}
	}

	public static final Transform NONE = new Transform();

	private Transform() {
	}

	/**
	 * Apply the transformation to a vertex.
	 * @param v
	 */
	public void apply(Vector3d v) {
	}

	/**
	 * Apply only rotation and scaling to a vertex.
	 * @param v
	 */
	public void applyRotScale(Vector3d v) {
	}

	private final Transform chain(Transform other) {
		if (this == NONE) {
			return other;
		} else {
			return new TransformPair(this, other);
		}

	}

	/**
	 * Translate by a vector
	 * @param translation The translation vector
	 */
	public final Transform translate(final Vector3d translation) {
		return chain(new Transform() {
			@Override
			public void apply(Vector3d v) {
				v.add(translation);
			}
		});
	}

	/**
	 * Translate by a vector
	 * @param translation The translation vector
	 */
	public final Transform translate(final double x, final double y, final double z) {
		return chain(new Transform() {
			@Override
			public void apply(Vector3d v) {
				v.x += x;
				v.y += y;
				v.z += z;
			}
		});
	}

	/**
	 * Scale by a scalar
	 * @param scale
	 */
	public final Transform scale(final double scale) {
		return chain(new Transform() {
			@Override
			public void apply(Vector3d v) {
				v.scale(scale);
			}
			@Override
			public void applyRotScale(Vector3d v) {
				v.scale(scale);
			}
		});
	}

	/**
	 * Rotation by 90 degrees around the Y axis
	 */
	public final Transform rotateY() {
		return chain(new Transform() {
			@Override
			public void apply(Vector3d o) {
		        double tmp = o.x;
		        o.x = -o.z;
		        o.z = tmp;
		    }
			@Override
			public void applyRotScale(Vector3d o) {
		        double tmp = o.x;
		        o.x = -o.z;
		        o.z = tmp;
		    }
		});
	}

	/**
	 * Rotation by 90 degrees around the X axis
	 */
	public final Transform rotateX() {
		return chain(new Transform() {
			@Override
			public void apply(Vector3d o) {
		        double tmp = o.y;
		        o.y = -o.z;
		        o.z = tmp;
		    }
			@Override
			public void applyRotScale(Vector3d o) {
		        double tmp = o.y;
		        o.y = -o.z;
		        o.z = tmp;
		    }
		});
	}

	/**
	 * Rotation by 90 degrees around the negative X axis
	 */
	public final Transform rotateNegX() {
		return chain(new Transform() {
			@Override
			public void apply(Vector3d o) {
		        double tmp = o.y;
		        o.y = o.z;
		        o.z = -tmp;
		    }
			@Override
			public void applyRotScale(Vector3d o) {
		        double tmp = o.y;
		        o.y = o.z;
		        o.z = -tmp;
		    }
		});
	}

	/**
	 * Rotation by 90 degrees around the Z axis
	 */
	public final Transform rotateZ() {
		return chain(new Transform() {
			@Override
			public void apply(Vector3d o) {
		        double tmp = o.x;
		        o.x = -o.y;
		        o.y = tmp;
		    }
			@Override
			public void applyRotScale(Vector3d o) {
		        double tmp = o.x;
		        o.x = -o.y;
		        o.y = tmp;
		    }
		});
	}

	/**
	 * Rotation by 90 degrees around the negative Z axis
	 */
	public final Transform rotateNegZ() {
		return chain(new Transform() {
			@Override
			public void apply(Vector3d o) {
		        double tmp = o.x;
		        o.x = o.y;
		        o.y = -tmp;
		    }
			@Override
			public void applyRotScale(Vector3d o) {
		        double tmp = o.x;
		        o.x = o.y;
		        o.y = -tmp;
		    }
		});
	}

	/**
	 * Mirror in Y axis
	 */
	public final Transform mirrorY() {
		return chain(new Transform() {
			@Override
			public void apply(Vector3d o) {
		        o.x = -o.x;
		        o.y = -o.y;
		    }
		});
	}

	/**
	 * Mirror in X axis
	 */
	public final Transform mirrorX() {
		return chain(new Transform() {
			@Override
			public void apply(Vector3d o) {
		        o.x = -o.x;
		        o.z = -o.z;
		    }
		});
	}

	/**
	 * Rotation around the Y axis
	 */
	public final Transform rotateY(final double angle) {
		return chain(new Transform() {
			private final Matrix3d mat = new Matrix3d();
			{
				mat.rotY(angle);
			}
			@Override
			public void apply(Vector3d v) {
				mat.transform(v);
		    }
			@Override
			public void applyRotScale(Vector3d v) {
				mat.transform(v);
		    }
		});
	}

	/**
	 * Rotation around the X axis
	 */
	public final Transform rotateX(final double angle) {
		return chain(new Transform() {
			private final Matrix3d mat = new Matrix3d();
			{
				mat.rotX(angle);
			}
			@Override
			public void apply(Vector3d v) {
				mat.transform(v);
		    }
			@Override
			public void applyRotScale(Vector3d v) {
				mat.transform(v);
		    }
		});
	}

	/**
	 * Rotation around the Z axis
	 */
	public final Transform rotateZ(final double angle) {
		return chain(new Transform() {
			private final Matrix3d mat = new Matrix3d();
			{
				mat.rotZ(angle);
			}
			@Override
			public void apply(Vector3d v) {
				mat.transform(v);
		    }
			@Override
			public void applyRotScale(Vector3d v) {
				mat.transform(v);
		    }
		});
	}
}
