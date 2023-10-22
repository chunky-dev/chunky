package se.llbit.chunky.renderer.scene.sky;

import se.llbit.chunky.renderer.SceneIOProvider;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.JsonObject;
import se.llbit.log.Log;
import se.llbit.resources.ImageLoader;
import se.llbit.util.Registerable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// TODO: use https://openjdk.org/jeps/409 sealed classes
public abstract class CelestialBodyType implements Registerable {
  private static final String CONFIG_TYPE_KEY = "celestialBodyType";

  public static final Map<String, Supplier<CelestialBodyType>> TYPES = new HashMap<>(3);
  static {
    TYPES.put(Sun.ID, Sun::new);
    TYPES.put(Moon.ID, Moon::new);
    TYPES.put(Custom.ID, Custom::new);
  }

  static CelestialBodyType newFromJson(JsonObject obj) {
    String typeStr = obj.get(CONFIG_TYPE_KEY).asString(Sun.ID);
    Supplier<CelestialBodyType> create = TYPES.get(typeStr);
    if(create == null) {
      Log.warnf("Unknown celestial body type \"%s\"", typeStr);
      return new CelestialBodyType.Sun();
    }
    CelestialBodyType type = create.get();
    type.importFromJson(obj);
    return type;
  }

  protected void importFromJson(JsonObject obj) {}

  public void appendToConfig(JsonObject obj) {
    obj.add(CONFIG_TYPE_KEY, getId());
  }

  public void loadCustomTextures(SceneIOProvider ioContext) {
  }

  public abstract Texture getTexture();

  @Override
  public String getDescription() {
    return getName();
  }

  public static class Sun extends CelestialBodyType {
    public static final String ID = "SUN";
    @Override
    public String getId() {
      return ID;
    }

    @Override
    public String getName() {
      return "Sun";
    }

    public static final Texture texture = new Texture();

    @Override
    public Texture getTexture() {
      return texture;
    }
  }

  public static class Moon extends CelestialBodyType {
    public static final String ID = "MOON";
    @Override
    public String getId() {
      return ID;
    }

    @Override
    public String getName() {
      return "Moon";
    }

    public enum Phase {
      NEW_MOON(0, 1),
      WAXING_CRESCENT(1, 1),
      FIRST_QUARTER(2, 1),
      WAXING_GIBBOUS(3, 1),
      FULL_MOON(0, 0),
      WANING_GIBBOUS(1, 0),
      LAST_QUARTER(2, 0),
      WANING_CRESCENT(3, 0);

      final byte textureAtlasPosX, textureAtlasPosY;
      Phase(int x, int y) {
        textureAtlasPosX = (byte) x;
        textureAtlasPosY = (byte) y;
      }
    }

    public static final Texture textureAtlas = new Texture();
    private final Texture texture = new Texture();
    private Phase phase;

    public Moon() {
      setPhase(Phase.FULL_MOON);
    }

    @Override
    protected void importFromJson(JsonObject obj) {
      setPhase(Phase.valueOf(obj.get("moonPhase").asString(Phase.FULL_MOON.toString())));
    }

    @Override
    public void appendToConfig(JsonObject obj) {
      super.appendToConfig(obj);
      obj.add("moonPhase", phase.name());
    }

    @Override
    public Texture getTexture() {
      return texture;
    }

    public Phase getPhase() {
      return phase;
    }

    public void setPhase(Phase phase) {
      this.phase = phase;

      if(textureAtlas.getWidth() == textureAtlas.getHeight()) {
        // only has 1 phase (phases atlas not found, fallback to default moon texture)
        Log.info("Moon texture did not contain multiple phases");
        texture.setTexture(textureAtlas);
        return;
      }

      int textureSize = textureAtlas.getHeight() / 2;
      BitmapImage phaseTexture = new BitmapImage(textureSize, textureSize);
      phaseTexture.blit(
        textureAtlas.getBitmap(),
        0, 0,
        phase.textureAtlasPosX * textureSize,
        phase.textureAtlasPosY * textureSize,
        (phase.textureAtlasPosX+1) * textureSize,
        (phase.textureAtlasPosY+1) * textureSize
      );
      texture.setTexture(phaseTexture);
    }
  }

  public static class Custom extends CelestialBodyType {
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
      if(fileName != null) {
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
}
