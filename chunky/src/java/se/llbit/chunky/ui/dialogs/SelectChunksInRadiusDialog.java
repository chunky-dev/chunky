package se.llbit.chunky.ui.dialogs;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.IntegerTextField;

public class SelectChunksInRadiusDialog extends Dialog<ButtonType> {
  protected float chunkSelectionRadius = 10;

  protected IntegerTextField selectionX;
  protected IntegerTextField selectionZ;

  public SelectChunksInRadiusDialog() {
    this.setTitle("Adjust chunk selection radius");

    this.selectionX = new IntegerTextField();
    this.selectionZ = new IntegerTextField();

    DialogPane dialogPane = this.getDialogPane();
    GridPane gridPane = new GridPane();
    gridPane.setHgap(5);
    gridPane.setVgap(5);

    DoubleAdjuster chunkRadiusAdjuster = new DoubleAdjuster();
    chunkRadiusAdjuster.setName("Radius (Chunks)");
    chunkRadiusAdjuster.setRange(0, 100);
    chunkRadiusAdjuster.clampMin();
    chunkRadiusAdjuster.setAlignment(Pos.CENTER);
    chunkRadiusAdjuster.set(this.chunkSelectionRadius); // set default
    chunkRadiusAdjuster.onValueChange(value -> this.chunkSelectionRadius = value.floatValue());

    gridPane.add(new Label("Chunk X:"), 0, 0);
    gridPane.add(this.selectionX, 1, 0);
    gridPane.add(new Label("Chunk Z:"), 2, 0);
    gridPane.add(this.selectionZ, 3, 0);
    gridPane.add(chunkRadiusAdjuster, 0, 1, 4, 1);

    dialogPane.setContent(gridPane);
    dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
  }

  public float getRadius() {
    return this.chunkSelectionRadius;
  }

  public void setSelectionPos(int selectionX, int selectionZ) {
    this.selectionX.setText(String.valueOf(selectionX));
    this.selectionZ.setText(String.valueOf(selectionZ));
  }

  public int getSelectionX() {
    return this.selectionX.getValue();
  }

  public int getSelectionZ() {
    return this.selectionZ.getValue();
  }
}
