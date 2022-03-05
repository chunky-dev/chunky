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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import se.llbit.math.QuickMath;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * An input UI element for integer width and integer height.
 * Supports ratio locking and queues the changes for submission via size changes listener.
 */
public class SizeInput extends GridPane {
  protected final IntegerProperty
    currentWidthProperty = new SimpleIntegerProperty(this, "currentWidth"),
    currentHeightProperty = new SimpleIntegerProperty(this, "currentHeight");

  protected final Label
    widthLabel = new Label("Width:"),
    heightLabel = new Label("Height:");
  protected final IntegerTextField
    widthInput = new IntegerTextField(),
    heightInput = new IntegerTextField();
  protected final CheckBox ratioLockCheckBox = new CheckBox();
  protected final Label unitLabel = new Label("px");

  protected BiConsumer<Integer, Integer> sizeChangedCallback = null;
  protected final BooleanProperty changeQueuedProperty = new SimpleBooleanProperty(this, "changeQueued", false);

  protected final Node aspectRatioDetailsNode;
  protected final BooleanProperty showAspectRatioDetailsProperty = new SimpleBooleanProperty(this, "showAspectRatioDetails", false);

  public SizeInput(
    @NamedArg("initialWidth") int initialWidth,
    @NamedArg("initialHeight")  int initialHeight
  ) {
    this(initialWidth, initialHeight, false);
  }
  public SizeInput(
    @NamedArg("initialWidth") int initialWidth,
    @NamedArg("initialHeight")  int initialHeight,
    @NamedArg("showAspectRatioDetails") boolean showAspectRatioDetails
  ) {
    super();
    currentWidthProperty.set(initialWidth);
    currentHeightProperty.set(initialHeight);
    queuedWidthValueProperty().set(initialWidth);
    queuedHeightValueProperty().set(initialHeight);
    widthInput.getConverter().setParseNonNegativeOnly(true);
    heightInput.getConverter().setParseNonNegativeOnly(true);

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

    VBox ratioLockArea = new VBox(-7, new Label("⌝ "), ratioLockCheckBox, new Label("⌟ "));
    ratioLockArea.setAlignment(Pos.CENTER);
    return ratioLockArea;
  }

  AtomicBoolean listenerLockedByUpdate = new AtomicBoolean(false);

  private int scaleAxis(int currentValue, double factor) {
    return Math.max(1, (int) Math.ceil(currentValue * factor));
  }
  private void handleAxisUpdate(
    IntegerTextField textField,
    IntegerProperty otherAxisValueProperty,
    Number oldValue, Number newValue
  ) {
    // lock stops recursive updates when the other locked axis gets updated
    if (listenerLockedByUpdate.get())
      return;
    if (!textField.isValid())
      return;

    if (isRatioLocked()) {
      int otherAxisScaledValue;
      if (oldValue.intValue() > 0) {
        double ratio = newValue.doubleValue() / oldValue.doubleValue();
        otherAxisScaledValue = scaleAxis(otherAxisValueProperty.get(), ratio);
      } else {
        otherAxisScaledValue = newValue.intValue();
      }
      listenerLockedByUpdate.set(true);
      otherAxisValueProperty.set(otherAxisScaledValue);
      listenerLockedByUpdate.set(false);
    }

    changeQueuedProperty.set(true);
  }

