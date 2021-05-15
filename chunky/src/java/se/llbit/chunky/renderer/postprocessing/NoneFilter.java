package se.llbit.chunky.renderer.postprocessing;

public class NoneFilter extends PixelPostProcessingFilter {
  @Override
  public void processPixel(double[] pixel) {
  }

  @Override
  public String getName() {
    return "None";
  }

  @Override
  public String getId() {
    return "NONE";
  }
}
