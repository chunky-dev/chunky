/* Copyright (c) 2021 Chunky contributors
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
package se.llbit.chunky.renderer.export;

import java.io.IOException;
import java.io.OutputStream;
import se.llbit.chunky.renderer.projection.ProjectionMode;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.png.ITXT;
import se.llbit.png.PngFileWriter;
import se.llbit.util.TaskTracker;

/**
 * Standard PNG with 8-bit color channels.
 */
public class PngExportFormat implements PictureExportFormat {

  @Override
  public String getName() {
    return "PNG";
  }

  @Override
  public String getExtension() {
    return ".png";
  }

  @Override
  public boolean isTransparencySupported() {
    return true;
  }

  @Override
  public void write(OutputStream out, Scene scene, TaskTracker taskTracker) throws IOException {
    try (TaskTracker.Task task = taskTracker.task("Writing PNG");
        PngFileWriter writer = new PngFileWriter(out)) {
      BitmapImage backBuffer = scene.getBackBuffer();
      int width = scene.subareaWidth();
      int height = scene.subareaHeight();
      if (scene.transparentSky()) {
        writer.write(backBuffer, scene.getSampleBuffer(), width, height, task);
      } else {
        writer.write(backBuffer, width, height, task);
      }
      if (scene.camera().getProjectionMode() == ProjectionMode.PANORAMIC
          && scene.camera().getFov() >= 179
          && scene.camera().getFov() <= 181) {
        String xmp = "";
        xmp += "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>\n";
        xmp += " <rdf:Description rdf:about=''\n";
        xmp += "   xmlns:GPano='http://ns.google.com/photos/1.0/panorama/'>\n";
        xmp += " <GPano:CroppedAreaImageHeightPixels>";
        xmp += scene.renderHeight();
        xmp += "</GPano:CroppedAreaImageHeightPixels>\n";
        xmp += " <GPano:CroppedAreaImageWidthPixels>";
        xmp += scene.renderWidth();
        xmp += "</GPano:CroppedAreaImageWidthPixels>\n";
        xmp += " <GPano:CroppedAreaLeftPixels>0</GPano:CroppedAreaLeftPixels>\n";
        xmp += " <GPano:CroppedAreaTopPixels>0</GPano:CroppedAreaTopPixels>\n";
        xmp += " <GPano:FullPanoHeightPixels>";
        xmp += scene.renderHeight();
        xmp += "</GPano:FullPanoHeightPixels>\n";
        xmp += " <GPano:FullPanoWidthPixels>";
        xmp += scene.renderWidth();
        xmp += "</GPano:FullPanoWidthPixels>\n";
        xmp += " <GPano:ProjectionType>equirectangular</GPano:ProjectionType>\n";
        xmp += " <GPano:UsePanoramaViewer>True</GPano:UsePanoramaViewer>\n";
        xmp += " </rdf:Description>\n";
        xmp += " </rdf:RDF>";
        ITXT iTXt = new ITXT("XML:com.adobe.xmp", xmp);
        writer.writeChunk(iTXt);
      }
    }
  }
}
