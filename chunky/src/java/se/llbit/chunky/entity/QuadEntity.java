package se.llbit.chunky.entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.controlsfx.control.ToggleSwitch;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.SolidColorTexture;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.ui.DoubleTextField;
import se.llbit.chunky.ui.dialogs.EditMaterialDialog;
import se.llbit.chunky.ui.elements.AngleAdjuster;
import se.llbit.chunky.ui.elements.TextFieldLabelWrapper;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.log.Log;
import se.llbit.math.Matrix3;
import se.llbit.math.Quad;
import se.llbit.math.QuickMath;
import se.llbit.math.Transform;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.resources.ImageLoader;

public class QuadEntity extends Entity {
  private TextureMaterial material;
  private final Vector3 v1;
  private final Vector3 v2;
  private final Vector4 uv;
  private boolean doubleSided = true;
  private double pitch = 0;
  private double roll = 0;
  private double yaw = 0;

  public QuadEntity(Vector3 position) {
    super(position);
    v1 = new Vector3(1, 0, 0);
    v2 = new Vector3(0, 1, 0);
    uv = new Vector4(0, 1, 0, 1);

    this.material = new TextureMaterial(SolidColorTexture.EMPTY);
  }

  private void selectTexture(RenderControlsTab parent) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose texture");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
    File imageFile = fileChooser.showOpenDialog(parent.getScene().getWindow());
    if (imageFile != null) {
      try {
        JsonObject materialProperties = material.saveMaterialProperties();
        this.material = new TextureMaterial(new Texture(ImageLoader.read(imageFile)));
        this.material.loadMaterialProperties(materialProperties);
      } catch (IOException e) {
        Log.error("Failed to load texture: " + imageFile.getAbsolutePath(), e);
      }
    }
  }

  @Override
  public JsonValue toJson() {
    return null;
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    List<Primitive> triangles = new ArrayList<>();
    Vector3 pOffset = position.rAdd(offset);
    Matrix3 transform = new Matrix3();
    transform.rotate(-pitch, -yaw, -roll);
    Vector3 v12 = new Vector3(v1);
    Vector3 v22 = new Vector3(v2);
    transform.transform(v12);
    transform.transform(v22);
    pOffset.scaleAdd(-0.5, v12);
    pOffset.scaleAdd(-0.5, v22);
    new Quad(pOffset, pOffset.rAdd(v12), pOffset.rAdd(v22), uv, doubleSided).addTriangles(triangles, material, Transform.NONE);
    return triangles;
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();

    Button loadTexture = new Button();
    loadTexture.setText("Load texture");
    loadTexture.setOnAction(e -> {
      selectTexture(parent);
      scene.rebuildActorBvh();
    });

    DoubleTextField v1X = new DoubleTextField();
    DoubleTextField v1Y = new DoubleTextField();
    DoubleTextField v1Z = new DoubleTextField();

    DoubleTextField v2X = new DoubleTextField();
    DoubleTextField v2Y = new DoubleTextField();
    DoubleTextField v2Z = new DoubleTextField();

    DoubleTextField uv0X = new DoubleTextField();
    DoubleTextField uv0Y = new DoubleTextField();

    DoubleTextField uv1X = new DoubleTextField();
    DoubleTextField uv1Y = new DoubleTextField();

    v1X.valueProperty().setValue(v1.x);
    v1X.valueProperty().addListener((observable, oldValue, newValue) -> {
      v1.x = newValue.doubleValue();
      scene.rebuildActorBvh();
    });
    v1Y.valueProperty().setValue(v1.y);
    v1Y.valueProperty().addListener((observable, oldValue, newValue) -> {
      v1.y = newValue.doubleValue();
      scene.rebuildActorBvh();
    });
    v1Z.valueProperty().setValue(v1.z);
    v1Z.valueProperty().addListener((observable, oldValue, newValue) -> {
      v1.z = newValue.doubleValue();
      scene.rebuildActorBvh();
    });

    v2X.valueProperty().setValue(v2.x);
    v2X.valueProperty().addListener((observable, oldValue, newValue) -> {
      v2.x = newValue.doubleValue();
      scene.rebuildActorBvh();
    });
    v2Y.valueProperty().setValue(v2.y);
    v2Y.valueProperty().addListener((observable, oldValue, newValue) -> {
      v2.y = newValue.doubleValue();
      scene.rebuildActorBvh();
    });
    v2Z.valueProperty().setValue(v2.z);
    v2Z.valueProperty().addListener((observable, oldValue, newValue) -> {
      v2.z = newValue.doubleValue();
      scene.rebuildActorBvh();
    });

    uv0X.valueProperty().setValue(uv.x);
    uv0X.valueProperty().addListener((observable, oldValue, newValue) -> {
      uv.x = newValue.doubleValue();
      scene.rebuildActorBvh();
    });
    uv0Y.valueProperty().setValue(uv.y);
    uv0Y.valueProperty().addListener((observable, oldValue, newValue) -> {
      uv.y = newValue.doubleValue();
      scene.rebuildActorBvh();
    });

    uv1X.valueProperty().setValue(uv.z);
    uv1X.valueProperty().addListener((observable, oldValue, newValue) -> {
      uv.z = newValue.doubleValue();
      scene.rebuildActorBvh();
    });
    uv1Y.valueProperty().setValue(uv.w);
    uv1Y.valueProperty().addListener((observable, oldValue, newValue) -> {
      uv.w = newValue.doubleValue();
      scene.rebuildActorBvh();
    });

    TextFieldLabelWrapper v1XWrapper = new TextFieldLabelWrapper();
    v1XWrapper.setTextField(v1X);
    v1XWrapper.setLabelText("x:");

    TextFieldLabelWrapper v1YWrapper = new TextFieldLabelWrapper();
    v1YWrapper.setTextField(v1Y);
    v1YWrapper.setLabelText("y:");

    TextFieldLabelWrapper v1ZWrapper = new TextFieldLabelWrapper();
    v1ZWrapper.setTextField(v1Z);
    v1ZWrapper.setLabelText("z:");

    TextFieldLabelWrapper v2XWrapper = new TextFieldLabelWrapper();
    v2XWrapper.setTextField(v2X);
    v2XWrapper.setLabelText("x:");

    TextFieldLabelWrapper v2YWrapper = new TextFieldLabelWrapper();
    v2YWrapper.setTextField(v2Y);
    v2YWrapper.setLabelText("y:");

    TextFieldLabelWrapper v2ZWrapper = new TextFieldLabelWrapper();
    v2ZWrapper.setTextField(v2Z);
    v2ZWrapper.setLabelText("z:");

    TextFieldLabelWrapper uv0XWrapper = new TextFieldLabelWrapper();
    uv0XWrapper.setTextField(uv0X);
    uv0XWrapper.setLabelText("x:");

    TextFieldLabelWrapper uv0YWrapper = new TextFieldLabelWrapper();
    uv0YWrapper.setTextField(uv0Y);
    uv0YWrapper.setLabelText("y:");

    TextFieldLabelWrapper uv1XWrapper = new TextFieldLabelWrapper();
    uv1XWrapper.setTextField(uv1X);
    uv1XWrapper.setLabelText("x:");

    TextFieldLabelWrapper uv1YWrapper = new TextFieldLabelWrapper();
    uv1YWrapper.setTextField(uv1Y);
    uv1YWrapper.setLabelText("y:");

    GridPane positionPane = new GridPane();
    positionPane.setVgap(10);
    positionPane.setHgap(6);

    positionPane.addRow(0, new Label("V1"), v1XWrapper, v1YWrapper, v1ZWrapper);
    positionPane.addRow(1, new Label("V2"), v2XWrapper, v2YWrapper, v2ZWrapper);
    positionPane.addRow(2, new Label("UV0"), uv0XWrapper, uv0YWrapper);
    positionPane.addRow(3, new Label("UV1"), uv1XWrapper, uv1YWrapper);

    Button editMaterialButton = new Button();
    editMaterialButton.setText("Edit material");

    editMaterialButton.setOnAction(e -> {
      EditMaterialDialog dialog = new EditMaterialDialog(material, scene);
      dialog.showAndWait();
    });

    AngleAdjuster pitchAdjuster = new AngleAdjuster();
    AngleAdjuster rollAdjuster = new AngleAdjuster();
    AngleAdjuster yawAdjuster = new AngleAdjuster();

    pitchAdjuster.setName("Pitch");
    pitchAdjuster.valueProperty().setValue(pitch);
    pitchAdjuster.onValueChange(v -> {
      pitch = QuickMath.degToRad(v);
      scene.rebuildActorBvh();
    });

    rollAdjuster.setName("Roll");
    rollAdjuster.valueProperty().setValue(roll);
    rollAdjuster.onValueChange(v -> {
      roll = QuickMath.degToRad(v);
      scene.rebuildActorBvh();
    });

    yawAdjuster.setName("Yaw");
    yawAdjuster.valueProperty().setValue(yaw);
    yawAdjuster.onValueChange(v -> {
      yaw = QuickMath.degToRad(v);
      scene.rebuildActorBvh();
    });

    ToggleSwitch doubleSidedSwitch = new ToggleSwitch("Double-sided");
    doubleSidedSwitch.setSelected(doubleSided);
    doubleSidedSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> {
      doubleSided = newValue;
      scene.rebuildActorBvh();
    });

    return new VBox(6, loadTexture, positionPane, editMaterialButton, pitchAdjuster, rollAdjuster, yawAdjuster, doubleSidedSwitch);
  }
}
