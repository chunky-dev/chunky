package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.StandingBanner;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.Json;
import se.llbit.json.JsonObject;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

public class WallBanner extends MinecraftBlockTranslucent {
  private final int facing, color;

  public WallBanner(String name, Texture texture, String facing, int color) {
    super(name, texture);
    invisible = true;
    opaque = false;
    localIntersect = true;
    switch (facing) {
      default:
      case "north":
        this.facing = 2;
        break;
      case "south":
        this.facing = 3;
        break;
      case "west":
        this.facing = 4;
        break;
      case "east":
        this.facing = 5;
        break;
    }
    this.color = color;
  }

  @Override public boolean intersect(Ray ray, Scene scene) {
    return false;
  }

  @Override public boolean isBlockEntity() {
    return true;
  }

  @Override public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    JsonObject design = StandingBanner.parseDesign(entityTag);
    design.set("base", Json.of(color)); // Base color is not included in the entity tag in Minecraft 1.13+.
    return new se.llbit.chunky.entity.WallBanner(position, facing, design);
  }
}
