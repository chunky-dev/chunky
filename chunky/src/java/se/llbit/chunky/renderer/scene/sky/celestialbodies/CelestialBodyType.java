package se.llbit.chunky.renderer.scene.sky.celestialbodies;

import se.llbit.chunky.renderer.SceneIOProvider;
import se.llbit.chunky.resources.Texture;
import se.llbit.json.JsonObject;
import se.llbit.log.Log;
import se.llbit.util.Registerable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class CelestialBodyType implements Registerable {
  private static final String CONFIG_TYPE_KEY = "celestialBodyType";

  public final static CelestialBodyType DEFAULT = new Sun();

  public static final Map<String, Supplier<CelestialBodyType>> TYPES = new HashMap<>(3);
  static {
    TYPES.put(Sun.ID, Sun::new);
    TYPES.put(Moon.ID, Moon::new);
    TYPES.put(Custom.ID, Custom::new);
  }

  public static CelestialBodyType newFromJson(JsonObject obj) {
    String typeStr = obj.get(CONFIG_TYPE_KEY).asString(Sun.ID);
    Supplier<CelestialBodyType> create = TYPES.get(typeStr);
    if(create == null) {
      Log.warnf("Unknown celestial body type \"%s\"", typeStr);
      return DEFAULT;
    }
    CelestialBodyType type = create.get();
    type.importFromJson(obj);
    return type;
  }

  protected void importFromJson(JsonObject obj) {}

  public void appendToConfig(JsonObject obj) {
    obj.add(CONFIG_TYPE_KEY, getId());
  }

  /**
   * will be called when a scene is loaded to load associated custom textures
   */
  public void loadCustomTextures(SceneIOProvider ioContext) {
  }

  public abstract Texture getTexture();

  @Override
  public String getDescription() {
    return getName();
  }
}
