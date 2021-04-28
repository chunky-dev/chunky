package se.llbit.chunky.renderer;

import se.llbit.chunky.renderer.scene.RayTracer;
import se.llbit.chunky.renderer.scene.TileBasedRenderer;
import se.llbit.util.TaskTracker;

public class PreviewRenderer extends TileBasedRenderer {
  protected RayTracer tracer;

  public PreviewRenderer(RayTracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public void render(InternalRenderManager manager) throws InterruptedException {
    TaskTracker.Task task = manager.getRenderTask();

    task.update("Preview", 2, 0, "");
    for (int i = 0; i < 2; i++) {
      submitPreviewTiles(manager, tracer, i);
      manager.pool.awaitEmpty();
      task.update(i+1);
      if (postRender.getAsBoolean()) return;
    }
  }
}