  private void initListeners() {
    queuedWidthValueProperty().addListener((observable, oldValue, newValue) ->
      handleAxisUpdate(widthInput, queuedHeightValueProperty(), oldValue, newValue));
    queuedHeightValueProperty().addListener((observable, oldValue, newValue) ->
      handleAxisUpdate(heightInput, queuedWidthValueProperty(), oldValue, newValue));

    widthValueProperty().addListener(observable -> {
      listenerLockedByUpdate.set(true);
      queuedWidthValueProperty().set(getWidthValue());
      listenerLockedByUpdate.set(false);
    });
    heightValueProperty().addListener(observable -> {
      listenerLockedByUpdate.set(true);
      queuedHeightValueProperty().set(getHeightValue());
      listenerLockedByUpdate.set(false);
    });

    // submit listener (typically enter)
    widthInput.setOnAction(event -> applyChanges());
    heightInput.setOnAction(event -> applyChanges());

    showAspectRatioDetailsProperty().addListener((observable, oldValue, newValue) -> {
      if(newValue != oldValue) {
        if(newValue) {
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

    IntegerProperty gdc = new SimpleIntegerProperty(QuickMath.gcd(getWidthValue(), getHeightValue()));

    details.textProperty().bind(
      Bindings.concat(
        widthValueProperty(), unitStringProperty(),
        " × ",
        heightValueProperty(), unitStringProperty(),
        " (",
        Bindings.createIntegerBinding(
          () -> (int) Math.ceil((double) getWidthValue() / gdc.get()),
          gdc, widthValueProperty()
        ),
        " : ",
        Bindings.createIntegerBinding(
          () -> (int) Math.ceil((double) getHeightValue() / gdc.get()),
          gdc, heightValueProperty()
        ),
        ")"
      )
    );
    changeQueuedProperty().addListener((observable, oldValue, newValue) -> {
      // size changes were committed
      if(!newValue) {
        gdc.set(QuickMath.gcd(getWidthValue(), getHeightValue()));
      }
    });
    return details;
  }

  public ReadOnlyIntegerProperty widthValueProperty() {
    return currentWidthProperty;
  }

  public ReadOnlyIntegerProperty heightValueProperty() {
    return currentHeightProperty;
  }

  public IntegerProperty queuedWidthValueProperty() {
    return widthInput.valueProperty();
  }

  public IntegerProperty queuedHeightValueProperty() {
    return heightInput.valueProperty();
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
   * @return currently set width
   * - may not be the same as the value in the input field (see {@link #getQueuedWidthValue()})
   */
  public int getWidthValue() {
    return widthValueProperty().get();
  }

  /**
   * @return currently set height
   * - may not be the same as the value in the input field (see {@link #getQueuedHeightValue()})
   */
  public int getHeightValue() {
    return heightValueProperty().get();
  }

  /**
   * @return current width from the input field
   * - may be applied to {@link #getWidthValue()} by calling {@link #applyChanges()}
   */
  public int getQueuedWidthValue() {
    return queuedWidthValueProperty().get();
  }

  /**
   * @return current height from the input field
   * - may be applied to {@link #getHeightValue()} by calling {@link #applyChanges()}
   */
  public int getQueuedHeightValue() {
    return queuedHeightValueProperty().get();
  }

  /**
   * Sets and applies the size.
   * @param width > 0
   * @param height > 0
   */
  public void setSize(int width, int height) {
    if(width < 1 || height < 1)
      throw new IllegalArgumentException("Size must be positive (w,h > 0)");
    currentWidthProperty.set(width);
    currentHeightProperty.set(height);
    applyChanges();
  }

  /**
   * Scales both axis by scale and applies the new values as the size.
   * @param scale scale factor
   */
  public void scaleSize(double scale) {
    setSize(
      scaleAxis(getWidthValue(), scale),
      scaleAxis(getHeightValue(), scale)
    );
  }

  /**
   * Submits queued changes to the size change listener.
   */
  public void applyChanges() {
    if(widthInput.isValid()) currentWidthProperty.set(getQueuedWidthValue());
    if(heightInput.isValid()) currentHeightProperty.set(getQueuedHeightValue());
    sizeChangedCallback.accept(getWidthValue(), getHeightValue());
    changeQueuedProperty.set(false);
  }

  /**
   * Updates the current size change listener.
   * @param onChangeCallback if null remove current listener, otherwise add/replace current listener
   */
  public void setSizeChangeListener(BiConsumer<Integer, Integer> onChangeCallback) {
    sizeChangedCallback = onChangeCallback;
  }
}
