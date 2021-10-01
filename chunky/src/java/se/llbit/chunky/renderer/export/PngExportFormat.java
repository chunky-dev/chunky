package se.llbit.chunky.renderer.export;

import java.io.IOException;
import java.io.OutputStream;
import se.llbit.chunky.renderer.projection.ProjectionMode;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.imageformats.png.ITXT;
import se.llbit.imageformats.png.PngFileWriter;
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
      if (scene.transparentSky()) {
        writer.write(backBuffer.data, scene.getAlphaChannel(), scene.canvasWidth(),
            scene.canvasHeight(), task);
      } else {
        writer.write(backBuffer.data, scene.canvasWidth(), scene.canvasHeight(), task);
      }
      if (scene.camera().getProjectionMode() == ProjectionMode.PANORAMIC
          && scene.camera().getFov() >= 179
          && scene.camera().getFov() <= 181) {
        String xmp = "";
        xmp += "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>\n";
        xmp += " <rdf:Description rdf:about=''\n";
        xmp += "   xmlns:GPano='http://ns.google.com/photos/1.0/panorama/'>\n";
        xmp += " <GPano:CroppedAreaImageHeightPixels>";
        xmp += scene.canvasHeight();
        xmp += "</GPano:CroppedAreaImageHeightPixels>\n";
        xmp += " <GPano:CroppedAreaImageWidthPixels>";
        xmp += scene.canvasWidth();
        xmp += "</GPano:CroppedAreaImageWidthPixels>\n";
        xmp += " <GPano:CroppedAreaLeftPixels>0</GPano:CroppedAreaLeftPixels>\n";
        xmp += " <GPano:CroppedAreaTopPixels>0</GPano:CroppedAreaTopPixels>\n";
        xmp += " <GPano:FullPanoHeightPixels>";
        xmp += scene.canvasHeight();
        xmp += "</GPano:FullPanoHeightPixels>\n";
        xmp += " <GPano:FullPanoWidthPixels>";
        xmp += scene.canvasWidth();
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
