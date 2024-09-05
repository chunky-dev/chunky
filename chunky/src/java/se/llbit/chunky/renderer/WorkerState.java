/* Copyright (c) 2014 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer;

import se.llbit.math.IntersectionRecord;
import se.llbit.math.Ray2;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Random;

/**
 * State for a render worker.
 */
public class WorkerState {
  public Ray2 ray = new Ray2();
  public IntersectionRecord intersectionRecord = new IntersectionRecord();
  public Ray2 sampleRay = new Ray2();
  public IntersectionRecord sampleRecord = new IntersectionRecord();
  public Vector3 throughput = new Vector3(1);
  public Vector4 color = new Vector4();
  public Vector3 emittance = new Vector3();
  public Vector4 sampleColor = new Vector4();
  public Vector3 attenuation = new Vector3();
  public Random random;
}
