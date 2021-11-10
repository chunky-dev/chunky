package se.llbit.chunky.block;

import se.llbit.chunky.model.CropsModel;
import se.llbit.chunky.resources.Texture;

public class NetherWart extends AbstractModelBlock {

  private static final Texture[] texture = {
      Texture.netherWart0, Texture.netherWart1, Texture.netherWart1, Texture.netherWart2
  };

  private final int age;

  public NetherWart(int age) {
    super("nether_wart", Texture.netherWart2);
    this.age = age & 3;
    this.model = new CropsModel(texture[this.age]);
  }

  @Override
  public String description() {
    return "age=" + age;
  }
}
