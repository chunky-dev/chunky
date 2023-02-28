package se.llbit.chunky.block;

import se.llbit.chunky.model.DecoratedPotModel;
import se.llbit.chunky.resources.Texture;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.StringTag;
import se.llbit.nbt.Tag;

import java.util.Arrays;
import java.util.stream.StreamSupport;

public class DecoratedPot extends AbstractModelBlock {

  private final String description;

  public DecoratedPot(String facing, boolean waterlogged, String[] shards) {
    super("decorated_pot", Texture.decoratedPotSide);
    this.waterlogged = waterlogged;
    description = "waterlogged=" + waterlogged + ", facing=" + facing + ", shards=" + Arrays.toString(shards);
    model = new DecoratedPotModel(facing, shards);
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public boolean isModifiedByBlockEntity() {
    return true;
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

    properties.add("blockEntity#shards", entityTag.get("shards").asList());
    properties.add("chunky#debugCoordinates", new StringTag(
      entityTag.get("x").intValue() + " " + entityTag.get("z").intValue()
    ));
    return newBlockTag;
  }
}
