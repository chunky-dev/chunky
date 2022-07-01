package se.llbit.chunky.ui.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.function.Predicate;

public class ValidatingTextInputDialog extends TextInputDialog {

  private Predicate<String> isValid = (value) -> true;


  public ValidatingTextInputDialog(String defaultValue) {
    super(defaultValue);
    Button okButton = this.getSubmitButtion();
    okButton.addEventFilter(ActionEvent.ACTION, this::onSubmit);
    getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
      okButton.setDisable(!this.isValid.test(newValue));
    });
  }

  private Button getSubmitButtion() {
    return (Button) getDialogPane().lookupButton(ButtonType.OK);
  }

  public ValidatingTextInputDialog(String defaultValue, Predicate<String> validator) {
    this(defaultValue);
    setValidator(validator);
  }

  public ValidatingTextInputDialog() {
    this("");
  }

  public ValidatingTextInputDialog(Predicate<String> validator) {
    this("");
    setValidator(validator);
  }

  public void setValidator(Predicate<String> validator) {
    this.isValid = validator;
    this.getSubmitButtion().setDisable(!this.isValid.test(getEditor().getText()));
  }

  private void onSubmit(ActionEvent event) {
    String currentInput = getEditor().getText();
    if (!isValid.test(currentInput)) {
      event.consume();
      System.out.println("invalid");
      // show invalid
    }
  }
}
