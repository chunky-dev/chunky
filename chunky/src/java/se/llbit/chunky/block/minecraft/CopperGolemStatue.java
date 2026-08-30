package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.MinecraftBlockTranslucent;
import se.llbit.chunky.entity.CopperGolemEntity;
import se.llbit.chunky.entity.CopperGolemStatueEntity;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Collection;
import java.util.Collections;

public class CopperGolemStatue extends MinecraftBlockTranslucent {
  private final String facing;
  private final String pose;
  private final CopperGolemEntity.Oxidation oxidation;

  public CopperGolemStatue(String name, Texture texture, String facing, String pose) {
    super(name, texture);
    this.facing = facing;
    this.pose = pose;
    this.oxidation = switch (name) {
      case "exposed_copper_golem_statue", "waxed_exposed_copper_golem_statue" -> CopperGolemEntity.Oxidation.EXPOSED;
      case "weathered_copper_golem_statue", "waxed_weathered_copper_golem_statue" ->
        CopperGolemEntity.Oxidation.WEATHERED;
      case "oxidized_copper_golem_statue", "waxed_oxidized_copper_golem_statue" -> CopperGolemEntity.Oxidation.OXIDIZED;
      default -> CopperGolemEntity.Oxidation.NONE;
    };
    invisible = true;
    opaque = false;
    localIntersect = true;
  }

  @Override
  public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override
  public boolean hasEntities() {
    return true;
  }

  @Override
  public Collection<Entity> createEntities(Vector3 position) {
    position = new Vector3(position);
    position.add(0.5, 0, 0.5);
    return Collections.singleton(new CopperGolemStatueEntity(position, pose, facing, oxidation));
  }
}
