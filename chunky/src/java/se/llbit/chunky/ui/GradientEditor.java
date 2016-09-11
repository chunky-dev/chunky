/* Copyright (c) 2014-2016 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.ui;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import se.llbit.chunky.renderer.scene.Sky;
import se.llbit.chunky.ui.render.SkyTab;
import se.llbit.json.JsonParser;
import se.llbit.math.ColorUtil;
import se.llbit.math.Constants;
import se.llbit.math.Vector4;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * A control for editing the sky color gradient.
 * The edited gradient does not use linear blending.
 */
public class GradientEditor extends VBox implements Initializable {
  private static final WritablePixelFormat<IntBuffer> PIXEL_FORMAT =
      PixelFormat.getIntArgbInstance();
  private final SkyTab sky;

  private String[][] presets = {
      {"Clear", "[{\"rgb\":\"0BABC7\",\"pos\":0.0},{\"rgb\":\"75AAFF\",\"pos\":1.0}]"},
      {"Desert", "[{\"rgb\":\"FF9966\",\"pos\":0.0},{\"rgb\":\"FFB77D\",\"pos\":0.19811320754716982},{\"rgb\":\"FFFFB3\",\"pos\":0.3867924528301887},{\"rgb\":\"D5ECEE\",\"pos\":0.7358490566037735},{\"rgb\":\"EBFCFD\",\"pos\":1.0}]"},
      {"The End", "[{\"rgb\":\"2F1234\",\"pos\":0.0},{\"rgb\":\"321237\",\"pos\":0.42924528301886794},{\"rgb\":\"110713\",\"pos\":0.6320754716981132},{\"rgb\":\"000000\",\"pos\":1.0}]"},
      {"Mountain", "[{\"rgb\":\"718A83\",\"pos\":0.0},{\"rgb\":\"E7E8E8\",\"pos\":0.41745283018867924},{\"rgb\":\"BBF1F4\",\"pos\":0.5801886792452831},{\"rgb\":\"72F0F7\",\"pos\":0.7735849056603774},{\"rgb\":\"58F0F9\",\"pos\":1.0}]"},
      {"The Nether", "[{\"rgb\":\"000000\",\"pos\":0.0},{\"rgb\":\"000000\",\"pos\":0.20047169811320756},{\"rgb\":\"B31A1A\",\"pos\":0.7240566037735849},{\"rgb\":\"B3281A\",\"pos\":0.8655660377358491},{\"rgb\":\"B3341A\",\"pos\":1.0}]"},
      {"Overcast", "[{\"rgb\":\"A5BECA\",\"pos\":0.0},{\"rgb\":\"BED6DD\",\"pos\":0.5259433962264151},{\"rgb\":\"D2E9EB\",\"pos\":0.7358490566037735},{\"rgb\":\"E3F3F4\",\"pos\":1.0}]"},
  };

  @FXML private MenuButton loadPreset;
  @FXML private Button prevBtn;
  @FXML private Button nextBtn;
  @FXML private Button removeBtn;
  @FXML private Button addBtn;
  @FXML private Button importBtn;
  @FXML private Button exportBtn;
  @FXML private Canvas canvas;
  @FXML private SimpleColorPicker colorPicker;

  private List<Vector4> gradient;
  int selected = 0;
  private ChangeListener<? super Color> colorListener = (observable, oldValue, newValue) -> {
    Vector4 stop = gradient.get(selected);
    stop.x = newValue.getRed();
    stop.y = newValue.getGreen();
    stop.z = newValue.getBlue();
    gradientChanged();
  };

