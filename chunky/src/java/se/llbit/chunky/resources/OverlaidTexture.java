package se.llbit.chunky.resources;

import se.llbit.math.ColorUtil;
import java.util.function.BiPredicate;

public class OverlaidTexture extends Texture {
  private Texture base;
  private Texture overlay;
  private BiPredicate mask;
  public OverlaidTexture(Texture base, Texture overlay, BiPredicate<Double, Double> mask) {
    this.base = base;
    this.overlay = overlay;
    this.mask = mask;
  }
  @Override
  public int[] getData() {
    int[] overlaidData = new int[image.data.length];
    for(int i = 0; i < overlaidData.length; i++) {
      int x = i % base.image.width;
      int y = i / base.image.height;
      boolean shouldOverlay = mask.test((double)x/base.image.width, (double)y/base.image.height);
      overlaidData[i] = shouldOverlay ? overlay.image.data[i] : base.image.data[i];
    }
    return overlaidData;
  }
  @Override
  public float[] getColor(int x, int y) {
    if(base.usesAverageColor())
      return base.getAvgColorFlat();
    float[] result = new float[4];
    boolean shouldOverlay = mask.test((double)x/base.image.width, (double)y/base.image.height);
    if(shouldOverlay) {
      ColorUtil.getRGBAComponentsGammaCorrected(overlay.image.data[width * y + x], result);
    } else {
      ColorUtil.getRGBAComponentsGammaCorrected(base.image.data[width * y + x], result);
    }
    return result;
  }
}
