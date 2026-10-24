package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.block.AbstractModelBlock;
import se.llbit.chunky.entity.Entity;
import se.llbit.chunky.entity.StrawBedFrills;
import se.llbit.chunky.model.minecraft.StrawBedFootModel;
import se.llbit.chunky.model.minecraft.StrawBedHeadModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.math.Vector3;

import java.util.Collection;
import java.util.Collections;

public class StrawBed extends AbstractModelBlock {
  private final String description;
  private final String part;
  private final String facing;

  public StrawBed(String part, String facing) {
    super("straw_bed", Texture.strawBed);
    this.description = String.format("part=%s, facing=%s", part, facing);
    this.part = part;
    this.facing = facing;
    int direction = switch (facing) {
      case "east" -> 3;
      case "west" -> 1;
      case "south" -> 0;
      default -> 2;
    };
    model = part.equals("head") ? new StrawBedHeadModel(direction) : new StrawBedFootModel(direction);
  }

  @Override
  public boolean hasEntities() {
    return true;
  }

  @Override
  public boolean isReplacedByEntities() {
    return false;
  }

  @Override
  public Collection<Entity> createEntities(Vector3 position) {
    return Collections.singleton(new StrawBedFrills(position, part, facing));
  }

  @Override
  public String description() {
    return description;
  }
}
