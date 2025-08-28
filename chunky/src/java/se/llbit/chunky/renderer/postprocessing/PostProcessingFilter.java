package se.llbit.chunky.renderer.postprocessing;

import se.llbit.chunky.plugin.PluginApi;
import se.llbit.json.JsonObject;
import se.llbit.util.Configurable;
import se.llbit.util.HasControls;
import se.llbit.util.Registerable;

/**
 * A post-processing filter.
 * <p>
 * These filters are used to convert the HDR sample buffer into an SDR image that can be displayed.
 * Exposure is applied before filters are applied.
 * <p>
 * Filters that support processing a single pixel at a time should implement {@link
 * PixelPostProcessingFilter} instead.
 */
@PluginApi
public abstract class PostProcessingFilter implements Registerable, Configurable, HasControls {
  /**
   * Post process the entire frame
   * @param width The width of the image
   * @param height The height of the image
   * @param input The input linear image as double array, exposure has been applied
   */
  public abstract void processFrame(int width, int height, double[] input);

  /**
   * Get description of the post processing filter
   * @return The description of the post processing filter
   */
  @Override
  public abstract String getDescription();

  @Override
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.add("id", getId());
    filterSettingsToJson(json);
    return json;
  }

  /**
   * Write filter-specific settings to the JSON
   * @param json The JsonObject to write settings to
   */
  public abstract void filterSettingsToJson(JsonObject json);
}
