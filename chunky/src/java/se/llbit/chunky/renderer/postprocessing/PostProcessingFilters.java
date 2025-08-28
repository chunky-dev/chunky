package se.llbit.chunky.renderer.postprocessing;

import se.llbit.chunky.plugin.PluginApi;

import java.util.*;

public class PostProcessingFilters {
  /**
   * Don't let anyone instantiate this class.
   */
  private PostProcessingFilters() {}

  private static final Map<String, PostProcessingFilter> filters = new HashMap<>();
  // using tree map for the added benefit of sorting by name
  private static final Map<String, PostProcessingFilter> filtersByName = new TreeMap<>();
  private static final List<PostProcessingFilter> filters2 = new ArrayList<>(0);

  static {
    addPostProcessingFilter(new GammaCorrectionFilter());
    addPostProcessingFilter(new HejlBurgessDawsonFilmicFilter());
    addPostProcessingFilter(new AldridgeFilmicFilter());
    addPostProcessingFilter(new HableFilmicFilter());
    addPostProcessingFilter(new HableUpdatedFilmicFilter());
    addPostProcessingFilter(new LottesFilmicFilter());
    addPostProcessingFilter(new DayFilmicFilter());
    addPostProcessingFilter(new UchimuraFilmicFilter());
    addPostProcessingFilter(new HillACESFilmicFilter());
    addPostProcessingFilter(new NarkowiczACESFilmicFilter());
    addPostProcessingFilter(new GuyACESFilmicFilter());
    addPostProcessingFilter(new UE4FilmicFilter());
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
    return filters2;
  }

  @PluginApi
  public static void addPostProcessingFilter(PostProcessingFilter filter) {
    filters.put(filter.getId(), filter);
    filtersByName.put(filter.getName(), filter);
    filters2.add(filter);
  }
}
