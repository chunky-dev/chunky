package se.llbit.chunky.renderer.postprocessing;

import se.llbit.chunky.plugin.PluginApi;

import java.util.*;

public abstract class PostProcessingFilters {

  private static final Map<String, PostProcessingFilter> filters = new HashMap<>();
  // using tree map for the added benefit of sorting by name
  private static final Map<String, PostProcessingFilter> filtersByName = new TreeMap<>();
  private static final PostProcessingFilter defaultFilter;
  
  static {
    defaultFilter = new GammaCorrectionFilter();
    addPostProcessingFilter(defaultFilter);
    addPostProcessingFilter(new NoneFilter());
    addPostProcessingFilter(new Tonemap1Filter());
    addPostProcessingFilter(new ACESFilmicFilter());
    addPostProcessingFilter(new HableToneMappingFilter());
  }
  
  public static Optional<PostProcessingFilter> getPostProcessingFilterFromId(String id) {
    return Optional.ofNullable(filters.get(id));
  }

  // TODO Create a ChoiceBox that can use different string as ID and as visual representation
  // so this isn't needed
  @Deprecated
  public static Optional<PostProcessingFilter> getPostProcessingFilterFromName(String name) {
    return Optional.ofNullable(filtersByName.get(name));
  }

  public static Collection<PostProcessingFilter> getFilters() {
    return filtersByName.values();
  }

  public static PostProcessingFilter getDefault() {
    return defaultFilter;
  }

  @PluginApi
  public static void addPostProcessingFilter(PostProcessingFilter filter) {
    filters.put(filter.getId(), filter);
    filtersByName.put(filter.getName(), filter);
  }
}
