package se.llbit.chunky.entity;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.SolidColorTexture;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
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

  public SphereEntity(Vector3 position, double radius) {
    super(position);
    this.material = new TextureMaterial(new SolidColorTexture(new Vector4(1, 1, 1, 1)));
    this.radius = radius;
  }

  public Material getMaterial() {
    return this.material;
  }

  public double getRadius() {
    return this.radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
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

    SphereEntity sphereEntity = new SphereEntity(position, radius);
    sphereEntity.getMaterial().loadMaterialProperties(json.get("materialProperties").asObject());

    return sphereEntity;
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();

    VBox controls = new VBox(6);

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

    TitledPane materialPropertiesPane = new TitledPane();
    materialPropertiesPane.setText("Material Properties");
    materialPropertiesPane.setContent(Material.getControls(this.material, scene));

    controls.getChildren().add(materialPropertiesPane);

    return controls;
  }
}
