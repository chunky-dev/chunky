package se.llbit.chunky.world.material;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.VolumeMaterial;

public class ParticleFogMaterial extends VolumeMaterial {
  public ParticleFogMaterial() {
    super("particle_fog", Texture.air);
  }
}
