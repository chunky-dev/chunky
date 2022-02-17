package se.llbit.chunky.ui.dialogs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import se.llbit.json.JsonObject;
import se.llbit.util.mojangapi.MojangApi;
import se.llbit.util.mojangapi.PlayerSkin;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SkinDownloadPopup extends Popup implements Initializable {

    public interface SkinURLCallback {
        void callback(PlayerSkin skin) throws IOException;
    }

    @FXML private VBox box;
    @FXML private Button okBtn;
    @FXML private Button cancelBtn;
    @FXML private TextField inputBox;
    @FXML private Label warningLabel;

    public SkinDownloadPopup(SkinURLCallback listener) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SkinDownloadPopup.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        getContent().add(root);

        cancelBtn.setOnAction( e -> {
            hide();
        });

        okBtn.setOnAction( e -> {
            try {
                JsonObject profile = MojangApi.fetchProfile(inputBox.getText()); //Search by uuid
                PlayerSkin skin = MojangApi.getSkinFromProfile(profile);
                if(skin != null) //If it found a skin, pass it back to caller
                {
                    listener.callback(skin);
                    hide();
                } else { //Otherwise, search by Username
                    String uuid = MojangApi.usernameToUUID(inputBox.getText());
                    profile = MojangApi.fetchProfile(uuid);
                    skin = MojangApi.getSkinFromProfile(profile);
                    if(skin != null) {
                        listener.callback(skin);
                        hide();
                    } else { //If still not found, warn user.
                        warningLabel.setVisible(true);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        warningLabel.setVisible(false);
        box.setStyle(
            "-fx-background-color: -fx-background;"
                + "-fx-border-color: -fx-accent;"
                + "-fx-border-style: solid;"
                + "-fx-border-width: 1px;");
    }
}
