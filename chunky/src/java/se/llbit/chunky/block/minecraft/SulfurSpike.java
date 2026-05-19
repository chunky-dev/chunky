package se.llbit.chunky.block.minecraft;

import se.llbit.chunky.resources.Texture;

public class SulfurSpike extends SpriteBlock {
  private final String description;

  public SulfurSpike(String thickness, String verticalDirection) {
    super("sulfur_spike", getTexture(thickness, verticalDirection));
    description = "thickness=" + thickness + ", vertical_direction=" + verticalDirection;
  }

  private static Texture getTexture(String thickness, String verticalDirection) {
    if (verticalDirection.equals("down")) {
      return switch (thickness) {
        case "tip_merge" -> Texture.sulfurSpikeDownTipMerge;
        case "frustum" -> Texture.sulfurSpikeDownFrustum;
        case "middle" -> Texture.sulfurSpikeDownMiddle;
        case "base" -> Texture.sulfurSpikeDownBase;
        default -> Texture.sulfurSpikeDownTip; // tip
      };
    } else {
      return switch (thickness) {
        case "tip_merge" -> Texture.sulfurSpikeUpTipMerge;
        case "frustum" -> Texture.sulfurSpikeUpFrustum;
        case "middle" -> Texture.sulfurSpikeUpMiddle;
        case "base" -> Texture.sulfurSpikeUpBase;
        default -> Texture.sulfurSpikeUpTip; // tip
      };
    }
  }

  @Override
  public String description() {
    return description;
  }
}
