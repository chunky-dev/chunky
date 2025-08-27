package se.llbit.chunky.renderer.scene;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import se.llbit.chunky.block.minecraft.Air;
import se.llbit.chunky.resources.SolidColorTexture;
import se.llbit.chunky.ui.DoubleTextField;
import se.llbit.chunky.ui.dialogs.EditMaterialDialog;
import se.llbit.chunky.ui.elements.TextFieldLabelWrapper;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.chunky.world.Clouds;
import se.llbit.chunky.world.Material;
import se.llbit.chunky.world.material.TextureMaterial;
import se.llbit.json.JsonObject;
import se.llbit.math.Intersectable;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray2;
import se.llbit.math.Vector4;
import se.llbit.util.Configurable;
import se.llbit.util.HasControls;
import se.llbit.util.Pair;

import java.util.Random;

public class CloudLayer implements Intersectable, Configurable, HasControls {
  /**
   * Default cloud y-position
   */
  private static final int DEFAULT_CLOUD_HEIGHT = 128;
  private static final int DEFAULT_CLOUD_SIZE = 12;
  private static final int DEFAULT_CLOUD_THICKNESS = 4;

  private final SimpleDoubleProperty scaleX = new SimpleDoubleProperty(DEFAULT_CLOUD_SIZE);
  private final SimpleDoubleProperty scaleY = new SimpleDoubleProperty(DEFAULT_CLOUD_THICKNESS);
  private final SimpleDoubleProperty scaleZ = new SimpleDoubleProperty(DEFAULT_CLOUD_SIZE);
  private final SimpleDoubleProperty offsetX = new SimpleDoubleProperty(0);
  private final SimpleDoubleProperty offsetY = new SimpleDoubleProperty(DEFAULT_CLOUD_HEIGHT);
  private final SimpleDoubleProperty offsetZ = new SimpleDoubleProperty(0);

  private final Material material = new TextureMaterial(new SolidColorTexture(new Vector4(1, 1, 1, 1)));

  public SimpleDoubleProperty scaleXProperty() {
    return scaleX;
  }

  public SimpleDoubleProperty scaleYProperty() {
    return scaleY;
  }

  public SimpleDoubleProperty scaleZProperty() {
    return scaleZ;
  }

  public SimpleDoubleProperty offsetXProperty() {
    return offsetX;
  }

  public SimpleDoubleProperty offsetYProperty() {
    return offsetY;
  }

  public SimpleDoubleProperty offsetZProperty() {
    return offsetZ;
  }

  @Override
  public boolean closestIntersection(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene,
      Random random) {
    Pair<Boolean, Boolean> intersection = cloudIntersection(ray, intersectionRecord, scene);
    boolean hit = intersection.thing1;
    boolean inCloud = intersection.thing2;
    if (hit) {
      if (inCloud) {
        intersectionRecord.n.scale(-1);
        intersectionRecord.shadeN.scale(-1);
        intersectionRecord.material = Air.INSTANCE;
        intersectionRecord.color.set(1, 1, 1, 0);
      } else {
        this.material.getColor(intersectionRecord);
        intersectionRecord.material = material;
      }
      return true;
    }
    return false;
  }

