package se.llbit.chunky.renderer;

import se.llbit.chunky.renderer.scene.RayTracer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.TileBasedRenderer;

public class PathTracingRenderer extends TileBasedRenderer {
  protected RayTracer tracer;

  public PathTracingRenderer(RayTracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public void render(InternalRenderManager manager) throws InterruptedException {
    Scene scene = manager.bufferedScene;

    while (scene.spp < scene.getTargetSpp()) {
      int pass = submitRenderTiles(manager, tracer);
      manager.pool.awaitEmpty();
      scene.spp += pass;
      if (postRender.getAsBoolean()) return;
    }
  }
}
