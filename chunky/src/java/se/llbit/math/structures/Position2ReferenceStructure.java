package se.llbit.math.structures;

public interface Position2ReferenceStructure<T> {

  void set(int x, int y, int z, T data);

  T get(int x, int y, int z);

  void compact();

}
