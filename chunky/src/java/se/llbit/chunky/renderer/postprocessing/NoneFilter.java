package se.llbit.chunky.renderer.postprocessing;

public class NoneFilter extends SimplePixelPostProcessingFilter {
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
