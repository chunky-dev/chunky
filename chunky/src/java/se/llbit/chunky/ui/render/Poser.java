/*
 * Copyright (c) 2016-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky contributors
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.ui.render;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.PlayerModel;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.math.bvh.BVH;
import se.llbit.math.ColorUtil;
import se.llbit.math.Matrix3;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.ResourceBundle;

/**
 * A tool for posing entities.
 */
public class Poser extends Stage implements Initializable {
  private static final WritablePixelFormat<IntBuffer> PIXEL_FORMAT =
      PixelFormat.getIntArgbInstance();
  private final EntitiesTab.EntityData player;
  private BVH bvh = BVH.EMPTY;
  private int[] pixels;
  private int width = 300;
  private int height = 300;
  private Matrix3 transform = new Matrix3();
  private Vector3 camPos = new Vector3();
  private WritableImage image;

  @FXML private Canvas preview;
  @FXML private ChoiceBox<PlayerModel> playerModel;
  @FXML private TextField skin;
  @FXML private Button selectSkin;
  @FXML private DoubleAdjuster direction;
  @FXML private DoubleAdjuster headYaw;
  @FXML private DoubleAdjuster headPitch;
  @FXML private DoubleAdjuster leftArmPose;
  @FXML private DoubleAdjuster rightArmPose;
  @FXML private DoubleAdjuster leftLegPose;
  @FXML private DoubleAdjuster rightLegPose;
  private double lastX;
  private double lastY;

  public Poser(EntitiesTab.EntityData data) throws IOException {
    this.player = data;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("Poser.fxml"));
    loader.setController(this);
    Parent root = loader.load();
    setScene(new javafx.scene.Scene(root));
    setTitle("Pose Preview");
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    selectSkin.setOnAction(e -> {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Load Skin");
      fileChooser
          .getExtensionFilters().add(new FileChooser.ExtensionFilter("Minecraft skin", "*.png"));
      File skinFile = fileChooser.showOpenDialog(getScene().getWindow());
      if (skinFile != null) {
        // TODO
        //player.entity.setTexture(skinFile.getAbsolutePath());
        skin.setText(skinFile.getAbsolutePath());
        redraw();
      }
    });
    preview.setOnMousePressed(e -> {
      lastX = e.getX();
      lastY = e.getY();
    });
    preview.setOnMouseDragged(e -> {
      double dx = e.getX() - lastX;
      double dy = e.getY() - lastY;
      lastX = e.getX();
      lastY = e.getY();
      direction.setAndUpdate(direction.get() + dx / 20);
      headPitch.setAndUpdate(headPitch.get() - dy / 60);
    });
    // TODO
    // skin.setText(player.entity.skin);
    pixels = new int[width * height];
    transform.setIdentity();
    image = new WritableImage(width, height);
    playerModel.getItems().addAll(PlayerModel.values());
    // TODO
    //playerModel.getSelectionModel().select(player.entity.model);
    playerModel.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          // TODO
          //player.entity.model = newValue;
          redraw();
        });
    direction.setName("Direction");
    direction.setRange(-Math.PI, Math.PI);
    direction.onValueChange(value -> {
      // TODO
      //player.entity.pose.set("rotation", Json.of(value));
      redraw();
    });
    headYaw.setName("Head yaw");
    headYaw.setRange(-QuickMath.HALF_PI, QuickMath.HALF_PI);
    headYaw.onValueChange(value -> {
      // TODO
      //player.entity.headYaw = value;
      redraw();
    });
    headPitch.setName("Head pitch");
    headPitch.setRange(-QuickMath.HALF_PI, QuickMath.HALF_PI);
    headPitch.onValueChange(value -> {
      // TODO
      //player.entity.pitch = value;
      redraw();
    });
    leftArmPose.setName("Left arm pose");
    leftArmPose.setRange(-Math.PI, Math.PI);
    leftArmPose.onValueChange(value -> {
      // TODO
      //player.entity.leftArmPose = value;
      redraw();
    });
    rightArmPose.setName("Right arm pose");
    rightArmPose.setRange(-Math.PI, Math.PI);
    rightArmPose.onValueChange(value -> {
      // TODO
      //player.entity.rightArmPose = value;
      redraw();
    });
    leftLegPose.setName("Left leg pose");
    leftLegPose.setRange(-QuickMath.HALF_PI, QuickMath.HALF_PI);
    leftLegPose.onValueChange(value -> {
      // TODO
      //player.entity.leftLegPose = value;
      redraw();
    });
    rightLegPose.setName("Right leg pose");
    rightLegPose.setRange(-QuickMath.HALF_PI, QuickMath.HALF_PI);
    rightLegPose.onValueChange(value -> {
      // TODO
      //player.entity.rightLegPose = value;
      redraw();
    });
    redraw();
  }

  private void buildBvh() {
    Vector3 offset = new Vector3(); // Offset to place player in focus.
    // TODO
    //offset.sub(player.entity.position);
    //bvh = new BVH(new LinkedList<>(player.entity.primitives(offset)));
  }

  private void redraw() {
    buildBvh();
    GraphicsContext gc = preview.getGraphicsContext2D();
    Ray ray = new Ray();
    double aspect = width / (double) height;
    double fovTan = Camera.clampedFovTan(70);
    camPos.set(0, 1, -2);
    for (int y = 0; y < height; ++y) {
      double rayy = fovTan * (.5 - ((double) y) / height);
      for (int x = 0; x < width; ++x) {
        double rayx = fovTan * aspect * (.5 - ((double) x) / width);
        ray.setDefault();
        ray.t = Double.MAX_VALUE;
        ray.d.set(rayx, rayy, 1);
        ray.d.normalize();

        ray.o.set(camPos);
        while (true) {
          if (bvh.closestIntersection(ray)) {
            if (ray.color.w > 0.9) {
              break;
            }
            ray.o.scaleAdd(ray.t, ray.d);
          } else {
            if (x % 20 == 0 || y % 20 == 0) {
              ray.color.set(0.7, 0.7, 0.7, 1);
            } else {
              ray.color.set(1, 1, 1, 1);
            }
            break;
          }
        }

        ray.color.x = QuickMath.min(1, FastMath.sqrt(ray.color.x));
        ray.color.y = QuickMath.min(1, FastMath.sqrt(ray.color.y));
        ray.color.z = QuickMath.min(1, FastMath.sqrt(ray.color.z));
        pixels[y * width + x] = ColorUtil.getRGB(ray.color);
      }
    }
    image.getPixelWriter().setPixels(0, 0, width, height, PIXEL_FORMAT, pixels, 0, width);
    gc.drawImage(image, 0, 0);
  }
}
