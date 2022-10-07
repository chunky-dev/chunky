package se.llbit.math;

public class AABBi {
  public int xMin;
  public int xMax;
  public int yMin;
  public int yMax;
  public int zMin;
  public int zMax;

  public AABBi(int xMin, int xMax, int yMin, int yMax, int zMin, int zMax) {
    this.xMin = xMin;
    this.xMax = xMax;
    this.yMin = yMin;
    this.yMax = yMax;
    this.zMin = zMin;
    this.zMax = zMax;
  }

  public AABBi(AABBi other) {
    this.xMin = other.xMin;
    this.xMax = other.xMax;
    this.yMin = other.yMin;
    this.yMax = other.yMax;
    this.zMin = other.zMin;
    this.zMax = other.zMax;
  }

  public void scale(int value) {
    this.xMin *= value;
    this.xMax *= value;
    this.yMin *= value;
    this.yMax *= value;
    this.zMin *= value;
    this.zMax *= value;
  }

  public void add(int value) {
    this.xMin += value;
    this.xMax += value;
    this.yMin += value;
    this.yMax += value;
    this.zMin += value;
    this.zMax += value;
  }

  public void lShift(int value) {
    this.xMin <<= value;
    this.xMax <<= value;
    this.yMin <<= value;
    this.yMax <<= value;
    this.zMin <<= value;
    this.zMax <<= value;
  }

  public void lShiftXZ(int value) {
    this.xMin <<= value;
    this.xMax <<= value;
    this.zMin <<= value;
    this.zMax <<= value;
  }

  public void rShift(int value) {
    this.xMin >>= value;
    this.xMax >>= value;
    this.yMin >>= value;
    this.yMax >>= value;
    this.zMin >>= value;
    this.zMax >>= value;
  }

  public void rShiftXZ(int value) {
    this.xMin >>= value;
    this.xMax >>= value;
    this.zMin >>= value;
    this.zMax >>= value;
  }
}