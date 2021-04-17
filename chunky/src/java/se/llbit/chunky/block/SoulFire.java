package se.llbit.chunky.block;

import se.llbit.chunky.model.FireModel;
import se.llbit.chunky.resources.Texture;

public class SoulFire extends AbstractModelBlock {

  public SoulFire() {
    super("soul_fire", Texture.soulFire);
    solid = false;
    model = new FireModel(Texture.soulFireLayer0, Texture.soulFireLayer1);
  }
}
