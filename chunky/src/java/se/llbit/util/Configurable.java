package se.llbit.util;

import se.llbit.json.JsonObject;

/**
 * This interface specifies an object that can be configured by the user.
 * This would be, for example, a post processing method.
 */
public interface Configurable extends JsonSerializable {
  /**
   * Load the configuration from the given JSON object that may have been created by {@link #toJson()}
   * but may as well have been created by external tools.
   *
   * @param json Source object
   */
  void fromJson(JsonObject json);

  /**
   * Restore the default configuration.
   */
  void reset();
}
