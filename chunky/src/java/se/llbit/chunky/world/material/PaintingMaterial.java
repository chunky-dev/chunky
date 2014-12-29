package se.llbit.chunky.world.material;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;

public class PaintingMaterial extends Material {

	public static final PaintingMaterial INSTANCE = new PaintingMaterial();

	private PaintingMaterial() {
		super("painting", Texture.paintings);
	}

}
