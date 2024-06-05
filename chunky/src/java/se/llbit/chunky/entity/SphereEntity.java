package se.llbit.chunky.entity;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.SolidColorTexture;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.fx.LuxColorPicker;
import se.llbit.json.JsonObject;
import se.llbit.json.JsonValue;
import se.llbit.math.ColorUtil;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;
import se.llbit.math.primitive.Primitive;
import se.llbit.math.primitive.Sphere;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class SphereEntity extends Entity {
  private BVHMaterial material;

  private double radius;

  private final Vector3 color;

  public SphereEntity(Vector3 position, Vector3 color, double radius) {
    super(position);
    this.material = new BVHMaterial(new SolidColorTexture(new Vector4(color.x, color.y, color.z, 1)));
    this.radius = radius;
    this.color = color;
  }

  public Material getMaterial() {
    return this.material;
  }

  public double getRadius() {
    return this.radius;
  }

  public Vector3 getColor() {
    return new Vector3(color);
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  public void setColor(Vector3 color) {
    this.color.set(color);
    JsonObject properties = this.material.saveMaterialProperties();

    this.material = new BVHMaterial(new SolidColorTexture(new Vector4(color.x, color.y, color.z, 1)));
    this.material.loadMaterialProperties(properties);
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Sphere sphere = new Sphere(position.rAdd(offset), radius, material);
    Collection<Primitive> primitives = new LinkedList<>();
    primitives.add(sphere);
    return primitives;
  }

  @Override
  public JsonValue toJson() {
    JsonObject json = new JsonObject();
    json.add("kind", "sphere");
    json.add("position", position.toJson());
    json.add("radius", radius);
    json.add("color", ColorUtil.rgbToJson(color));
    json.add("materialProperties", material.saveMaterialProperties());
    return json;
  }

  /**
   * Deserialize entity from JSON.
   *
   * @return deserialized entity, or {@code null} if it was not a valid entity
   */
  public static Entity fromJson(JsonObject json) {
    Vector3 position = new Vector3();
    position.fromJson(json.get("position").object());
    Vector3 color = ColorUtil.jsonToRGB(json.get("color").asObject());
    double radius = json.get("radius").doubleValue(0.5);

    SphereEntity sphereEntity = new SphereEntity(position, color, radius);
    sphereEntity.getMaterial().loadMaterialProperties(json.get("materialProperties").asObject());

    return sphereEntity;
  }

  @Override
  public VBox getControls(Node tab, Scene scene) {
    VBox controls = new VBox();

    DoubleAdjuster radiusAdjuster = new DoubleAdjuster();
    radiusAdjuster.setName("Radius");
    radiusAdjuster.setTooltip("Set the radius of the sphere.");
    radiusAdjuster.setRange(0.001, 50);
    radiusAdjuster.clampMin();
    radiusAdjuster.set(this.radius);
    radiusAdjuster.onValueChange(value -> {
      this.setRadius(value);
      scene.rebuildBvh();
    });
    controls.getChildren().add(radiusAdjuster);

    DoubleAdjuster emittanceAdjuster = new DoubleAdjuster();
    emittanceAdjuster.setName("Emittance");
    emittanceAdjuster.setTooltip("Set the emittance of the sphere material.");
    emittanceAdjuster.setRange(0, 100);
    emittanceAdjuster.clampMin();
    emittanceAdjuster.set(this.material.emittance);
    emittanceAdjuster.onValueChange(value -> {
      this.material.emittance = value.floatValue();
      scene.rebuildBvh();
    });
    controls.getChildren().add(emittanceAdjuster);

    DoubleAdjuster specularAdjuster = new DoubleAdjuster();
    specularAdjuster.setName("Specular");
    specularAdjuster.setTooltip("Set the specular of the sphere material.");
    specularAdjuster.setRange(0, 1);
    specularAdjuster.clampBoth();
    specularAdjuster.set(this.material.specular);
    specularAdjuster.onValueChange(value -> {
      this.material.specular = value.floatValue();
      scene.rebuildBvh();
    });
    controls.getChildren().add(specularAdjuster);

    DoubleAdjuster smoothnessAdjuster = new DoubleAdjuster();
    smoothnessAdjuster.setName("Smoothness");
    smoothnessAdjuster.setTooltip("Set the smoothness of the sphere material.");
    smoothnessAdjuster.setRange(0, 1);
    smoothnessAdjuster.clampBoth();
    smoothnessAdjuster.set(this.material.getPerceptualSmoothness());
    smoothnessAdjuster.onValueChange(value -> {
      this.material.setPerceptualSmoothness(value);
      scene.rebuildBvh();
    });
    controls.getChildren().add(smoothnessAdjuster);

    DoubleAdjuster transmissionSmoothnessAdjuster = new DoubleAdjuster();
    transmissionSmoothnessAdjuster.setName("Transmission smoothness");
    transmissionSmoothnessAdjuster.setTooltip("Set the transmission smoothness of the sphere material.");
    transmissionSmoothnessAdjuster.setRange(0, 1);
    transmissionSmoothnessAdjuster.clampBoth();
    transmissionSmoothnessAdjuster.set(this.material.getPerceptualTransmissionSmoothness());
    transmissionSmoothnessAdjuster.onValueChange(value -> {
      this.material.setPerceptualTransmissionSmoothness(value);
      scene.rebuildBvh();
    });
    controls.getChildren().add(transmissionSmoothnessAdjuster);

    DoubleAdjuster iorAdjuster = new DoubleAdjuster();
    iorAdjuster.setName("IoR");
    iorAdjuster.setTooltip("Set the IoR of the sphere material.");
    iorAdjuster.setRange(0, 5);
    iorAdjuster.clampMin();
    iorAdjuster.setMaximumFractionDigits(6);
    iorAdjuster.set(this.material.ior);
    iorAdjuster.onValueChange(value -> {
      this.material.ior = value.floatValue();
      scene.rebuildBvh();
    });
    controls.getChildren().add(iorAdjuster);

    DoubleAdjuster metalnessAdjuster = new DoubleAdjuster();
    metalnessAdjuster.setName("Metalness");
    metalnessAdjuster.setTooltip("Set the metalness of the sphere material.");
    metalnessAdjuster.setRange(0, 1);
    metalnessAdjuster.clampBoth();
    metalnessAdjuster.set(this.material.metalness);
    metalnessAdjuster.onValueChange(value -> {
      this.material.metalness = value.floatValue();
      scene.rebuildBvh();
    });
    controls.getChildren().add(metalnessAdjuster);

    DoubleAdjuster alphaAdjuster = new DoubleAdjuster();
    alphaAdjuster.setName("Alpha");
    alphaAdjuster.setTooltip("Set the alpha (opacity) of the sphere material.");
    alphaAdjuster.setRange(0, 1);
    alphaAdjuster.clampBoth();
    alphaAdjuster.set(this.material.alpha);
    alphaAdjuster.onValueChange(value -> {
      this.material.alpha = value.floatValue();
      scene.rebuildBvh();
    });
    controls.getChildren().add(alphaAdjuster);

    HBox colorPickerBox = new HBox();
    colorPickerBox.setSpacing(10);

    Label label = new Label("Color:");
    label.setTooltip(new Tooltip("Set the color of the sphere."));
    LuxColorPicker colorPicker = new LuxColorPicker();
    colorPicker.setColor(ColorUtil.toFx(this.color));
    colorPicker.colorProperty().addListener(
      ((observable, oldValue, newValue) -> {
        this.setColor(ColorUtil.fromFx(newValue));
        scene.rebuildBvh();
      })
    );
    colorPickerBox.getChildren().addAll(label, colorPicker);

    controls.getChildren().add(colorPickerBox);
    return controls;
  }
}
