package se.llbit.chunky.renderer;

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

	public static final Postprocess DEFAULT = GAMMA;

	public static final Postprocess[] values = values();

	public static Postprocess get(int index) {
		index = Math.max(0, index);
		index = Math.min(values.length-1, index);
		return values[index];
	}

	public static Postprocess get(AnyTag tag) {
		return get(tag.intValue(DEFAULT.ordinal()));
	}

	public static Postprocess get(String name) {
		for (Postprocess mode: values) {
			if (mode.name().equals(name)) {
				return mode;
			}
		}
		return DEFAULT;
	}
}
