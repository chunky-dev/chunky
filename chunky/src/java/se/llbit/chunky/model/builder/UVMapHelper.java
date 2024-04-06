package se.llbit.chunky.model.builder;

import se.llbit.math.Vector4;

public class UVMapHelper {
  private final int textureWidth;
  private final int textureHeight;
  private final int width;
  private final int length;
  private final int height;
  private final int boxU;
  private final int boxV;

  private boolean flipX = false;
  private boolean flipY = false;

  public UVMapHelper(int textureWidth, int textureHeight, int width, int length, int height, int boxU, int boxV) {
    this.textureWidth = textureWidth;
    this.textureHeight = textureHeight;
    this.width = width;
    this.length = length;
    this.height = height;
    this.boxU = boxU;
    this.boxV = boxV;
  }

  @Deprecated
  public UVMapHelper(int width, int length, int height, int boxU, int boxV) {
    this(64, 64, width, length, height, boxU, boxV);
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

  public class Side {
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

    /**
     * Flip the texture horizontally.
     *
     * @return This side
     */
    public Side flipX() {
      double tmp = x0;
      x0 = x1;
      x1 = tmp;
      return this;
    }

    /**
     * Flip the texture vertically.
     *
     * @return This side
     */
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
      return new Vector4(x0 / textureWidth, x1 / textureWidth, 1 - y0 / textureHeight, 1 - y1 / textureHeight);
    }
  }
}
