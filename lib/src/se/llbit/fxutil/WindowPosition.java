package se.llbit.fxutil;

import javafx.stage.Screen;
import javafx.stage.Stage;

public class WindowPosition {

  private final double x;
  private final double y;
  private final double width;
  private final double height;
  private final boolean maximized;

  public WindowPosition(double x, double y, double width, double height, boolean maximized) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.maximized = maximized;
  }

  public WindowPosition(Stage stage) {
    this(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight(), stage.isMaximized());
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

  public boolean isMaximized() {
    return maximized;
  }

  /**
   * Move the given stage to this window position if the position is at least on one screen.
   *
   * @param stage Stage to reposition
   * @param minWidth Minimum window width
   * @param minHeight Minimum window height
   */
  public void apply(Stage stage, double minWidth, double minHeight) {
    double width = Math.max(minWidth, this.width);
    double height = Math.max(minHeight, this.height);
    if (!Screen.getScreensForRectangle(x, y, width, height).isEmpty()) {
      stage.setX(x);
      stage.setY(y);
      stage.setWidth(width);
      stage.setHeight(height);
    }
    stage.setMaximized(maximized);
  }
}
