/* Copyright (c) 2012 Jesper Öqvist <jesper@llbit.se>
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
package se.llbit.chunky.renderer.cl;
import org.apache.commons.math3.util.FastMath;

import org.jocl.cl_device_id;

/**
 * OpenCL device information.
 * @author Jesper Öqvist <jesper@llbit.se>
 */
@SuppressWarnings("javadoc")
public class CLDevice {

	public cl_device_id id;
	public String type;
	public String name;
	public int computeUnits;
	public int workGroupSize;

	public CLDevice(cl_device_id deviceId,
			String type, String name, int computeUnits,
			int workGroupSize) {

		this.id = deviceId;
		this.type = type;
		this.name = name.trim();
		this.computeUnits = computeUnits;
		this.workGroupSize = workGroupSize;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getInfoString() {
		return String.format(
				"<html>Device type: %s<br>" +
				"Compute units: %d<br>" +
				"Max work group size: %d",
				type,
				computeUnits,
				workGroupSize);
	}
}
