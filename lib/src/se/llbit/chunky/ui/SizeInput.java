/* Copyright (c) 2022 Chunky contributors
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.ui;

import javafx.beans.NamedArg;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import se.llbit.math.ObservableSize2D;
import se.llbit.math.QuickMath;
import se.llbit.math.WritableSize2D;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An input UI element for integer width and integer height.
 * Supports ratio locking and queues the changes for submission via size changes listener.
 */
public class SizeInput extends GridPane {
  protected final WritableSize2D currentSize, queuedSize;

  protected final Label
    widthLabel = new Label("Width:"),
    heightLabel = new Label("Height:");
  protected final IntegerTextField widthInput, heightInput;
  protected final CheckBox ratioLockCheckBox = new CheckBox();
  protected final Label unitLabel = new Label("px");

  // TODO: represent this state in the UI
  protected final BooleanProperty changeQueuedProperty = new SimpleBooleanProperty(this, "changeQueued", false);

  protected final Node aspectRatioDetailsNode;
  protected final BooleanProperty showAspectRatioDetailsProperty = new SimpleBooleanProperty(this, "showAspectRatioDetails", false);

  public SizeInput(
    @NamedArg("initialWidth") int initialWidth,
    @NamedArg("initialHeight") int initialHeight
  ) {
    this(initialWidth, initialHeight, false);
  }

  public SizeInput(
    @NamedArg("initialWidth") int initialWidth,
    @NamedArg("initialHeight") int initialHeight,
    @NamedArg("showAspectRatioDetails") boolean showAspectRatioDetails
  ) {
    super();
    currentSize = new WritableSize2D(initialWidth, initialHeight);
    queuedSize = new WritableSize2D(initialWidth, initialHeight);
    widthInput = new IntegerTextField(initialWidth);
    widthInput.getConverter().setRange(ValidatingNumberStringConverter.AllowedRange.POSITIVE);
    heightInput = new IntegerTextField(initialHeight);
    heightInput.getConverter().setRange(ValidatingNumberStringConverter.AllowedRange.POSITIVE);

    // width and height of max 999999 should be sufficient, right?
    widthInput.setMaximumCharacterInputLength(6);
    heightInput.setMaximumCharacterInputLength(6);

    setHgap(4);
    setVgap(4);
    add(widthLabel, 0, 0);
    add(heightLabel, 0, 1);
    add(widthInput, 1, 0);
    add(heightInput, 1, 1);
    getColumnConstraints().add(new ColumnConstraints(USE_COMPUTED_SIZE, 60, USE_COMPUTED_SIZE));
    getColumnConstraints().add(new ColumnConstraints(USE_COMPUTED_SIZE, 100, USE_COMPUTED_SIZE));
    add(buildRatioLock(), 2, 0, 1, 2);
    add(unitLabel, 3, 0, 1, 2);

    initListeners();

    aspectRatioDetailsNode = buildAspectRatioDetails();
    setConstraints(aspectRatioDetailsNode, 1, 2, 3, 1);
    showAspectRatioDetailsProperty.set(showAspectRatioDetails);
  }

  private void updateQueuedWidth(int width) {
    if (isRatioLocked()) {
      double matchingHeight = (double) width * currentSize.getHeight() / currentSize.getWidth();
      queuedSize.set(width, (int) Math.ceil(matchingHeight));
    } else {
      queuedSize.setWidth(width);
    }
  }

  private void updateQueuedHeight(int height) {
    if (isRatioLocked()) {
      double matchingWidth = (double) height * currentSize.getWidth() / currentSize.getHeight();
      queuedSize.set((int) Math.ceil(matchingWidth), height);
    } else {
      queuedSize.setHeight(height);
    }
  }

  private Node buildRatioLock() {
    ratioLockCheckBox.getStyleClass().add("lock-toggle");

    Tooltip ratioLockTooltip = new Tooltip();
    ratioLockTooltip.textProperty().bind(Bindings.createStringBinding(
      () -> {
        if (isRatioLocked()) {
          return "Unlock aspect ratio";
        } else {
          return "Lock aspect ratio";
        }
      },
      ratioLockedProperty()
    ));
    ratioLockCheckBox.setTooltip(ratioLockTooltip);
    SVGPath icon = new SVGPath();
    ratioLockCheckBox.setGraphic(icon);

    VBox ratioLockArea = new VBox(-7, new Label("⌝ "), ratioLockCheckBox, new Label("⌟ "));
    ratioLockArea.setAlignment(Pos.CENTER);
    return ratioLockArea;
  }

