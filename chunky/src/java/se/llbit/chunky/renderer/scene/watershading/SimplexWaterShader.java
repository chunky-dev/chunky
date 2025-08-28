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
import se.llbit.chunky.ui.DoubleAdjuster;
import se.llbit.chunky.ui.IntegerAdjuster;
import se.llbit.chunky.ui.render.RenderControlsTab;
import se.llbit.json.JsonObject;
import se.llbit.math.*;

public class SimplexWaterShader implements WaterShader {
  /*
  Water shading is implemented using fractal noise based on simplex noise
  (superimposed layers of simplex noise with increasing frequency and decreasing amplitude)
  This 3D noise function gives the height of the water for a given x, z and t,
  what we are really interested in are the partial derivatives of that function
  with respect to x and z as they give us the slope along x and z,
  and the normal is simply the cross product of the slope along x and the slope along z.
   */

  public int iterations = 4; /// Number of iteration of the fractal noise
  public double baseFrequency = 0.4; /// frequency of the first iteration, doubles each iteration
  public double baseAmplitude = 0.025; /// amplitude of the first iteration, halves each iteration
  public double animationSpeed = 1; /// animation speed
  private final Vector2 offset = new Vector2();
  private final Vector2 scale = new Vector2(1, 1);
  private final SimplexNoise noise = new SimplexNoise();


  @Override
  public Vector3 doWaterShading(Ray2 ray, IntersectionRecord intersectionRecord, double animationTime) {
    double frequency = baseFrequency;
    double amplitude = baseAmplitude;

    double ddx = 0;
    double ddz = 0;

    for(int i = 0; i < iterations; ++i) {
      noise.calculate((float)(ray.o.x * frequency), (float)(ray.o.z * frequency), (float)(animationTime * animationSpeed));
      noise.calculate(
        (float) ((ray.o.x + offset.x) / scale.x * frequency),
        (float) ((ray.o.z + offset.y) / scale.y * frequency),
        (float) (animationTime * animationSpeed)
      );
      double ddxNext = ddx - amplitude * noise.ddx;
      double ddzNext = ddz - amplitude * noise.ddy;
      if (Double.isNaN(ddxNext + ddzNext)) {
        break;
      }
      ddx = ddxNext;
      ddz = ddzNext;

      frequency *= 2;
      amplitude *= 0.5;
    }
    Vector3 xSlope = new Vector3(1, ddx, 0);
    Vector3 zSlope = new Vector3(0, ddz, 1);
    Vector3 normal = new Vector3();
    normal.cross(zSlope, xSlope);
    normal.normalize();
    return normal;
  }

  @Override
  public WaterShader clone() {
    SimplexWaterShader shader = new SimplexWaterShader();
    shader.iterations = iterations;
    shader.baseFrequency = baseFrequency;
    shader.baseAmplitude = baseAmplitude;
    shader.animationSpeed = animationSpeed;
    shader.offset.set(offset);
    shader.scale.set(scale);
    return shader;
  }

  @Override
  public JsonObject toJson() {
    JsonObject params = new JsonObject();
    params.add("iterations", iterations);
    params.add("frequency", baseFrequency);
    params.add("amplitude", baseAmplitude);
    params.add("animationSpeed", animationSpeed);
    params.add("offset", offset.toJson());
    params.add("scale", scale.toJson());
    return params;
  }

  @Override
  public void fromJson(JsonObject json) {
    iterations = json.get("iterations").intValue(4);
    baseFrequency = json.get("frequency").doubleValue(0.4);
    baseAmplitude = json.get("amplitude").doubleValue(0.025);
    animationSpeed = json.get("animationSpeed").doubleValue(1);
    offset.fromJson(json.get("offset").asObject());
    scale.fromJson(json.get("scale").asObject());
  }

  @Override
  public void reset() {
    iterations = 4;
    baseAmplitude = 0.025;
    baseFrequency = 0.4;
    animationSpeed = 1;
    offset.set(0, 0);
    scale.set(1, 1);
  }

  @Override
  public VBox getControls(RenderControlsTab parent) {
    Scene scene = parent.getChunkyScene();

    IntegerAdjuster iterations = new IntegerAdjuster();
    iterations.setName("Iterations");
    iterations.setRange(1, 10);
    iterations.clampMin();
    iterations.set(this.iterations);
    iterations.onValueChange(value -> {
      this.iterations = value;
      scene.refresh();
    });

    DoubleAdjuster frequency = new DoubleAdjuster();
    frequency.setName("Frequency");
    frequency.setRange(0, 1);
    frequency.set(this.baseFrequency);
    frequency.onValueChange(value -> {
      this.baseFrequency = value;
      scene.refresh();
    });

    DoubleAdjuster amplitude = new DoubleAdjuster();
    amplitude.setName("Amplitude");
    amplitude.setRange(0, 1);
    amplitude.set(this.baseAmplitude);
    amplitude.onValueChange(value -> {
      this.baseAmplitude = value;
      scene.refresh();
    });

    DoubleAdjuster animationSpeed = new DoubleAdjuster();
    animationSpeed.setName("Animation speed");
    animationSpeed.setRange(0, 10);
    animationSpeed.set(this.animationSpeed);
    animationSpeed.onValueChange(value -> {
      this.animationSpeed = value;
      scene.refresh();
    });

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

    return new VBox(6, iterations, frequency, amplitude, animationSpeed, xScale, zScale, xOffset, zOffset);
  }
}
