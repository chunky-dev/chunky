package se.llbit.chunky.block;

import se.llbit.chunky.model.DecoratedPotModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.entity.Entity;
import se.llbit.math.Vector3;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DecoratedPot extends AbstractModelBlock {

  private final String facing;
  private final String description;

  public DecoratedPot(String facing, boolean waterlogged, String[] sherds) {
    super("decorated_pot", Texture.decoratedPotSide);
    this.waterlogged = waterlogged;
    this.facing = facing;
    description = "waterlogged=" + waterlogged
      + ", facing=" + facing
      + ", sherds=" + Arrays.stream(sherds)
      .map(sherd -> sherd == null ? "minecraft:brick" : sherd)
      .collect(Collectors.joining(", ", "[", "]"));
    model = new DecoratedPotModel(facing, sherds);
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public boolean isBlockEntity() {
    return true;
  }

  @Override
  public boolean isModifiedByBlockEntity() {
    return true;
  }

  @Override
  public Entity toBlockEntity(Vector3 position, CompoundTag entityTag) {
    return new DecoratedPotModel.DecoratedPotSpoutEntity(position, facing);
  }

  @Override
  public Tag getNewTagWithBlockEntity(Tag blockTag, CompoundTag entityTag) {
    CompoundTag newBlockTag = new CompoundTag();
    // create a copy of the block tag
    StreamSupport.stream(blockTag.asCompound().spliterator(), false)
      .filter(namedTag -> !namedTag.name.equals("Properties"))
      .forEach(newBlockTag::add);
    CompoundTag properties = new CompoundTag();
    newBlockTag.add("Properties", properties); // because set is not implemented
    blockTag.get("Properties").asCompound().iterator().forEachRemaining(properties::add);

    properties.add("blockEntity#sherds", entityTag.get("sherds").asList());
    properties.add("chunky#debugCoordinates", new StringTag(
      entityTag.get("x").intValue() + " " + entityTag.get("z").intValue()
    ));
    return newBlockTag;
  }
}
