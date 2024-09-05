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
import java.util.LinkedList;

public class SphereEntity extends Entity {
  private final Material material;
  private double radius;
  private final Vector4 color;

  public SphereEntity(Vector3 position, Vector4 color, double radius) {
    super(position);
    this.color = color;
    this.material = new TextureMaterial(new SolidColorTexture(this.color));
    this.radius = radius;
  }

  public Material getMaterial() {
    return this.material;
  }

  public double getRadius() {
    return this.radius;
  }

  public Vector4 getColor() {
    return new Vector4(color);
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  public void setColor(Vector4 color) {
    this.color.set(color);
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
    json.add("color", ColorUtil.rgbToJson(color.toVec3()));
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

    SphereEntity sphereEntity = new SphereEntity(position, new Vector4(color.x, color.y, color.z, 1), radius);
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
      scene.refresh();
    });
    controls.getChildren().add(emittanceAdjuster);

    DoubleAdjuster alphaAdjuster = new DoubleAdjuster();
    alphaAdjuster.setName("Alpha");
    alphaAdjuster.setTooltip("Set the alpha (opacity) of the sphere material.");
    alphaAdjuster.setRange(0, 1);
    alphaAdjuster.clampBoth();
    alphaAdjuster.set(this.material.alpha);
    alphaAdjuster.onValueChange(value -> {
      this.material.alpha = value.floatValue();
      scene.refresh();
    });
    controls.getChildren().add(alphaAdjuster);

    DoubleAdjuster subsurfaceScatteringAdjuster = new DoubleAdjuster();
    subsurfaceScatteringAdjuster.setName("Subsurface scattering");
    subsurfaceScatteringAdjuster.setTooltip("Probability of a ray to be scattered behind the surface.");
    subsurfaceScatteringAdjuster.setRange(0, 1);
    subsurfaceScatteringAdjuster.clampBoth();
    subsurfaceScatteringAdjuster.set(this.material.subSurfaceScattering);
    subsurfaceScatteringAdjuster.onValueChange( value -> {
      this.material.subSurfaceScattering = value.floatValue();
      scene.refresh();
    });
    controls.getChildren().add(subsurfaceScatteringAdjuster);

    Label colorLabel = new Label("Color:");
    colorLabel.setTooltip(new Tooltip("Set the color of the sphere."));
    LuxColorPicker colorPicker = new LuxColorPicker();
    colorPicker.setColor(ColorUtil.toFx(this.color.toVec3()));
    colorPicker.colorProperty().addListener(
      ((observable, oldValue, newValue) -> {
        Vector3 color = ColorUtil.fromFx(newValue);
        this.setColor(new Vector4(color.x, color.y, color.z, 1));
        scene.refresh();
      })
    );
    HBox colorPickerBox = new HBox(10, colorLabel, colorPicker);
    controls.getChildren().add(colorPickerBox);

    DoubleAdjuster specularAdjuster = new DoubleAdjuster();
    specularAdjuster.setName("Specular");
    specularAdjuster.setTooltip("Set the specular of the sphere material.");
    specularAdjuster.setRange(0, 1);
    specularAdjuster.clampBoth();
    specularAdjuster.set(this.material.specular);
    specularAdjuster.onValueChange(value -> {
      this.material.specular = value.floatValue();
      scene.refresh();
    });
    controls.getChildren().add(specularAdjuster);

    DoubleAdjuster iorAdjuster = new DoubleAdjuster();
    iorAdjuster.setName("IoR");
    iorAdjuster.setTooltip("Set the IoR of the sphere material.");
    iorAdjuster.setRange(0, 5);
    iorAdjuster.clampMin();
    iorAdjuster.setMaximumFractionDigits(6);
    iorAdjuster.set(this.material.ior);
    iorAdjuster.onValueChange(value -> {
      this.material.ior = value.floatValue();
      scene.refresh();
    });
    controls.getChildren().add(iorAdjuster);

    DoubleAdjuster smoothnessAdjuster = new DoubleAdjuster();
    smoothnessAdjuster.setName("Smoothness");
    smoothnessAdjuster.setTooltip("Set the smoothness of the sphere material.");
    smoothnessAdjuster.setRange(0, 1);
    smoothnessAdjuster.clampBoth();
    smoothnessAdjuster.set(this.material.getPerceptualSmoothness());
    smoothnessAdjuster.onValueChange(value -> {
      this.material.setPerceptualSmoothness(value);
      scene.refresh();
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
      scene.refresh();
    });
    controls.getChildren().add(transmissionSmoothnessAdjuster);

    DoubleAdjuster metalnessAdjuster = new DoubleAdjuster();
    metalnessAdjuster.setName("Metalness");
    metalnessAdjuster.setTooltip("Texture tinting of reflected light.");
    metalnessAdjuster.setRange(0, 1);
    metalnessAdjuster.clampBoth();
    metalnessAdjuster.set(this.material.metalness);
    metalnessAdjuster.onValueChange(value -> {
      this.material.metalness = value.floatValue();
      scene.refresh();
    });
    controls.getChildren().add(metalnessAdjuster);

