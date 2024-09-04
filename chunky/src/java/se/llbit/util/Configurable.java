package se.llbit.util;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.json.JsonObject;

/**
 * This interface specifies an object that can be configured by the user.
 * This would be, for example, a post processing method.
 */
public interface Configurable {
  /**
   * Load the configuration from the given JSON object that may have been created by {@link #storeConfiguration(JsonObject)}
   * but may as well have been created by external tools.
   *
   * @param json Source object
   */
  void loadConfiguration(JsonObject json);

  /**
   * Store the configuration in the given JSON object such that it can be loaded later with {@link #loadConfiguration(JsonObject)}.
   *
   * @param json Destination object
   */
  void storeConfiguration(JsonObject json);

  /**
   * Restore the default configuration.
   */
  void reset();
}
