package se.llbit.chunky.renderer.scene.sky.celestialbodies;

import se.llbit.chunky.renderer.SceneIOProvider;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.JsonObject;
import se.llbit.log.Log;
import se.llbit.resources.ImageLoader;

import java.io.File;
import java.io.IOException;

public class Custom extends CelestialBodyType {
  public static final String ID = "CUSTOM";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "Custom";
  }

  @Override
  public String getDescription() {
    return "Custom celestial body texture";
  }

  private final Texture texture = new Texture();
  private String fileName;

  public Custom() {
    texture.setTexture(Sun.texture);
  }

  @Override
  protected void importFromJson(JsonObject obj) {
    fileName = obj.get("customTextureFile").asString(null);
  }

  @Override
  public void appendToConfig(JsonObject obj) {
    super.appendToConfig(obj);
    obj.add("customTextureFile", fileName);
  }

  public void loadCustomTextures(SceneIOProvider ioContext) {
    if (fileName != null) {
      try {
        setFile(ioContext.resolveLinkedFile(fileName));
      } catch (IOException ex) {
        Log.error("Failed to find custom skymap file: " + fileName);
      }
    }
  }

  @Override
  public Texture getTexture() {
    return texture;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFile(File file) {
    try {
      texture.setTexture(ImageLoader.read(file));
    } catch (IOException ex) {
      Log.error("Failed to load custom skymap: " + file, ex);
    }
  }
}
