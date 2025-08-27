package se.llbit.chunky.renderer.scene.volumetricfog;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.DoubleTextField;
import se.llbit.chunky.ui.elements.TextFieldLabelWrapper;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.world.Material;
import se.llbit.json.JsonObject;
import se.llbit.math.*;
import se.llbit.math.primitive.Primitive;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

public class SphericalFogVolume extends DiscreteFogVolume {
  private static final double DEFAULT_X = 0;
  private static final double DEFAULT_Y = 100;
  private static final double DEFAULT_Z = 0;
  private static final double DEFAULT_RADIUS = 10;

  private final Vector3 position = new Vector3(DEFAULT_X, DEFAULT_Y, DEFAULT_Z);
  private final Vector3 center = new Vector3();
  private double radius = DEFAULT_RADIUS;

  @Override
  public AABB bounds() {
    return new AABB(
      center.x - radius,
      center.x + radius,
      center.y - radius,
      center.y + radius,
      center.z - radius,
      center.z + radius
    );
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> primitives = new LinkedList<>();
    this.center.set(this.position.rAdd(offset));
    primitives.add(this);
    return primitives;
  }

  @Override
  public boolean closestIntersection(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene, Random random) {
    double t0;
    double t1;

    double radiusSquared = radius * radius;

    Vector3 l = center.rSub(ray.o);
    double tca = ray.d.dot(l);
    if (tca < 0.0 && l.length() > radius) {
      return false;
    }

    double d2 = l.dot(l) - tca * tca;
    if (d2 > radiusSquared) {
      return false;
    }
    double thc = FastMath.sqrt(radiusSquared - d2);
    t0 = tca - thc;
    t1 = tca + thc;

    if (t0 > t1) {
      double tmp = t0;
      t0 = t1;
      t1 = tmp;
    }

    double distance;
    double distanceLimit;

    if (t1 < 0) {
      return false;
    } else if (t0 < 0) {
      distance = 0;
      distanceLimit = t1;
    } else if (t0 > intersectionRecord.distance) {
      return false;
    } else {
      distance = t0;
      distanceLimit = t1;
    }

    Vector3 o = new Vector3(ray.o);
    o.scaleAdd(distance, ray.d);
    for (int i = 0; i < this.noiseConfig.marchSteps; i++) {
      double dist = Material.fogDistance(this.material.volumeDensity, random);
      if (dist + distance > intersectionRecord.distance || dist + distance > distanceLimit) {
        // The ray would have encountered enough fog to be scattered, but something is in the way.
        return false;
      }
      if (noiseConfig.useNoise) {
        o.scaleAdd(dist, ray.d);
        float noiseValue = noiseConfig.calculate((float) o.x, (float) o.y, (float) o.z);
        if (noiseConfig.cutoff) {
          if (noiseValue < noiseConfig.lowerThreshold || noiseValue > noiseConfig.upperThreshold) {
            distance += dist;
            continue;
          }
        } else {
          if (random.nextDouble(noiseConfig.lowerThreshold, noiseConfig.upperThreshold + Constants.EPSILON) > noiseValue) {
            distance += dist;
            continue;
          }
        }
      }
      intersectionRecord.distance = dist + distance;
      intersectionRecord.material = this.material;
      intersectionRecord.color.set(material.volumeColor.x, material.volumeColor.y, material.volumeColor.z, 1);
      intersectionRecord.flags |= IntersectionRecord.VOLUME_INTERSECT;
      return true;
    }
    return false;
  }

  @Override
  public FogVolumeShape getShape() {
    return FogVolumeShape.SPHERE;
  }

  @Override
  public JsonObject saveVolumeSpecificConfiguration() {
    JsonObject json = new JsonObject();
    json.add("position", position.toJson());
    json.add("radius", radius);
    return json;
  }

