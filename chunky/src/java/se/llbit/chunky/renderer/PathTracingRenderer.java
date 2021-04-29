package se.llbit.chunky.renderer;

import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.RayTracer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.TileBasedRenderer;

public class PathTracingRenderer extends TileBasedRenderer {
  protected RayTracer tracer;

  public PathTracingRenderer(RayTracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public String getIdString() {
    return "InternalRender";
  }

  @Override
  public void render(InternalRenderManager manager) throws InterruptedException {
    Scene scene = manager.bufferedScene;
    int width = scene.width;
    int height = scene.height;

    int sppPerPass = manager.context.sppPerPass();
    Camera cam = scene.camera();
    double halfWidth = width / (2.0 * height);
    double invHeight = 1.0 / height;

    double[] sampleBuffer = scene.getSampleBuffer();

    while (scene.spp < scene.getTargetSpp()) {
      int spp = scene.spp;
      double passinv = 1.0 / sppPerPass;
      double sinv = 1.0 / (sppPerPass + spp);

      submitTiles(manager, (state, pixel) -> {
        int x = pixel.firstInt();
        int y = pixel.secondInt();

        double sr = 0;
        double sg = 0;
        double sb = 0;

        for (int k = 0; k < sppPerPass; k++) {
          double ox = state.random.nextDouble();
          double oy = state.random.nextDouble();

          cam.calcViewRay(state.ray, state.random,
              -halfWidth + (x + ox) * invHeight,
              -0.5 + (y + oy) * invHeight);
          scene.rayTrace(tracer, state);

          sr += state.ray.color.x;
          sg += state.ray.color.y;
          sb += state.ray.color.z;
        }

        int offset = 3 * (y*width + x);
        sampleBuffer[offset + 0] = (sampleBuffer[offset + 0] * spp + (sr * passinv)) * sinv;
        sampleBuffer[offset + 1] = (sampleBuffer[offset + 1] * spp + (sg * passinv)) * sinv;
        sampleBuffer[offset + 2] = (sampleBuffer[offset + 2] * spp + (sb * passinv)) * sinv;
      });

      manager.pool.awaitEmpty();
      scene.spp += sppPerPass;
      if (postRender.getAsBoolean()) return;
    }
  }
}
