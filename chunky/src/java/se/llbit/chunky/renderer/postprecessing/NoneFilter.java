package se.llbit.chunky.renderer.postprecessing;

public class NoneFilter extends IndependentPostProcessingFilter {
  @Override
  protected void processPixel(double[] pixel) {
  }

  @Override
  public String getName() {
    return "None";
  }
}
