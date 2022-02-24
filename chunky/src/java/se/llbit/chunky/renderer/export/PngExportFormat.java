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
        writePanoramaMetaData(scene, writer);
      }
      }
    }

  private static final String PNG_PANORAMA_META_ADOBE_RDF_XML =
    "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>\n" +
    " <rdf:Description rdf:about=''\n" +
    "   xmlns:GPano='http://ns.google.com/photos/1.0/panorama/'>\n" +
    "  <GPano:CroppedAreaImageHeightPixels>%d</GPano:CroppedAreaImageHeightPixels>\n" +
    "  <GPano:CroppedAreaImageWidthPixels>%d</GPano:CroppedAreaImageWidthPixels>\n" +
    "  <GPano:CroppedAreaLeftPixels>0</GPano:CroppedAreaLeftPixels>\n" +
    "  <GPano:CroppedAreaTopPixels>0</GPano:CroppedAreaTopPixels>\n" +
    "  <GPano:FullPanoHeightPixels>%d</GPano:FullPanoHeightPixels>\n" +
    "  <GPano:FullPanoWidthPixels>%d</GPano:FullPanoWidthPixels>\n" +
    "  <GPano:ProjectionType>equirectangular</GPano:ProjectionType>\n" +
    "  <GPano:UsePanoramaViewer>True</GPano:UsePanoramaViewer>\n" +
    " </rdf:Description>\n" +
    "</rdf:RDF>";

  private void writePanoramaMetaData(Scene scene, PngFileWriter writer) throws IOException {
    writer.writeChunk(new ITXT(
      "XML:com.adobe.xmp",
      String.format(
        PNG_PANORAMA_META_ADOBE_RDF_XML,
        scene.canvasHeight(),
        scene.canvasWidth(),
        scene.canvasHeight(),
        scene.canvasWidth()
      )
    ));
  }
}
