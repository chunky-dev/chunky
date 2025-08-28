package se.llbit.chunky.renderer.scene.volumetricfog;

import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import se.llbit.chunky.renderer.scene.Scene;
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

public class CuboidFogVolume extends DiscreteFogVolume {
  private static final double DEFAULT_XMIN = -10;
  private static final double DEFAULT_XMAX = 10;
  private static final double DEFAULT_YMIN = 100;
  private static final double DEFAULT_YMAX = 120;
  private static final double DEFAULT_ZMIN = -10;
  private static final double DEFAULT_ZMAX = 10;

  private double xmin = DEFAULT_XMIN;
  private double xmax = DEFAULT_XMAX;
  private double ymin = DEFAULT_YMIN;
  private double ymax = DEFAULT_YMAX;
  private double zmin = DEFAULT_ZMIN;
  private double zmax = DEFAULT_ZMAX;

  private final AABB bounds = new AABB(
    this.xmin,
    this.xmax,
    this.ymin,
    this.ymax,
    this.zmin,
    this.zmax
  );

  @Override
  public boolean closestIntersection(Ray ray, IntersectionRecord intersectionRecord, Scene scene, Random random) {
    double distance;
    double distanceLimit;

    DoubleDoubleImmutablePair intersectionDistance = this.bounds.intersectionDistance(ray);
    if (Double.isNaN(intersectionDistance.leftDouble())) {
      return false;
    }

    double t0 = intersectionDistance.leftDouble();
    double t1 = intersectionDistance.rightDouble();

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
  public AABB bounds() {
    return this.bounds;
  }

  @Override
  public Collection<Primitive> primitives(Vector3 offset) {
    Collection<Primitive> primitives = new LinkedList<>();
    this.bounds.xmin = this.xmin + offset.x;
    this.bounds.xmax = this.xmax + offset.x;
    this.bounds.ymin = this.ymin + offset.y;
    this.bounds.ymax = this.ymax + offset.y;
    this.bounds.zmin = this.zmin + offset.z;
    this.bounds.zmax = this.zmax + offset.z;
    primitives.add(this);
    return primitives;
  }

  @Override
  public FogVolumeShape getShape() {
    return FogVolumeShape.CUBOID;
  }

  @Override
  public JsonObject saveVolumeSpecificConfiguration() {
    JsonObject json = new JsonObject();
    json.add("xmin", xmin);
    json.add("xmax", xmax);
    json.add("ymin", ymin);
    json.add("ymax", ymax);
    json.add("zmin", zmin);
    json.add("zmax", zmax);
    return json;
  }

  @Override
  public void loadVolumeSpecificConfiguration(JsonObject json) {
    xmin = json.get("xmin").doubleValue(xmin);
    xmax = json.get("xmax").doubleValue(xmax);
    ymin = json.get("ymin").doubleValue(ymin);
    ymax = json.get("ymax").doubleValue(ymax);
    zmin = json.get("zmin").doubleValue(zmin);
    zmax = json.get("zmax").doubleValue(zmax);
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();

    DoubleTextField x1 = new DoubleTextField();
    DoubleTextField y1 = new DoubleTextField();
    DoubleTextField z1 = new DoubleTextField();
    DoubleTextField x2 = new DoubleTextField();
    DoubleTextField y2 = new DoubleTextField();
    DoubleTextField z2 = new DoubleTextField();

    x1.setTooltip(new Tooltip("X-coordinate (east/west) of first corner"));
    y1.setTooltip(new Tooltip("Y-coordinate (up/down) of first corner"));
    z1.setTooltip(new Tooltip("Z-coordinate (south/north) of first corner"));
    x2.setTooltip(new Tooltip("X-coordinate (east/west) of second corner"));
    y2.setTooltip(new Tooltip("Y-coordinate (up/down) of second corner"));
    z2.setTooltip(new Tooltip("Z-coordinate (south/north) of second corner"));

    x1.valueProperty().setValue(this.bounds.xmin);
    y1.valueProperty().setValue(this.bounds.ymin);
    z1.valueProperty().setValue(this.bounds.zmin);
    x2.valueProperty().setValue(this.bounds.xmax);
    y2.valueProperty().setValue(this.bounds.ymax);
    z2.valueProperty().setValue(this.bounds.zmax);

    ChangeListener<Number> x1Listener = (observable, oldValue, newValue) -> {
      this.xmin = Math.min(newValue.doubleValue(), x2.valueProperty().doubleValue());
      this.xmax = Math.max(newValue.doubleValue(), x2.valueProperty().doubleValue());
      scene.buildFogVolumeBVH();
    };
    ChangeListener<Number> y1Listener = (observable, oldValue, newValue) -> {
      this.ymin = Math.min(newValue.doubleValue(), y2.valueProperty().doubleValue());
      this.ymax = Math.max(newValue.doubleValue(), y2.valueProperty().doubleValue());
      scene.buildFogVolumeBVH();
    };
    ChangeListener<Number> z1Listener = (observable, oldValue, newValue) -> {
      this.zmin = Math.min(newValue.doubleValue(), z2.valueProperty().doubleValue());
      this.zmax = Math.max(newValue.doubleValue(), z2.valueProperty().doubleValue());
      scene.buildFogVolumeBVH();
    };
    ChangeListener<Number> x2Listener = (observable, oldValue, newValue) -> {
      this.xmin = Math.min(x1.valueProperty().doubleValue(), newValue.doubleValue());
      this.xmax = Math.max(x1.valueProperty().doubleValue(), newValue.doubleValue());
      scene.buildFogVolumeBVH();
    };
    ChangeListener<Number> y2Listener = (observable, oldValue, newValue) -> {
      this.ymin = Math.min(y1.valueProperty().doubleValue(), newValue.doubleValue());
      this.ymax = Math.max(y1.valueProperty().doubleValue(), newValue.doubleValue());
      scene.buildFogVolumeBVH();
    };
    ChangeListener<Number> z2Listener = (observable, oldValue, newValue) -> {
      this.zmin = Math.min(z1.valueProperty().doubleValue(), newValue.doubleValue());
      this.zmax = Math.max(z1.valueProperty().doubleValue(), newValue.doubleValue());
      scene.buildFogVolumeBVH();
    };

    x1.valueProperty().addListener(x1Listener);
    y1.valueProperty().addListener(y1Listener);
    z1.valueProperty().addListener(z1Listener);

    x2.valueProperty().addListener(x2Listener);
    y2.valueProperty().addListener(y2Listener);
    z2.valueProperty().addListener(z2Listener);

    TextFieldLabelWrapper x1Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper y1Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper z1Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper x2Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper y2Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper z2Text = new TextFieldLabelWrapper();

    x1Text.setTextField(x1);
    y1Text.setTextField(y1);
    z1Text.setTextField(z1);
    x2Text.setTextField(x2);
    y2Text.setTextField(y2);
    z2Text.setTextField(z2);

    x1Text.setLabelText("x:");
    y1Text.setLabelText("y:");
    z1Text.setLabelText("z:");
    x2Text.setLabelText("x:");
    y2Text.setLabelText("y:");
    z2Text.setLabelText("z:");

    Button pos1ToCamera = new Button();
    pos1ToCamera.setText("To camera");
    pos1ToCamera.setOnAction(event -> {
      Vector3 cameraPosition = scene.camera().getPosition();
      this.xmin = Math.min(cameraPosition.x, x2.valueProperty().doubleValue());
      this.xmax = Math.max(cameraPosition.y, x2.valueProperty().doubleValue());

      this.ymin = Math.min(cameraPosition.y, y2.valueProperty().doubleValue());
      this.ymax = Math.max(cameraPosition.y, y2.valueProperty().doubleValue());

      this.zmin = Math.min(cameraPosition.z, z2.valueProperty().doubleValue());
      this.zmax = Math.max(cameraPosition.z, z2.valueProperty().doubleValue());

      x1.valueProperty().removeListener(x1Listener);
      y1.valueProperty().removeListener(y1Listener);
      z1.valueProperty().removeListener(z1Listener);

      x1.valueProperty().setValue(cameraPosition.x);
      y1.valueProperty().setValue(cameraPosition.y);
      z1.valueProperty().setValue(cameraPosition.z);

      x1.valueProperty().addListener(x1Listener);
      y1.valueProperty().addListener(y1Listener);
      z1.valueProperty().addListener(z1Listener);
      scene.buildFogVolumeBVH();
    });

    Button pos1ToTarget = new Button();
    pos1ToTarget.setText("To target");
    pos1ToTarget.setOnAction(event -> {
      Vector3 targetPosition = scene.getTargetPosition();
      if (targetPosition != null) {
        this.xmin = Math.min(targetPosition.x, x2.valueProperty().doubleValue());
        this.xmax = Math.max(targetPosition.y, x2.valueProperty().doubleValue());

        this.ymin = Math.min(targetPosition.y, y2.valueProperty().doubleValue());
        this.ymax = Math.max(targetPosition.y, y2.valueProperty().doubleValue());

        this.zmin = Math.min(targetPosition.z, z2.valueProperty().doubleValue());
        this.zmax = Math.max(targetPosition.z, z2.valueProperty().doubleValue());

        x1.valueProperty().removeListener(x1Listener);
        y1.valueProperty().removeListener(y1Listener);
        z1.valueProperty().removeListener(z1Listener);

        x1.valueProperty().setValue(targetPosition.x);
        y1.valueProperty().setValue(targetPosition.y);
        z1.valueProperty().setValue(targetPosition.z);

        x1.valueProperty().addListener(x1Listener);
        y1.valueProperty().addListener(y1Listener);
        z1.valueProperty().addListener(z1Listener);
        scene.buildFogVolumeBVH();
      }
    });

    Button pos2ToCamera = new Button();
    pos2ToCamera.setText("To camera");
    pos2ToCamera.setOnAction(event -> {
      Vector3 cameraPosition = scene.camera().getPosition();
      this.xmin = Math.min(x1.valueProperty().doubleValue(), cameraPosition.x);
      this.xmax = Math.max(x1.valueProperty().doubleValue(), cameraPosition.x);

      this.ymin = Math.min(y1.valueProperty().doubleValue(), cameraPosition.y);
      this.ymax = Math.max(y1.valueProperty().doubleValue(), cameraPosition.y);

      this.zmin = Math.min(z1.valueProperty().doubleValue(), cameraPosition.z);
      this.zmax = Math.max(z1.valueProperty().doubleValue(), cameraPosition.z);

      x2.valueProperty().removeListener(x2Listener);
      y2.valueProperty().removeListener(y2Listener);
      z2.valueProperty().removeListener(z2Listener);

      x2.valueProperty().setValue(cameraPosition.x);
      y2.valueProperty().setValue(cameraPosition.y);
      z2.valueProperty().setValue(cameraPosition.z);

      x2.valueProperty().addListener(x2Listener);
      y2.valueProperty().addListener(y2Listener);
      z2.valueProperty().addListener(z2Listener);
      scene.buildFogVolumeBVH();
    });

    Button pos2ToTarget = new Button();
    pos2ToTarget.setText("To target");
    pos2ToTarget.setOnAction(event -> {
      Vector3 targetPosition = scene.getTargetPosition();
      if (targetPosition != null) {
        this.xmin = Math.min(x1.valueProperty().doubleValue(), targetPosition.x);
        this.xmax = Math.max(x1.valueProperty().doubleValue(), targetPosition.x);

        this.ymin = Math.min(y1.valueProperty().doubleValue(), targetPosition.y);
        this.ymax = Math.max(y1.valueProperty().doubleValue(), targetPosition.y);

        this.zmin = Math.min(z1.valueProperty().doubleValue(), targetPosition.z);
        this.zmax = Math.max(z1.valueProperty().doubleValue(), targetPosition.z);

        x2.valueProperty().removeListener(x2Listener);
        y2.valueProperty().removeListener(y2Listener);
        z2.valueProperty().removeListener(z2Listener);

        x2.valueProperty().setValue(targetPosition.x);
        y2.valueProperty().setValue(targetPosition.y);
        z2.valueProperty().setValue(targetPosition.z);

        x2.valueProperty().addListener(x2Listener);
        y2.valueProperty().addListener(y2Listener);
        z2.valueProperty().addListener(z2Listener);
        scene.buildFogVolumeBVH();
      }
    });

    ColumnConstraints labelConstraints = new ColumnConstraints();
    labelConstraints.setHgrow(Priority.NEVER);
    labelConstraints.setPrefWidth(90);
    ColumnConstraints posFieldConstraints = new ColumnConstraints();
    posFieldConstraints.setMinWidth(20);
    posFieldConstraints.setPrefWidth(90);

    GridPane gridPane1 = new GridPane();
    gridPane1.setHgap(6);
    gridPane1.getColumnConstraints().addAll(
      labelConstraints,
      posFieldConstraints,
      posFieldConstraints,
      posFieldConstraints
    );
    gridPane1.addRow(0, new Label("Corner 1:"), x1Text, y1Text, z1Text);

    HBox hBox1 = new HBox();
    hBox1.setSpacing(10);
    hBox1.getChildren().addAll(pos1ToCamera, pos1ToTarget);

    GridPane gridPane2 = new GridPane();
    gridPane2.setHgap(6);
    gridPane2.getColumnConstraints().addAll(
      labelConstraints,
      posFieldConstraints,
      posFieldConstraints,
      posFieldConstraints
    );
    gridPane2.addRow(1, new Label("Corner 2:"), x2Text, y2Text, z2Text);

    HBox hBox2 = new HBox();
    hBox2.setSpacing(10);
    hBox2.getChildren().addAll(pos2ToCamera, pos2ToTarget);

    VBox noiseControls = this.noiseConfig.getControls(parent);

    return new VBox(6, gridPane1, hBox1, gridPane2, hBox2, new Separator(), noiseControls);
  }
}
