package se.llbit.chunky.renderer.postprocessing;

import se.llbit.chunky.plugin.PluginApi;

import java.util.*;

public class PostProcessingFilters {
  /**
   * Don't let anyone instantiate this class.
   */
  private PostProcessingFilters() {}

  private static final Map<String, Class<? extends PostProcessingFilter>> filterClassesById = new HashMap<>();
  // using tree map for the added benefit of sorting by name
  private static final Map<Class<? extends PostProcessingFilter>, String> filterNamesByClass = new HashMap<>();
  private static final List<Class<? extends PostProcessingFilter>> filterClasses = new ArrayList<>(0);
  private static final Map<String, Class<? extends PostProcessingFilter>> filterClassesByName = new TreeMap<>();
  private static final Map<Class<? extends PostProcessingFilter>, PostProcessingFilter> filtersByClass = new HashMap<>(0);

  static {
    addPostProcessingFilter(GammaCorrectionFilter.class);
    addPostProcessingFilter(HejlBurgessDawsonFilmicFilter.class);
    addPostProcessingFilter(AldridgeFilmicFilter.class);
    addPostProcessingFilter(HableFilmicFilter.class);
    addPostProcessingFilter(HableUpdatedFilmicFilter.class);
    addPostProcessingFilter(LottesFilmicFilter.class);
    addPostProcessingFilter(DayFilmicFilter.class);
    addPostProcessingFilter(UchimuraFilmicFilter.class);
    addPostProcessingFilter(HillACESFilmicFilter.class);
    addPostProcessingFilter(NarkowiczACESFilmicFilter.class);
    addPostProcessingFilter(GuyACESFilmicFilter.class);
    addPostProcessingFilter(UE4FilmicFilter.class);
    addPostProcessingFilter(AgXFilter.class);
    addPostProcessingFilter(VignetteFilter.class);
  }

  public static Optional<Class<? extends PostProcessingFilter>> getPostProcessingFilterFromId(String id) {
    return Optional.ofNullable(filterClassesById.get(id));
  }

  // TODO Create a ChoiceBox that can use different string as ID and as visual representation
  // so this isn't needed
  @Deprecated
  public static Optional<Class<? extends PostProcessingFilter>> getPostProcessingFilterFromName(String name) {
    return Optional.ofNullable(filterClassesByName.get(name));
  }

  public static String getFilterName(Class<? extends PostProcessingFilter> filterClass) {
    return filterNamesByClass.get(filterClass);
  }

  public static Collection<Class<? extends PostProcessingFilter>> getFilterClasses() {
    return filterClasses;
  }

  public static PostProcessingFilter getSampleFilterFromClass(Class<? extends PostProcessingFilter> filterClass) {
    return filtersByClass.get(filterClass);
  }

  @PluginApi
  public static void addPostProcessingFilter(Class<? extends PostProcessingFilter> filterClass) {
    try {
      PostProcessingFilter filter = filterClass.newInstance();
      filterClasses.add(filterClass);
      filterClassesById.put(filter.getId(), filterClass);
      filterNamesByClass.put(filterClass, filter.getName());
      filterClassesByName.put(filter.getName(), filterClass);
      filtersByClass.put(filterClass, filter);
    } catch (InstantiationException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }
}
