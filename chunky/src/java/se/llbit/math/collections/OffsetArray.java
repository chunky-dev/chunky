package se.llbit.math.collections;

/**
 * Represents an array, but allows negative indices using an offset set at construction
 */
public class OffsetArray<T> {
  private final Object[] data;
  private final int offset;

  public OffsetArray(int size, int startsAt) {
    this.data = new Object[size];
    this.offset = -startsAt;
  }

  public void set(int idx, T t) {
    this.data[idx + this.offset] = t;
  }

  public T get(int idx) {
    //noinspection unchecked
    return (T) this.data[idx + this.offset];
  }

  public int min() {
    return offset;
  }

  public int max() {
    return min() + length();
  }

  public int length() {
    return data.length;
  }
}