package se.llbit.chunky.renderer.postprocessing;

import se.llbit.chunky.plugin.PluginApi;

import java.util.*;

public abstract class PostProcessingFilters {

  public static final PixelPostProcessingFilter NONE = new NoneFilter();

  private static final Map<String, PostProcessingFilter> filters = new HashMap<>();
  // using tree map for the added benefit of sorting by name
  private static final Map<String, PostProcessingFilter> filtersByName = new TreeMap<>();

  static {
    addPostProcessingFilter(NONE);
    addPostProcessingFilter(new GammaCorrectionFilter());
    addPostProcessingFilter(new Tonemap1Filter());
    addPostProcessingFilter(new ACESFilmicFilter());
    addPostProcessingFilter(new HableToneMappingFilter());
  }

  public static Optional<PostProcessingFilter> getPostProcessingFilterFromId(String id) {
    return Optional.ofNullable(filters.get(id));
  }

  public static Collection<PostProcessingFilter> getFilters() {
    return filtersByName.values();
  }

  @PluginApi
  public static void addPostProcessingFilter(PostProcessingFilter filter) {
    filters.put(filter.getId(), filter);
    filtersByName.put(filter.getName(), filter);
  }
}
