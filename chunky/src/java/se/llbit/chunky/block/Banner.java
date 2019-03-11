package se.llbit.chunky.block;

import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.SignEntity;
import se.llbit.chunky.entity.StandingBanner;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.Json;
import se.llbit.json.JsonObject;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;

public class Banner extends MinecraftBlockTranslucent {
  private final int rotation, color;

  public Banner(String name, Texture texture, int rotation, int color) {
    super(name, texture);
    invisible = true;
    opaque = false;
    localIntersect = true;
    this.rotation = rotation % 16;
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
    return new StandingBanner(position, rotation, design);
  }
}
