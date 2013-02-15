package se.llbit.chunky.resources.texturepack;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class AlternateTextures extends TextureRef {

	private TextureRef[] alternatives;

	/**
	 * @param alternatives
	 */
	public AlternateTextures(TextureRef... alternatives) {
		super("noname");
		
		this.alternatives = alternatives;
	}

	@Override
	boolean load(InputStream imageStream) throws IOException {
		for (TextureRef alternative: alternatives) {
			try {
				if (alternative.load(imageStream)) {
					return true;
				}
			} catch (IOException e) {
				// TODO
			}
		}
		return false;
	}

}
