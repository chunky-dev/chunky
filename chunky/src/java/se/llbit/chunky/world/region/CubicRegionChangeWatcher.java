package se.llbit.chunky.world.region;

import javafx.application.Platform;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.map.MapView;
import se.llbit.chunky.map.WorldMapLoader;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.chunky.world.ChunkView;
import se.llbit.chunky.world.CubicWorld;

public class CubicRegionChangeWatcher extends RegionChangeWatcher {
  public CubicRegionChangeWatcher(WorldMapLoader loader, MapView mapView) {
    super(loader, mapView, "Cubic Region Refresher");
  }

  @Override public void run() {
    try {
      while (!isInterrupted()) {
        sleep(3000);
        CubicWorld world = (CubicWorld) mapLoader.getWorld();
        if (world.reloadPlayerData()) {
          if (PersistentSettings.getFollowPlayer()) {
            Platform.runLater(() -> world.playerPos().ifPresent(mapView::panTo));
          }
        }
        ChunkView theView = view;
        for (int rx = theView.prx0; rx <= theView.prx1; ++rx) {
          for (int rz = theView.prz0; rz <= theView.prz1; ++rz) {
            Region region = world.getRegionWithinRange(ChunkPosition.get(rx, rz), theView.yMin, theView.yMax);
            if (region.isEmpty()) {
              ChunkPosition pos = ChunkPosition.get(rx, rz);
              if (world.regionExistsWithinRange(pos, theView.yMin, theView.yMax)) {
                region = world.createRegion(pos);
              }
              world.setRegion(pos, region);
              region.parse(theView.yMin, theView.yMax);
              world.regionDiscovered(pos);
              mapLoader.regionUpdated(pos);
            } else if (region.hasChanged()) {
              region.parse(theView.yMin, theView.yMax);
              ChunkPosition pos = region.getPosition();
              mapLoader.regionUpdated(pos);
            }
          }
        }
      }
    } catch (InterruptedException e) {
      // Interrupted.
    }
  }
}
