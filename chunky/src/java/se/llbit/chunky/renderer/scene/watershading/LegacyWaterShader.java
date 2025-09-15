/* Copyright (c) 2012-2021 Chunky contributors
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
package se.llbit.chunky.renderer.scene.watershading;

import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.Texture;
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.json.JsonObject;
import se.llbit.math.*;

public class LegacyWaterShader implements WaterShader {
  private static final float[] normalMap;
  private static final int normalMapW;

  private final Vector2 offset = new Vector2();
  private final Vector2 scale = new Vector2(1, 1);

  static {
    // precompute normal map
    Texture waterHeight = new Texture("water-height");
    normalMapW = waterHeight.getWidth();
    normalMap = new float[normalMapW*normalMapW*2];
    for (int u = 0; u < normalMapW; ++u) {
      for (int v = 0; v < normalMapW; ++v) {

        float hx0 = (waterHeight.getColorWrapped(u, v) & 0xFF) / 255.f;
        float hx1 = (waterHeight.getColorWrapped(u + 1, v) & 0xFF) / 255.f;
        float hz0 = (waterHeight.getColorWrapped(u, v) & 0xFF) / 255.f;
        float hz1 = (waterHeight.getColorWrapped(u, v + 1) & 0xFF) / 255.f;
        normalMap[(u*normalMapW + v) * 2] = hx1 - hx0;
        normalMap[(u*normalMapW + v) * 2 + 1] = hz1 - hz0;
      }
    }
  }

  /**
   * Displace the normal using the water displacement map.
   */
  @Override
  public Vector3 doWaterShading(Ray ray, IntersectionRecord intersectionRecord, double animationTime) {
    int w = (1 << 4);
    double ox = (ray.o.x + offset.x) / scale.x;
    double oz = (ray.o.z + offset.y) / scale.y;
    double x = ox / w - QuickMath.floor(ox / w);
    double z = oz / w - QuickMath.floor(oz / w);
    int u = (int) (x * normalMapW - Constants.EPSILON);
    int v = (int) ((1 - z) * normalMapW - Constants.EPSILON);
    Vector3 n = new Vector3(normalMap[(u*normalMapW + v) * 2], .15f, normalMap[(u*normalMapW + v) * 2 + 1]);
    w = (1 << 1);
    x = ox / w - QuickMath.floor(ox / w);
    z = oz / w - QuickMath.floor(oz / w);
    u = (int) (x * normalMapW - Constants.EPSILON);
    v = (int) ((1 - z) * normalMapW - Constants.EPSILON);
    n.x += normalMap[(u*normalMapW + v) * 2] / 2;
    n.z += normalMap[(u*normalMapW + v) * 2 + 1] / 2;
    n.normalize();
    return n;
  }

  @Override
  public WaterShader clone() {
    return new LegacyWaterShader();
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.add("offset", offset.toJson());
    json.add("scale", scale.toJson());
    return json;
  }

  @Override
  public void fromJson(JsonObject json) {
    offset.fromJson(json.get("offset").asObject());
    scale.fromJson(json.get("scale").asObject());
  }

  @Override
  public void reset() {
    this.scale.set(1, 1);
    this.offset.set(0, 0);
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();

    DoubleAdjuster xScale = new DoubleAdjuster();
    xScale.setName("X scale");
    xScale.setRange(0.001, 64);
    xScale.clampMin();
    xScale.set(this.scale.x);
    xScale.onValueChange(value -> {
      this.scale.x = value;
      scene.refresh();
    });

    DoubleAdjuster zScale = new DoubleAdjuster();
    zScale.setName("Z scale");
    zScale.setRange(0.001, 64);
    zScale.clampMin();
    zScale.set(this.scale.y);
    zScale.onValueChange(value -> {
      this.scale.y = value;
      scene.refresh();
    });

    DoubleAdjuster xOffset = new DoubleAdjuster();
    xOffset.setName("X offset");
    xOffset.setRange(-128, 128);
    xOffset.set(this.offset.x);
    xOffset.onValueChange(value -> {
      this.offset.x = value;
      scene.refresh();
    });

    DoubleAdjuster zOffset = new DoubleAdjuster();
    zOffset.setName("Z offset");
    zOffset.setRange(-128, 128);
    zOffset.set(this.offset.y);
    zOffset.onValueChange(value -> {
      this.offset.y = value;
      scene.refresh();
    });

    return new VBox(6, xScale, zScale, xOffset, zOffset);
  }
}
