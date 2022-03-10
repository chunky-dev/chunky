package se.llbit.math;

import javafx.beans.Observable;
import javafx.beans.binding.IntegerBinding;

public interface ObservableSize2D extends Size2D, Observable {
  IntegerBinding getWidthBinding();
  IntegerBinding getHeightBinding();

  void addListener(UpdateListener listener);
  void removeListener(UpdateListener listener);

  @FunctionalInterface
  interface UpdateListener {
    void update(int width, int height);
  }
}