  private void initListeners() {

    AtomicBoolean updateInProgress = new AtomicBoolean(false);
    widthInput.valueProperty().addListener(observable -> {
      if (updateInProgress.compareAndSet(false, true)) {
        updateQueuedWidth(widthInput.getValue());
        changeQueuedProperty.set(true);
        updateInProgress.set(false);
      }
    });
    heightInput.valueProperty().addListener(observable -> {
      if (updateInProgress.compareAndSet(false, true)) {
        updateQueuedHeight(heightInput.getValue());
        changeQueuedProperty.set(true);
        updateInProgress.set(false);
      }
    });
    queuedSize.addListener((queuedWidth, queuedHeight) -> {
      widthInput.valueProperty().set(queuedWidth);
      heightInput.valueProperty().set(queuedHeight);
    });

    // submit listener (typically enter)
    widthInput.setOnAction(event -> applyChanges());
    heightInput.setOnAction(event -> applyChanges());

    showAspectRatioDetailsProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != oldValue) {
        if (newValue) {
          getChildren().add(aspectRatioDetailsNode);
        } else {
          getChildren().remove(aspectRatioDetailsNode);
        }
      }
    });
  }

  private Node buildAspectRatioDetails() {
    Label details = new Label();
    details.setPadding(new Insets(1, 0, 2, 3));
    details.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
    details.visibleProperty().bind(showAspectRatioDetailsProperty);

    IntegerBinding gdc = Bindings.createIntegerBinding(() -> QuickMath.gcd(
      currentSize.getWidth(),
      currentSize.getHeight()
    ), currentSize);

    details.textProperty().bind(
      Bindings.concat(
        currentSize.getWidthBinding(), unitStringProperty(),
        " × ",
        currentSize.getHeightBinding(), unitStringProperty(),
        " (",
        Bindings.createIntegerBinding(
          () -> (int) Math.ceil((double) currentSize.getWidth() / gdc.get()),
          gdc
        ),
        " ∶ ",
        Bindings.createIntegerBinding(
          () -> (int) Math.ceil((double) currentSize.getHeight() / gdc.get()),
          gdc
        ),
        ")"
      )
    );
    return details;
  }

  public BooleanProperty ratioLockedProperty() {
    return ratioLockCheckBox.selectedProperty();
  }

  public StringProperty unitStringProperty() {
    return unitLabel.textProperty();
  }

  public ReadOnlyBooleanProperty changeQueuedProperty() {
    return changeQueuedProperty;
  }

  public BooleanProperty showAspectRatioDetailsProperty() {
    return showAspectRatioDetailsProperty;
  }

  public void showAspectRatioDetails(boolean show) {
    showAspectRatioDetailsProperty.set(show);
  }

  public boolean isRatioLocked() {
    return ratioLockedProperty().get();
  }

  /**
   * @return currently set size
   * - may not be the same as the value in the input field (see {@link #getQueuedSize()})
   */
  public ObservableSize2D getSize() {
    return currentSize;
  }

  /**
   * @return current size from the input fields
   * - may be applied to {@link #getSize()} by calling {@link #applyChanges()}
   */
  public ObservableSize2D getQueuedSize() {
    return queuedSize;
  }

  /**
   * Sets and applies the size.
   *
   * @param width  > 0
   * @param height > 0
   */
  public void setSize(int width, int height) {
    if (width < 1 || height < 1)
      throw new IllegalArgumentException("Size must be positive (w,h > 0)");
    queuedSize.set(width, height);
    applyChanges();
  }

  /**
   * Scales both axis by scale and applies the new values as the size.
   *
   * @param scale scale factor
   */
  public void scaleSize(double scale) {
    queuedSize.scale(scale);
    applyChanges();
  }

  /**
   * Submits queued changes to the size change listener.
   */
  public void applyChanges() {
    if (widthInput.isValid() && heightInput.isValid()) {
      currentSize.set(queuedSize);
      changeQueuedProperty.set(false);
    }
  }
}