  /**
   * Test for a cloud intersection. If the ray intersects a cloud,
   * the distance to the intersection is stored in <code>ray.t</code>.
   * @param ray Ray with which to test for cloud intersection.
   * @return {@link se.llbit.util.Pair} of Booleans.
   * <code>pair.thing1</code>: <code>true</code> if the ray intersected a cloud.
   * <code>pair.thing2</code>: <code>true</code> if the ray origin is inside a cloud.
   */
  private Pair<Boolean, Boolean> cloudIntersection(Ray2 ray, IntersectionRecord intersectionRecord, Scene scene) {
    double ox = ray.o.x + scene.origin.x;
    double oy = ray.o.y + scene.origin.y;
    double oz = ray.o.z + scene.origin.z;
    double offsetX = this.offsetX.doubleValue();
    double offsetY = this.offsetY.doubleValue();
    double offsetZ = this.offsetZ.doubleValue();
    double invSizeX = 1 / this.scaleX.doubleValue();
    double invSizeZ = 1 / this.scaleZ.doubleValue();
    double cloudTop = offsetY + this.scaleY.doubleValue();
    int target = 1;
    double t_offset = 0;
    if (oy < offsetY || oy > cloudTop) {
      if (ray.d.y > 0) {
        t_offset = (offsetY - oy) / ray.d.y;
      } else {
        t_offset = (cloudTop - oy) / ray.d.y;
      }
      if (t_offset < 0) {
        return new Pair<>(false, false);
      }
      // Ray is entering cloud.
      if (inCloud((ray.d.x * t_offset + ox) * invSizeX + offsetX,
          (ray.d.z * t_offset + oz) * invSizeZ + offsetZ)) {
        intersectionRecord.setNormal(0, -Math.signum(ray.d.y), 0);
        intersectionRecord.distance = t_offset;
        return new Pair<>(true, false);
      }
    } else if (inCloud(ox * invSizeX + offsetX, oz * invSizeZ + offsetZ)) {
      target = 0;
    }
    double tExit;
    if (ray.d.y > 0) {
      tExit = (cloudTop - oy) / ray.d.y - t_offset;
    } else {
      tExit = (offsetY - oy) / ray.d.y - t_offset;
    }
    if (intersectionRecord.distance < tExit) {
      tExit = intersectionRecord.distance;
    }
    double x0 = (ox + ray.d.x * t_offset) * invSizeX + offsetX;
    double z0 = (oz + ray.d.z * t_offset) * invSizeZ + offsetZ;
    double xp = x0;
    double zp = z0;
    int ix = (int) Math.floor(xp);
    int iz = (int) Math.floor(zp);
    int xmod = (int) Math.signum(ray.d.x), zmod = (int) Math.signum(ray.d.z);
    int xo = (1 + xmod) / 2, zo = (1 + zmod) / 2;
    double dx = Math.abs(ray.d.x) * invSizeX;
    double dz = Math.abs(ray.d.z) * invSizeZ;
    double t = 0;
    int i = 0;
    int nx = 0, nz = 0;
    if (dx > dz) {
      double m = dz / dx;
      double xrem = xmod * (ix + xo - xp);
      double zlimit = xrem * m;
      while (t < tExit) {
        double zrem = zmod * (iz + zo - zp);
        if (zrem < zlimit) {
          iz += zmod;
          if (Clouds.getCloud(ix, iz) == target) {
            t = i / dx + zrem / dz;
            nx = 0;
            nz = -zmod;
            break;
          }
          ix += xmod;
          if (Clouds.getCloud(ix, iz) == target) {
            t = (i + xrem) / dx;
            nx = -xmod;
            nz = 0;
            break;
          }
        } else {
          ix += xmod;
          if (Clouds.getCloud(ix, iz) == target) {
            t = (i + xrem) / dx;
            nx = -xmod;
            nz = 0;
            break;
          }
          if (zrem <= m) {
            iz += zmod;
            if (Clouds.getCloud(ix, iz) == target) {
              t = i / dx + zrem / dz;
              nx = 0;
              nz = -zmod;
              break;
            }
          }
        }
        t = i / dx;
        i += 1;
        zp = z0 + zmod * i * m;
      }
    } else {
      double m = dx / dz;
      double zrem = zmod * (iz + zo - zp);
      double xlimit = zrem * m;
      while (t < tExit) {
        double xrem = xmod * (ix + xo - xp);
        if (xrem < xlimit) {
          ix += xmod;
          if (Clouds.getCloud(ix, iz) == target) {
            t = i / dz + xrem / dx;
            nx = -xmod;
            nz = 0;
            break;
          }
          iz += zmod;
          if (Clouds.getCloud(ix, iz) == target) {
            t = (i + zrem) / dz;
            nx = 0;
            nz = -zmod;
            break;
          }
        } else {
          iz += zmod;
          if (Clouds.getCloud(ix, iz) == target) {
            t = (i + zrem) / dz;
            nx = 0;
            nz = -zmod;
            break;
          }
          if (xrem <= m) {
            ix += xmod;
            if (Clouds.getCloud(ix, iz) == target) {
              t = i / dz + xrem / dx;
              nx = -xmod;
              nz = 0;
              break;
            }
          }
        }
        t = i / dz;
        i += 1;
        xp = x0 + xmod * i * m;
      }
    }
    int ny = 0;
    if (target == 1) {
      if (t > tExit) {
        return new Pair<>(false, false);
      }
      if (nx == 0 && ny == 0 && nz == 0) {
        // fix ray.n being set to zero (issue #643)
        return new Pair<>(false, false);
      }
      intersectionRecord.setNormal(nx, ny, nz);
      intersectionRecord.distance = t + t_offset;
      return new Pair<>(true, false);
    } else {
      if (t > tExit) {
        nx = 0;
        ny = (int) Math.signum(ray.d.y);
        nz = 0;
        t = tExit;
      } else {
        nx = -nx;
        nz = -nz;
      }
      if (nx == 0 && ny == 0 && nz == 0) {
        // fix ray.n being set to zero (issue #643)
        return new Pair<>(false, false);
      }
      intersectionRecord.setNormal(nx, ny, nz);
      intersectionRecord.distance = t;
      return new Pair<>(true, true);
    }
  }

  private static boolean inCloud(double x, double z) {
    return Clouds.getCloud((int) Math.floor(x), (int) Math.floor(z)) == 1;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.add("scaleX", scaleX.doubleValue());
    json.add("scaleY", scaleY.doubleValue());
    json.add("scaleZ", scaleZ.doubleValue());
    json.add("offsetX", offsetX.doubleValue());
    json.add("offsetY", offsetY.doubleValue());
    json.add("offsetZ", offsetZ.doubleValue());
    json.add("materialProperties", material.saveMaterialProperties());
    return json;
  }

