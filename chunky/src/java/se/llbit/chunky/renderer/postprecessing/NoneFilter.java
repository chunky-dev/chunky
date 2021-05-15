package se.llbit.chunky.renderer.postprecessing;

public class NoneFilter extends IndependentPostProcessingFilter {
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
