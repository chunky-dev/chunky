package se.llbit.chunky.entity;

import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;

public class CopperGolemEntity {
  public enum Oxidation {
    NONE,
    EXPOSED,
    WEATHERED,
    OXIDIZED
  }

  public static Material material = new TextureMaterial(Texture.copperGolem);
  public static Material exposedMaterial = new TextureMaterial(Texture.copperGolemExposed);
  public static Material oxidizedMaterial = new TextureMaterial(Texture.copperGolemOxidized);
  public static Material weatheredMaterial = new TextureMaterial(Texture.copperGolemWeathered);

  static Material getMaterial(Oxidation oxidation) {
    return switch (oxidation) {
      case NONE -> material;
      case EXPOSED -> exposedMaterial;
      case WEATHERED -> weatheredMaterial;
      case OXIDIZED -> oxidizedMaterial;
    };
  }
}
