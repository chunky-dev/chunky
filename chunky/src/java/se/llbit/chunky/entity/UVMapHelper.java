package se.llbit.chunky.entity;

import se.llbit.math.Vector4;

public class UVMapHelper {
  private final int width;
  private final int length;
  private final int height;
  private final int boxU;
  private final int boxV;

  private boolean flipX = false;
  private boolean flipY = false;

  public UVMapHelper(int width, int length, int height, int boxU, int boxV) {
    this.width = width;
    this.length = length;
    this.height = height;
    this.boxU = boxU;
    this.boxV = boxV;
  }

  public UVMapHelper flipX() {
    flipX = !flipX;
    return this;
  }

  public UVMapHelper flipY() {
    flipY = !flipY;
    return this;
  }

  public Side front() {
    return createSide(boxU + length, boxU + length + width, boxV + length, boxV + length + height);
  }

  public Side back() {
    return createSide(boxU + 2 * length + width, boxU + 2 * length + 2 * width, boxV + length, boxV + length + height);
  }

  public Side right() {
    return createSide(boxU, boxU + length, boxV + length, boxV + length + height);
  }

  public Side left() {
    return createSide(boxU + length + width, boxU + 2 * length + width, boxV + length, boxV + length + height);
  }

  public Side top() {
    return createSide(boxU + length, boxU + length + width, boxV, boxV + length);
  }

  public Side bottom() {
    return createSide(boxU + length + width, boxU + length + 2 * width, boxV, boxV + length);
  }

  protected Side createSide(int x0, int x1, int y0, int y1) {
    return new Side(flipX ? x1 : x0, flipX ? x0 : x1, flipY ? y1 : y0, flipY ? y0 : y1);
  }

  public static class Side {
    private double x0;
    private double x1;
    private double y0;
    private double y1;

    protected Side(double x0, double x1, double y0, double y1) {
      this.x0 = x0;
      this.x1 = x1;
      this.y0 = y0;
      this.y1 = y1;
    }

    public Side flipX() {
      double tmp = x0;
      x0 = x1;
      x1 = tmp;
      return this;
    }

    public Side flipY() {
      double tmp = y0;
      y0 = y1;
      y1 = tmp;
      return this;
    }

    /**
     * Creates a UV vector that can be used by the {@link se.llbit.math.Quad} constructor.
     *
     * @return UV vector t hat can be used to construct a Quad
     */
    public Vector4 toVectorForQuad() {
      return new Vector4(x0 / 64., x1 / 64., 1 - y0 / 64., 1 - y1 / 64.);
    }
  }
}
