package se.llbit.chunky.ui.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilter;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilters;
import se.llbit.chunky.renderer.scene.Scene;

public class PostprocessingFilterChooser extends Dialog<ButtonType> {

  private final ChoiceBox<PostProcessingFilter> choiceBox = new ChoiceBox<>();

  public PostprocessingFilterChooser() {
    this.setTitle("Select Postprocessing Filter");

    DialogPane dialogPane = this.getDialogPane();
    VBox vBox = new VBox();

    choiceBox.getItems().addAll(PostProcessingFilters.getFilters());
    choiceBox.setConverter(new StringConverter<PostProcessingFilter>() {
      @Override
      public String toString(PostProcessingFilter object) {
        return object == null ? null : object.getName();
      }

      @Override
      public PostProcessingFilter fromString(String string) {
        return PostProcessingFilters.getPostProcessingFilterFromName(string)
            .orElse(Scene.DEFAULT_POSTPROCESSING_FILTER);
      }
    });
    choiceBox.getSelectionModel().selectedItemProperty()
        .addListener(((observable, oldValue, newValue) -> choiceBox.setTooltip(
            new Tooltip(newValue.getDescription()))));

    vBox.getChildren().addAll(choiceBox);
    vBox.setPadding(new Insets(10));

    dialogPane.setContent(vBox);
    dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
  }

  public PostProcessingFilter getFilter() {
    return choiceBox.getSelectionModel().getSelectedItem();
  }
}
