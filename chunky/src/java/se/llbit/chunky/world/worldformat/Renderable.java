package se.llbit.chunky.world.worldformat;

import se.llbit.math.Octree;

/** For simple world formats that only support being rendered directly */
public interface Renderable extends Loadable {
  void populateData(RenderableData data);

  record RenderableData(Octree octree) {}
}
