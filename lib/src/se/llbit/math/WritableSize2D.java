package se.llbit.math;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;

import java.util.ArrayList;
import java.util.Collection;

public class WritableSize2D implements ObservableSize2D {
  protected int width, height;
  IntegerBinding widthBinding, heightBinding;

  public WritableSize2D() {
    this(1, 1);
  }
  public WritableSize2D(int width, int height) {
    set(width, height);
    widthBinding = Bindings.createIntegerBinding(this::getWidth, this);
    heightBinding = Bindings.createIntegerBinding(this::getHeight, this);
  }

  public void set(Size2D other) {
    set(other.getWidth(), other.getHeight());
  }

  public void set(int width, int height) {
    this.width = Math.max(width, 1);
    this.height = Math.max(height, 1);
    invalidate();
    fireUpdateListeners();
  }

  public void setWidth(int width) {
    set(width, height);
  }

  public void setHeight(int height) {
    set(width, height);
  }

  public void scale(double scale) {
    set(
      (int) Math.ceil(width * scale),
      (int) Math.ceil(height * scale)
    );
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public IntegerBinding getWidthBinding() {
    return widthBinding;
  }

  @Override
  public IntegerBinding getHeightBinding() {
    return heightBinding;
  }

  private final Collection<InvalidationListener> invalidationListeners = new ArrayList<>(0);

  public void invalidate() {
    invalidationListeners.forEach(listener -> listener.invalidated(this));
  }

  @Override
  public void addListener(InvalidationListener listener) {
    invalidationListeners.add(listener);
  }

  @Override
  public void removeListener(InvalidationListener listener) {
    invalidationListeners.remove(listener);
  }

  private final Collection<UpdateListener> updateListeners = new ArrayList<>(1);

  public void fireUpdateListeners() {
    updateListeners.forEach(listener -> listener.update(width, height));
  }

  @Override
  public void addListener(UpdateListener listener) {
    updateListeners.add(listener);
  }

  @Override
  public void removeListener(UpdateListener listener) {
    updateListeners.remove(listener);
  }
}
