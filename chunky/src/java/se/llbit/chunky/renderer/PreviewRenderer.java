package se.llbit.chunky.renderer;

import se.llbit.chunky.renderer.scene.RayTracer;
import se.llbit.chunky.renderer.scene.TileBasedRenderer;

public class PreviewRenderer extends TileBasedRenderer {
  protected RayTracer tracer;

  public PreviewRenderer(RayTracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public void render(InternalRenderManager manager) throws InterruptedException {
    submitTiles(manager, tracer, true);
    postRender.getAsBoolean();
  }
}