  public GradientEditor(SkyTab sky) throws IOException {
    this.sky = sky;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("GradientEditor.fxml"));
    loader.setClassLoader(getClass()
        .getClassLoader()); // Needed for Java 1.8u40 where FXMLLoader has a null class loader for some reason.
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
  }

  @Override public void initialize(URL location, ResourceBundle resources) {
    for (String[] preset : presets) {
      MenuItem menuItem = new MenuItem(preset[0]);
      menuItem.setOnAction(e -> importGradient(preset[1]));
      loadPreset.getItems().add(menuItem);
    }
    prevBtn.setTooltip(new Tooltip("Select the previous stop."));
    prevBtn.setOnAction(e -> {
      selectStop(Math.max(selected - 1, 0));
      draw();
    });
    nextBtn.setTooltip(new Tooltip("Select the next stop."));
    nextBtn.setOnAction(e -> {
      selectStop(Math.min(selected + 1, gradient.size() - 1));
      draw();
    });
    addBtn.setTooltip(new Tooltip("Add a new stop after the selected stop."));
    addBtn.setOnAction(e -> {
      selectStop(addStopAfter(selected));
      gradientChanged();
    });
    removeBtn.setTooltip(new Tooltip("Delete the selected stop."));
    removeBtn.setDisable(true);
    removeBtn.setOnAction(e -> {
      if (removeStop(selected)) {
        selectStop(Math.min(selected, gradient.size() - 1));
        nextBtn.setDisable(selected == gradient.size() - 1);
        gradientChanged();
      }
    });
    importBtn.setOnAction(e -> {
      javafx.scene.control.TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle("Import Gradient");
      dialog.setHeaderText("Gradient Import");
      dialog.setContentText("Graident JSON:");
      Optional<String> result = dialog.showAndWait();
      if (result.isPresent()) {
        importGradient(result.get());
      }
    });
    exportBtn.setOnAction(e -> {
      javafx.scene.control.TextInputDialog dialog =
          new TextInputDialog(Sky.gradientJson(gradient).toCompactString());
      dialog.setTitle("Gradient Export");
      dialog.setHeaderText("Gradient Export");
      dialog.setContentText("Gradient JSON:");
      dialog.showAndWait();
    });
    canvas.setOnMouseDragged(e -> {
      double pos = e.getX() / canvas.getWidth();
      if (selected > 0 && selected + 1 < gradient.size()) {
        pos = Math.max(gradient.get(selected - 1).w, pos);
        pos = Math.min(gradient.get(selected + 1).w, pos);
        gradient.get(selected).w = pos;
        gradientChanged();
      }
    });
    canvas.setOnMousePressed(e -> {
      // Select closest stop.
      double pos = e.getX() / canvas.getWidth();
      double closest = Double.MAX_VALUE;
      int stop = 0;
      int index = 0;
      for (Vector4 m : gradient) {
        double distance = Math.abs(m.w - pos);
        if (distance < closest) {
          stop = index;
          closest = distance;
        }
        index += 1;
      }
      selectStop(stop);
      draw();
    });
    canvas.setOnMouseClicked(e -> {
      if (e.getClickCount() == 2) {
        // Add new stop after the last stop before click position.
        double pos = e.getX() / canvas.getWidth();
        int stop = 0;
        int index = 0;
        for (Vector4 m : gradient) {
          if (pos >= m.w) {
            stop = index;
          } else {
            break;
          }
          index += 1;
        }
        int added = addStopAfter(stop, pos);
        gradient.get(added).w = pos;
        selectStop(added);
        gradientChanged();
      }
    });
    colorPicker.colorProperty().addListener(colorListener);
  }

  private void importGradient(String data) {
    JsonParser parser = new JsonParser(new ByteArrayInputStream(data.getBytes()));
    try {
      List<Vector4> newGradient = Sky.gradientFromJson(parser.parse().array());
      if (newGradient != null) {
        setGradientNoUpdate(newGradient);
      }
    } catch (IOException e1) {
    } catch (JsonParser.SyntaxError e2) {
    }
  }

  private Color toFxColor(Vector4 color) {
    return new Color(color.x, color.y, color.z, 1);
  }

  private void gradientChanged() {
    removeBtn.setDisable(gradient.size() == 2);
    draw();
    sky.gradientChanged(gradient);
  }

  /**
   * @return the index of the added stop
   */
  private int addStopAfter(int stop) {
    int i0;
    int i1;
    if (stop == gradient.size() - 1) {
      i0 = stop - 1;
      i1 = stop;
    } else {
      i0 = stop;
      i1 = stop + 1;
    }
    gradient.add(i1, blend(gradient.get(i0), gradient.get(i1), 0.5));
    nextBtn.setDisable(selected == gradient.size() - 1);
    return i1;
  }

  /**
   * @return the index of the added stop
   */
  private int addStopAfter(int stop, double pos) {
    int i0;
    int i1;
    if (stop == gradient.size() - 1) {
      i0 = stop - 1;
      i1 = stop;
    } else {
      i0 = stop;
      i1 = stop + 1;
    }
    Vector4 s0 = gradient.get(i0);
    Vector4 s1 = gradient.get(i1);
    gradient.add(i1, blend(s0, s1, (pos - s0.w) / (s1.w - s0.w)));
    nextBtn.setDisable(selected == gradient.size() - 1);
    return i1;
  }

  /**
   * @return {@code true} if a stop was deleted.
   */
  public boolean removeStop(int stop) {
    if (gradient.size() > 2) {
      gradient.remove(stop);
      if (stop == 0) {
        gradient.get(0).w = 0;
      } else if (stop == gradient.size()) {
        gradient.get(stop - 1).w = 1;
      }
      return true;
    }
    return false;
  }

  private Vector4 blend(Vector4 s0, Vector4 s1, double d) {
    double xx = 0.5 * (Math.sin(Math.PI * d - Constants.HALF_PI) + 1);
    double a = 1 - xx;
    double b = xx;
    return new Vector4(a * s0.x + b * s1.x, a * s0.y + b * s1.y, a * s0.z + b * s1.z,
        a * s0.w + b * s1.w);
  }

  private synchronized void selectStop(int stop) {
    if (stop != selected) {
      selected = stop;
      updateSelectedColor();
    }
  }

  private void updateSelectedColor() {
    colorPicker.colorProperty().removeListener(colorListener);
    colorPicker.setColor(toFxColor(gradient.get(selected)));
    colorPicker.colorProperty().addListener(colorListener);
    prevBtn.setDisable(selected == 0);
    nextBtn.setDisable(selected == gradient.size() - 1);
  }

  private void draw() {
    int width = (int) canvas.getWidth();
    int height = (int) canvas.getHeight();
    Image image = drawGradient(width, height, gradient);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.drawImage(image, 0, 0);
    gc.setFill(Color.WHITE);
    gc.fillRect(0, 0, width, 15);

    int min = 0, max = width;
    if (selected > 0) {
      min = (int) (gradient.get(selected - 1).w * width);
    }
    if (selected < gradient.size() - 1) {
      max = (int) (gradient.get(selected + 1).w * width);
    }
    gc.setFill(Color.LIGHTGRAY);
    gc.fillRect(min, 0, max - min, 15);
    int index = 0;
    for (Vector4 stop : gradient) {
      if (index == selected) {
        index += 1;
        continue;
      }

      gc.setFill(Color.WHITE);
      int x = Math.min(width - 1, (int) (stop.w * width));
      double[] xPoints = {x - 5, x, x + 5};
      double[] yPoints = {0, 15, 0};
      gc.fillPolygon(xPoints, yPoints, 3);

      boolean isEndpoint = index == 0 || index == gradient.size() - 1;
      if (isEndpoint) {
        gc.setStroke(Color.GRAY);
      } else {
        gc.setStroke(Color.BLACK);
      }
      gc.strokeLine(x - 5, 0, x, 15);
      gc.strokeLine(x, 15, x + 5, 0);
      gc.strokeLine(x - 5, 0, x + 5, 0);
      index += 1;
    }
    gc.setLineWidth(2);
    Vector4 stop = gradient.get(selected);
    boolean isEndpoint = selected == 0 || selected == gradient.size() - 1;
    if (isEndpoint) {
      gc.setFill(Color.GRAY);
    } else {
      gc.setFill(Color.BLACK);
    }
    int x = Math.min(width - 1, (int) (stop.w * width));
    double[] xPoints = {x - 5, x, x + 5};
    double[] yPoints = {0, 15, 0};
    gc.fillPolygon(xPoints, yPoints, 3);

    gc.setStroke(Color.WHITE);
    gc.strokeLine(x - 5, 0, x, 15);
    gc.strokeLine(x, 15, x + 5, 0);
    gc.strokeLine(x - 5, 0, x + 5, 0);
  }

  protected static Image drawGradient(int width, int height, List<Vector4> gradient) {
    int[] pixels = new int[width * height];
    if (width <= 0 || height <= 0 || gradient.size() < 2) {
      throw new IllegalArgumentException();
    }
    int x = 0;
    // Fill the first row.
    for (int i = 0; i < width; ++i) {
      double weight = i / (double) width;
      Vector4 c0 = gradient.get(x);
      Vector4 c1 = gradient.get(x + 1);
      double xx = (weight - c0.w) / (c1.w - c0.w);
      while (x + 2 < gradient.size() && xx > 1) {
        x += 1;
        c0 = gradient.get(x);
        c1 = gradient.get(x + 1);
        xx = (weight - c0.w) / (c1.w - c0.w);
      }
      xx = 0.5 * (Math.sin(Math.PI * xx - Constants.HALF_PI) + 1);
      double a = 1 - xx;
      double b = xx;
      int argb = ColorUtil
          .getArgb(a * c0.x + b * c1.x, a * c0.y + b * c1.y, a * c0.z + b * c1.z, 1);
      pixels[i] = argb;
    }
    // Copy top row to the rest of the image.
    for (int j = 1; j < height; ++j) {
      for (int i = 0; i < width; ++i) {
        pixels[j * width + i] = pixels[i];
      }
    }
    WritableImage image = new WritableImage(width, height);
    image.getPixelWriter().setPixels(0, 0, width, height, PIXEL_FORMAT, pixels, 0, width);
    return image;
  }

  private void setGradientNoUpdate(List<Vector4> gradient) {
    this.gradient = gradient;
    selected = Math.min(selected, gradient.size() - 1);
    updateSelectedColor();
    gradientChanged();
  }

  public void setGradient(List<Vector4> gradient) {
    this.gradient = gradient;
    selected = Math.min(selected, gradient.size() - 1);
    updateSelectedColor();
    draw();
  }
}
