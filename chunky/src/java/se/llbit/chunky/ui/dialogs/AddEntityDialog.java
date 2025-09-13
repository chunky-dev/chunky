package se.llbit.chunky.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import se.llbit.chunky.ui.DoubleTextField;
import se.llbit.chunky.ui.elements.TextFieldLabelWrapper;
import se.llbit.chunky.ui.render.tabs.EntitiesTab;
import se.llbit.math.Vector3;

public class AddEntityDialog extends Dialog<ButtonType> {
  protected ChoiceBox<String> entityType = new ChoiceBox<>();
  protected ChoiceBox<EntitiesTab.EntityPlacement> entityPlacement = new ChoiceBox<>();
  protected GridPane positionPane = new GridPane();
  protected DoubleTextField posX = new DoubleTextField();
  protected DoubleTextField posY = new DoubleTextField();
  protected DoubleTextField posZ = new DoubleTextField();

  public AddEntityDialog() {
    this.setTitle("Add an entity to the scene");

    Label entityTypeLabel = new Label("Entity type:");
    entityType.getItems().addAll(EntitiesTab.entityTypes.keySet());
    HBox entityTypeBox = new HBox(10, entityTypeLabel, entityType);

    Label entityPlacementLabel = new Label("Entity placement:");
    entityPlacement.getItems().addAll(EntitiesTab.EntityPlacement.values());
    entityPlacement.getSelectionModel().selectedItemProperty().addListener(
      (observable, oldValue, newValue) -> {
        boolean disablePositionControls = newValue != EntitiesTab.EntityPlacement.POSITION;
        positionPane.setDisable(disablePositionControls);
      }
    );
    HBox entityPlacementBox = new HBox(10, entityPlacementLabel, entityPlacement);

    TextFieldLabelWrapper posXWrapper = new TextFieldLabelWrapper();
    posXWrapper.setLabelText("x:");
    posXWrapper.setTextField(posX);
    posX.setText("0.0");

    TextFieldLabelWrapper posYWrapper = new TextFieldLabelWrapper();
    posYWrapper.setLabelText("y:");
    posYWrapper.setTextField(posY);
    posY.setText("0.0");

    TextFieldLabelWrapper posZWrapper = new TextFieldLabelWrapper();
    posZWrapper.setLabelText("z:");
    posZWrapper.setTextField(posZ);
    posZ.setText("0.0");

    ColumnConstraints labelConstraints = new ColumnConstraints();
    labelConstraints.setHgrow(Priority.NEVER);
    labelConstraints.setPrefWidth(120);
    ColumnConstraints posFieldConstraints = new ColumnConstraints();
    posFieldConstraints.setMinWidth(20);
    posFieldConstraints.setPrefWidth(90);

    positionPane.setHgap(6);
    positionPane.getColumnConstraints().addAll(labelConstraints, posFieldConstraints, posFieldConstraints, posFieldConstraints);
    positionPane.addRow(0, new Label("Specific position:"), posXWrapper, posYWrapper, posZWrapper);
    positionPane.setDisable(true);

    DialogPane dialogPane = this.getDialogPane();
    VBox vBox = new VBox(10, entityTypeBox, entityPlacementBox, positionPane);
    vBox.setPadding(new Insets(10));

    dialogPane.setContent(vBox);
    dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
  }

  public EntitiesTab.EntityType<?> getType() {
    return EntitiesTab.entityTypes.get(entityType.getSelectionModel().getSelectedItem());
  }

  public EntitiesTab.EntityPlacement getPlacement() {
    return entityPlacement.getValue();
  }

  public Vector3 getPosition() {
    return new Vector3(
      posX.valueProperty().doubleValue(),
      posY.valueProperty().doubleValue(),
      posZ.valueProperty().doubleValue()
    );
  }
}
