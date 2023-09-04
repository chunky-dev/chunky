package se.llbit.chunky.block;

import se.llbit.chunky.model.PitcherCropBottomModel;
import se.llbit.chunky.model.PitcherCropTopModel;
import se.llbit.chunky.resources.Texture;

public class PitcherCrop extends AbstractModelBlock {
  private final String description;

  public PitcherCrop(int age, String half) {
    super("pitcher_crop", Texture.pitcherCropTop);
    localIntersect = true;
    opaque = false;
    this.model = half.equals("upper")
      ? new PitcherCropTopModel(age)
      : new PitcherCropBottomModel(age);
    this.description = String.format("age=%d, half=%s", age, half);
  }

  @Override
  public String description() {
    return description;
  }
}