package se.llbit.chunky.renderer;
import org.apache.commons.math3.util.FastMath;

import se.llbit.nbt.AnyTag;

/**
 * Postprocessing modes
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("javadoc")
public enum Postprocess {
	NONE {
		@Override
		public String toString() {
			return "None";
		}
	},
	GAMMA {
		@Override
		public String toString() {
			return "Gamma correction";
		}
	},
	TONEMAP1 {
		@Override
		public String toString() {
			return "Tonemap op1";
		}
	};

	public static final int DEFAULT = GAMMA.ordinal();

	public static final Postprocess[] values = values();

	public static Postprocess get(int index) {
		index = FastMath.max(0, index);
		index = FastMath.min(values.length-1, index);
		return values[index];
	}

	public static Postprocess get(AnyTag tag) {
		return get(tag.intValue(DEFAULT));
	}
}
