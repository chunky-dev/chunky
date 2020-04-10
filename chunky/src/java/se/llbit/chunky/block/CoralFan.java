package se.llbit.chunky.block;

import se.llbit.chunky.entity.CoralFanEntity;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Quad;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

public class CoralFan extends MinecraftBlockTranslucent {

  private final String coralType;

  public CoralFan(String name, String coralType) {
    super(name, coralTexture(coralType));
    this.coralType = coralType;
    localIntersect = true;
    solid = false;
    invisible = true;
  }

  public static Texture coralTexture(String coralType) {
    switch (coralType) {
      default:
      case "tube":
        return Texture.tubeCoralFan;
      case "brain":
        return Texture.brainCoralFan;
      case "horn":
        return Texture.hornCoralFan;
      case "bubble":
        return Texture.bubbleCoralFan;
      case "fire":
        return Texture.fireCoralFan;
      case "dead_tube":
        return Texture.deadTubeCoralFan;
      case "dead_brain":
        return Texture.deadBrainCoralFan;
      case "dead_horn":
        return Texture.deadHornCoralFan;
      case "dead_bubble":
        return Texture.deadBubbleCoralFan;
      case "dead_fire":
        return Texture.deadFireCoralFan;
    }
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override public boolean isEntity() {
    return true;
  }

  @Override public Entity toEntity(Vector3 position) {
    return new CoralFanEntity(position, coralType);
  }
}
