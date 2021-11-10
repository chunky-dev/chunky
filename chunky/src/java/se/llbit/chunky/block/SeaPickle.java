package se.llbit.chunky.block;

import se.llbit.chunky.model.SeaPickleModel;
import se.llbit.chunky.resources.Texture;

public class SeaPickle extends AbstractModelBlock {

  private final String description;
  public final boolean live;
  public final int pickles;

  public SeaPickle(int pickles, boolean live) {
    super("sea_pickle", Texture.seaPickle);
    pickles = Math.max(1, Math.min(4, pickles));
    this.description = String.format("pickles=%d, waterlogged=%s", pickles, live);
    this.pickles = pickles;
    this.live = live;
    this.model = new SeaPickleModel(pickles, live);
  }

  @Override
  public String description() {
    return description;
  }
}