  @Override
  public void loadVolumeSpecificConfiguration(JsonObject json) {
    position.fromJson(json.get("position").asObject());
    radius = json.get("radius").doubleValue(radius);
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();

    DoubleTextField posX = new DoubleTextField();
    DoubleTextField posY = new DoubleTextField();
    DoubleTextField posZ = new DoubleTextField();

    posX.setTooltip(new Tooltip("Sphere x-coordinate (east/west)"));
    posY.setTooltip(new Tooltip("Sphere y-coordinate (up/down)"));
    posZ.setTooltip(new Tooltip("Sphere z-coordinate (south/north)"));

    posX.valueProperty().setValue(this.position.x);
    posY.valueProperty().setValue(this.position.y);
    posZ.valueProperty().setValue(this.position.z);

    ChangeListener<Number> posXListener = (observable, oldValue, newValue) -> {
      this.position.x = newValue.doubleValue();
      scene.buildFogVolumeBVH();
    };
    ChangeListener<Number> posYListener = (observable, oldValue, newValue) -> {
      this.position.y = newValue.doubleValue();
      scene.buildFogVolumeBVH();
    };
    ChangeListener<Number> posZListener = (observable, oldValue, newValue) -> {
      this.position.z = newValue.doubleValue();
      scene.buildFogVolumeBVH();
    };

    posX.valueProperty().addListener(posXListener);
    posY.valueProperty().addListener(posYListener);
    posZ.valueProperty().addListener(posZListener);

    TextFieldLabelWrapper xText = new TextFieldLabelWrapper();
    TextFieldLabelWrapper yText = new TextFieldLabelWrapper();
    TextFieldLabelWrapper zText = new TextFieldLabelWrapper();

    xText.setTextField(posX);
    yText.setTextField(posY);
    zText.setTextField(posZ);

    xText.setLabelText("x:");
    yText.setLabelText("y:");
    zText.setLabelText("z:");

    Button toCamera = new Button();
    toCamera.setText("To camera");
    toCamera.setOnAction(event -> {
      Vector3 cameraPosition = scene.camera().getPosition();
      this.position.x = cameraPosition.x;
      this.position.y = cameraPosition.y;
      this.position.z = cameraPosition.z;

      posX.valueProperty().removeListener(posXListener);
      posY.valueProperty().removeListener(posYListener);
      posZ.valueProperty().removeListener(posZListener);

      posX.valueProperty().setValue(cameraPosition.x);
      posY.valueProperty().setValue(cameraPosition.y);
      posZ.valueProperty().setValue(cameraPosition.z);

      posX.valueProperty().addListener(posXListener);
      posY.valueProperty().addListener(posYListener);
      posZ.valueProperty().addListener(posZListener);
      scene.buildFogVolumeBVH();
    });

    Button toTarget = new Button();
    toTarget.setText("To target");
    toTarget.setOnAction(event -> {
      Vector3 targetPosition = scene.getTargetPosition();
      if (targetPosition != null) {
        this.position.x = targetPosition.x;
        this.position.y = targetPosition.y;
        this.position.z = targetPosition.z;

        posX.valueProperty().removeListener(posXListener);
        posY.valueProperty().removeListener(posYListener);
        posZ.valueProperty().removeListener(posZListener);

        posX.valueProperty().setValue(targetPosition.x);
        posY.valueProperty().setValue(targetPosition.y);
        posZ.valueProperty().setValue(targetPosition.z);

        posX.valueProperty().addListener(posXListener);
        posY.valueProperty().addListener(posYListener);
        posZ.valueProperty().addListener(posZListener);
        scene.buildFogVolumeBVH();
      }
    });

    ColumnConstraints labelConstraints = new ColumnConstraints();
    labelConstraints.setHgrow(Priority.NEVER);
    labelConstraints.setPrefWidth(90);
    ColumnConstraints posFieldConstraints = new ColumnConstraints();
    posFieldConstraints.setMinWidth(20);
    posFieldConstraints.setPrefWidth(90);

    GridPane gridPane = new GridPane();
    gridPane.setHgap(6);
    gridPane.getColumnConstraints().addAll(
      labelConstraints,
      posFieldConstraints,
      posFieldConstraints,
      posFieldConstraints
    );
    gridPane.addRow(0, new Label("Center:"), xText, yText, zText);

    HBox hBox = new HBox();
    hBox.setSpacing(10);
    hBox.getChildren().addAll(toCamera, toTarget);

    DoubleAdjuster radius = new DoubleAdjuster();
    radius.setName("Radius");
    radius.setTooltip("Radius of the sphere");
    radius.setRange(0.001, 100);
    radius.set(this.radius);
    radius.clampMin();
    radius.onValueChange(value -> {
      this.radius = value;
      scene.buildFogVolumeBVH();
    });

    VBox noiseControls = this.noiseConfig.getControls(parent);

    return new VBox(6, gridPane, hBox, radius, new Separator(), noiseControls);
  }
}