  public void fromJson(JsonObject json) {
    scaleX.set(json.get("scaleX").doubleValue(DEFAULT_CLOUD_SIZE));
    scaleY.set(json.get("scaleY").doubleValue(DEFAULT_CLOUD_THICKNESS));
    scaleZ.set(json.get("scaleZ").doubleValue(DEFAULT_CLOUD_SIZE));
    offsetX.set(json.get("offsetX").doubleValue(0));
    offsetY.set(json.get("offsetY").doubleValue(DEFAULT_CLOUD_HEIGHT));
    offsetZ.set(json.get("offsetZ").doubleValue(0));
    this.material.loadMaterialProperties(json.get("materialProperties").asObject());
  }

  @Override
  public void reset() {
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();

    GridPane settings = new GridPane();

    ColumnConstraints columnConstraints = new ColumnConstraints();
    columnConstraints.setPercentWidth(100d / 3);

    settings.getColumnConstraints().addAll(columnConstraints, columnConstraints);
    settings.setHgap(10);
    settings.setVgap(10);

    DoubleTextField scaleX = new DoubleTextField();
    DoubleTextField scaleY = new DoubleTextField();
    DoubleTextField scaleZ = new DoubleTextField();

    scaleX.setTooltip(new Tooltip("Scale of the X-dimension of the clouds, measured in blocks per pixel of clouds.png texture"));
    scaleY.setTooltip(new Tooltip("Scale of the Y-dimension of the clouds, measured in blocks per pixel of clouds.png texture"));
    scaleZ.setTooltip(new Tooltip("Scale of the Z-dimension of the clouds, measured in blocks per pixel of clouds.png texture"));

    scaleX.valueProperty().setValue(this.scaleX.doubleValue());
    scaleY.valueProperty().setValue(this.scaleY.doubleValue());
    scaleZ.valueProperty().setValue(this.scaleZ.doubleValue());

    scaleX.valueProperty().addListener((observable, oldValue, newValue) -> {
      this.scaleX.set(newValue.doubleValue());
      scene.refresh();
    });
    scaleY.valueProperty().addListener((observable, oldValue, newValue) -> {
      this.scaleY.set(newValue.doubleValue());
      scene.refresh();
    });
    scaleZ.valueProperty().addListener((observable, oldValue, newValue) -> {
      this.scaleZ.set(oldValue.doubleValue());
      scene.refresh();
    });


    DoubleTextField offsetX = new DoubleTextField();
    DoubleTextField offsetY = new DoubleTextField();
    DoubleTextField offsetZ = new DoubleTextField();

    offsetX.valueProperty().setValue(this.offsetX.doubleValue());
    offsetY.valueProperty().setValue(this.offsetY.doubleValue());
    offsetZ.valueProperty().setValue(this.offsetZ.doubleValue());

    offsetX.valueProperty().addListener((observable, oldValue, newValue) -> {
      this.offsetX.set(newValue.doubleValue());
      scene.refresh();
    });
    offsetY.valueProperty().addListener((observable, oldValue, newValue) -> {
      this.offsetY.set(newValue.doubleValue());
      scene.refresh();
    });
    offsetZ.valueProperty().addListener((observable, oldValue, newValue) -> {
      this.offsetZ.set(newValue.doubleValue());
      scene.refresh();
    });

    TextFieldLabelWrapper x1Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper y1Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper z1Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper x2Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper y2Text = new TextFieldLabelWrapper();
    TextFieldLabelWrapper z2Text = new TextFieldLabelWrapper();

    x1Text.setTextField(scaleX);
    y1Text.setTextField(scaleY);
    z1Text.setTextField(scaleZ);
    x2Text.setTextField(offsetX);
    y2Text.setTextField(offsetY);
    z2Text.setTextField(offsetZ);

    x1Text.setLabelText("x:");
    y1Text.setLabelText("y:");
    z1Text.setLabelText("z:");
    x2Text.setLabelText("x:");
    y2Text.setLabelText("y:");
    z2Text.setLabelText("z:");

    Button editMaterialButton = new Button("Edit material");
    editMaterialButton.setOnAction(e -> {
      EditMaterialDialog dialog = new EditMaterialDialog(this.material, scene);
      dialog.showAndWait();
      dialog = null;
    });

    ColumnConstraints labelConstraints = new ColumnConstraints();
    labelConstraints.setHgrow(Priority.NEVER);
    labelConstraints.setPrefWidth(90);
    ColumnConstraints posFieldConstraints = new ColumnConstraints();
    posFieldConstraints.setMinWidth(20);
    posFieldConstraints.setPrefWidth(90);

    GridPane scaleAndOffsetPane = new GridPane();
    scaleAndOffsetPane.getColumnConstraints().addAll(labelConstraints, posFieldConstraints, posFieldConstraints, posFieldConstraints);
    scaleAndOffsetPane.setVgap(6);
    scaleAndOffsetPane.setHgap(6);

    scaleAndOffsetPane.addRow(0, new Label("Scale"), x1Text, y1Text, z1Text);
    scaleAndOffsetPane.addRow(1, new Label("Offset"), x2Text, y2Text, z2Text);

    return new VBox(
        6,
        scaleAndOffsetPane,
        editMaterialButton
    );
  }
}