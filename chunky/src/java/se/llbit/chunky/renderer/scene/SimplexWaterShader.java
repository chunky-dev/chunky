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
package se.llbit.chunky.renderer.scene;

import se.llbit.json.JsonObject;
import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray;
import se.llbit.math.Ray2;
import se.llbit.math.SimplexNoise;
import se.llbit.math.Vector3;

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
  private final SimplexNoise noise = new SimplexNoise();


  @Override
  public Vector3 doWaterShading(Ray2 ray, IntersectionRecord intersectionRecord, double animationTime) {
    double frequency = baseFrequency;
    double amplitude = baseAmplitude;

    double ddx = 0;
    double ddz = 0;

    for(int i = 0; i < iterations; ++i) {
      noise.calculate((float)(ray.o.x * frequency), (float)(ray.o.z * frequency), (float)(animationTime * animationSpeed));
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
    Vector3 xslope = new Vector3(1, ddx, 0);
    Vector3 zslope = new Vector3(0, ddz, 1);
    Vector3 normal = new Vector3();
    normal.cross(zslope, xslope);
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
    return shader;
  }

  @Override
  public void save(JsonObject json) {
    JsonObject params = new JsonObject();
    params.add("iterations", iterations);
    params.add("frequency", baseFrequency);
    params.add("amplitude", baseAmplitude);
    params.add("animationSpeed", animationSpeed);
    json.add("simplexWaterShader", params);
  }

  @Override
  public void load(JsonObject json) {
    JsonObject params = json.get("simplexWaterShader").asObject();
    if(params == null)
      return;

    iterations = params.get("iterations").intValue(4);
    baseFrequency = params.get("frequency").doubleValue(0.4);
    baseAmplitude = params.get("amplitude").doubleValue(0.025);
    animationSpeed = params.get("animationSpeed").doubleValue(1);
  }
}
