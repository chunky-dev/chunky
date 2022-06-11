package se.llbit.chunky.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Labeled;
import javafx.scene.shape.SVGPath;
import javafx.scene.image.Image;

/**
 * Icons made for Chunky and released under CC0
 *
 * Most icons are provided as SVG paths to use with {@link javafx.scene.shape.SVGPath}.
 */
public class Icons {
  /**
   * Chunky icon ("chunky-icon.png")
   * typically used as window/dialog icon {@link javafx.stage.Stage#getIcons()}
   */
  public final static Image CHUNKY_ICON = new Image(Icons.class.getResourceAsStream("/chunky-icon.png"));

  public final static String PORTRAIT_TO_LANDSCAPE =
    "M0-8v9h-3v3h-5v-12Zm-7 1v10h3v-3h3v-7Z" + // portrait rectangle with missing edge
    "M8 0v8h-12v-8Zm-11 1v6h10v-6Z" + // landscape rectangle
    "M1-7h2c2 0 3 1 3 3v.5h2l-3 3-3-3h2v-.5c0-1-1-1-1-1h-2Z"; // arrow

  public final static String SQUARE_DIAGONALLY_CROSSED =
    "M-6-6h11l1-1 1 1-1 1v11h-11l-1 1-1-1 1-1ZM4-5h-9v9Zm1 1-9 9h9Z";

  /**
   * base for open and closed chains icon
   * use in concatenation with {@link #CHAIN_CONNECTION_BROKEN} or {@link #CHAIN_CONNECTION_CLOSED}
   */
  public final static String CHAIN_LINK_BASE =
    "M-2-16c-1 0-2 1-2 2l0 8c0 1 1 2 2 2l4 0c1 0 2-1 2-2l0-8c0-1-1-2-2-2z" + // upper chain link outside
      "m3 2c1 0 1 1 1 1l0 6c0 1-1 1-1 1l-2 0c-1 0-1-1-1-1l0-6c0-1 1-1 1-1z" + // upper chain link inside
      "M-2 4c-1 0-2 1-2 2l0 8c0 1 1 2 2 2l4 0c1 0 2-1 2-2l0-8c0-1-1-2-2-2z" + // lower chain link outside
      "m3 2c1 0 1 1 1 1l0 6c0 1-1 1-1 1l-2 0c-1 0-1-1-1-1l0-6c0-1 1-1 1-1z"; // lower chain link inside

  /**
   * append this to the {@link #CHAIN_LINK_BASE} to create the open chains icon
   */
  public final static String CHAIN_CONNECTION_BROKEN =
    "M-1-3l2 1 0-5c0-1-1-1-1-1-1 0-1 1-1 1z" + // broken upper chain link
    "M-1 2l0 5c0 1 1 1 1 1 1 0 1-1 1-1l0-4z"; // broken lower chain link

  /**
   * append this to the {@link #CHAIN_LINK_BASE} to create the closed chains icon
   */
  public final static String CHAIN_CONNECTION_CLOSED =
    "M-1 5c0 1 1 1 1 1 1 0 1-1 1-1l0-10c0-1-1-1-1-1-1 0-1 1-1 1z"; // connecting chain link

  public final static String HEAVY_PLUS =
    "M-2-8l4 0L2 8l-4 0zM8-2l0 4L-8 2l0-4z";

  public final static String HEAVY_ARROW_RIGHT =
    "M-1-8 8 0-1 8zM-1-4-1 4-8 4-8-4z";

  public static IconBuilder buildIcon(String svgPathString) {
    return new IconBuilder(svgPathString);
  }

  public static class IconBuilder {
    private final SVGPath svgPath = new SVGPath();

    public IconBuilder(String svgPathString) {
      svgPath.setContent(svgPathString);
    }

    /**
     * scale the icon to the requested size
     * @param size in px
     */
    public IconBuilder withSize(double size) {
      double scale = size / svgPath.prefWidth(-1);
      svgPath.setScaleX(scale);
      svgPath.setScaleY(scale);
      return this;
    }

    /**
     * flip the icon on the x-axis
     */
    public IconBuilder flipX() {
      svgPath.setScaleX(svgPath.getScaleX() * -1);
      return this;
    }

    /**
     * flip the icon on the y-axis
     */
    public IconBuilder flipY() {
      svgPath.setScaleY(svgPath.getScaleY() * -1);
      return this;
    }

    /**
     * rotate the icon 90 degrees clockwise
     */
    public IconBuilder rotateCW() {
      svgPath.setRotate(svgPath.getRotate() + 90.0);
      return this;
    }

    /**
     * rotate the icon 90 degrees counterclockwise
     */
    public IconBuilder rotateCCW() {
      svgPath.setRotate(svgPath.getRotate() - 90.0);
      return this;
    }

    public IconBuilder setAsGraphic(Labeled labeled) {
      return setAsGraphic(labeled, false);
    }
    public IconBuilder setAsGraphic(Labeled labeled, boolean noPadding) {
      labeled.setGraphic(svgPath);
      if(noPadding)
        labeled.setPadding(Insets.EMPTY);
      return this;
    }

    public SVGPath build() {
      return svgPath;
    }
  }
}