    DoubleAdjuster transmissionMetalnessAdjuster = new DoubleAdjuster();
    transmissionMetalnessAdjuster.setName("Transmission metalness");
    transmissionMetalnessAdjuster.setTooltip("Texture tinting of refracted/transmitted light.");
    transmissionMetalnessAdjuster.setRange(0, 1);
    transmissionMetalnessAdjuster.clampBoth();
    transmissionMetalnessAdjuster.set(this.material.transmissionMetalness);
    transmissionMetalnessAdjuster.onValueChange(value -> {
      this.material.transmissionMetalness = value.floatValue();
      scene.refresh();
    });
    controls.getChildren().add(transmissionMetalnessAdjuster);

    Label specularColorLabel = new Label("Specular color:");
    specularColorLabel.setTooltip(new Tooltip("Set the specular color of the sphere."));
    LuxColorPicker specularColorPicker = new LuxColorPicker();
    specularColorPicker.setColor(ColorUtil.toFx(this.material.specularColor));
    specularColorPicker.colorProperty().addListener(
      ((observable, oldValue, newValue) -> {
        this.material.specularColor.set(ColorUtil.fromFx(newValue));
        scene.refresh();
      })
    );
    HBox specularColorPickerBox = new HBox(10, specularColorLabel, specularColorPicker);
    controls.getChildren().add(specularColorPickerBox);

    Label transmissionSpecularColorLabel = new Label("Transmission specular color:");
    transmissionSpecularColorLabel.setTooltip(new Tooltip("Set the transmission specular color of the sphere."));
    LuxColorPicker transmissionSpecularColorPicker = new LuxColorPicker();
    transmissionSpecularColorPicker.setColor(ColorUtil.toFx(this.material.transmissionSpecularColor));
    transmissionSpecularColorPicker.colorProperty().addListener(
      ((observable, oldValue, newValue) -> {
        this.material.transmissionSpecularColor.set(ColorUtil.fromFx(newValue));
        scene.refresh();
      })
    );
    HBox transmissionSpecularColorPickerBox = new HBox(10, transmissionSpecularColorLabel, transmissionSpecularColorPicker);
    controls.getChildren().add(transmissionSpecularColorPickerBox);

    DoubleAdjuster volumeDensityAdjuster = new DoubleAdjuster();
    volumeDensityAdjuster.setName("Volume density");
    volumeDensityAdjuster.setTooltip("Density of volume medium.");
    volumeDensityAdjuster.setRange(0, 1);
    volumeDensityAdjuster.clampMin();
    volumeDensityAdjuster.set(this.material.volumeDensity);
    volumeDensityAdjuster.onValueChange( value -> {
      this.material.volumeDensity = value.floatValue();
      scene.refresh();
    });
    controls.getChildren().add(volumeDensityAdjuster);

    DoubleAdjuster volumeAnisotropyAdjuster = new DoubleAdjuster();
    volumeAnisotropyAdjuster.setName("Volume anisotropy");
    volumeAnisotropyAdjuster.setTooltip("Changes the direction light is more likely to be scattered.\n" +
      "Positive values increase the chance light scatters into its original direction of travel.\n" +
      "Negative values increase the chance light scatters away from its original direction of travel.");
    volumeAnisotropyAdjuster.setRange(-1, 1);
    volumeAnisotropyAdjuster.clampBoth();
    volumeAnisotropyAdjuster.set(this.material.volumeAnisotropy);
    volumeAnisotropyAdjuster.onValueChange( value -> {
      this.material.volumeAnisotropy = value.floatValue();
      scene.refresh();
    });
    controls.getChildren().add(volumeAnisotropyAdjuster);

    DoubleAdjuster volumeEmittanceAdjuster = new DoubleAdjuster();
    volumeEmittanceAdjuster.setName("Volume emittance");
    volumeEmittanceAdjuster.setTooltip("Emittance of volume medium.");
    volumeEmittanceAdjuster.setRange(0, 100);
    volumeEmittanceAdjuster.clampMin();
    volumeEmittanceAdjuster.set(this.material.volumeEmittance);
    volumeEmittanceAdjuster.onValueChange( value -> {
      this.material.volumeEmittance = value.floatValue();
      scene.refresh();
    });
    controls.getChildren().add(volumeEmittanceAdjuster);

    Label volumeColorLabel = new Label("Volume color:");
    volumeColorLabel.setTooltip(new Tooltip("Set the volume color of the sphere."));
    LuxColorPicker volumeColorPicker = new LuxColorPicker();
    volumeColorPicker.setColor(ColorUtil.toFx(this.material.volumeColor));
    volumeColorPicker.colorProperty().addListener(
      ((observable, oldValue, newValue) -> {
        this.material.volumeColor.set(ColorUtil.fromFx(newValue));
        scene.refresh();
      })
    );
    HBox volumeColorPickerBox = new HBox(10, volumeColorLabel, volumeColorPicker);
    controls.getChildren().add(volumeColorPickerBox);

    controls.setSpacing(10);

    return controls;
  }
}
