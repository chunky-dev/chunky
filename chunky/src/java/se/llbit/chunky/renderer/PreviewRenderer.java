package se.llbit.chunky.renderer;

import se.llbit.chunky.renderer.scene.Camera;
import se.llbit.chunky.renderer.scene.RayTracer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.TileBasedRenderer;
import se.llbit.math.Ray;
import se.llbit.util.TaskTracker;

public class PreviewRenderer extends TileBasedRenderer {
  protected RayTracer tracer;

  public PreviewRenderer(RayTracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public String getIdString() {
    return "InternalPreview";
  }

  @Override
  public void render(InternalRenderManager manager) throws InterruptedException {
    TaskTracker.Task task = manager.getRenderTask();
    task.update("Preview", 2, 0, "");

    Scene scene = manager.bufferedScene;
    int width = scene.width;
    int height = scene.height;

    Camera cam = scene.camera();
    double halfWidth = width / (2.0 * height);
    double invHeight = 1.0 / height;

    Ray target = new Ray();
    boolean hit = scene.traceTarget(target);
    int tx = (int) Math.floor(target.o.x + target.d.x * Ray.OFFSET);
    int ty = (int) Math.floor(target.o.y + target.d.y * Ray.OFFSET);
    int tz = (int) Math.floor(target.o.z + target.d.z * Ray.OFFSET);

    double[] sampleBuffer = scene.getSampleBuffer();

    for (int i = 0; i < 2; i++) {
      int sampleNum = i;

      submitTiles(manager, (state, pixel) -> {
        int x = pixel.firstInt();
        int y = pixel.secondInt();

        int offset = 3 * (y*width + x);

        // Interlacing
        if (((x + y) % 2) == sampleNum) return;

        // Draw crosshairs
        if (x == width / 2 && (y >= height / 2 - 5 && y <= height / 2 + 5) || y == height / 2 && (
            x >= width / 2 - 5 && x <= width / 2 + 5)) {
          sampleBuffer[offset + 0] = 0xFF;
          sampleBuffer[offset + 1] = 0xFF;
          sampleBuffer[offset + 2] = 0xFF;
          return;
        }

        cam.calcViewRay(state.ray, state.random,
            -halfWidth + x * invHeight,
            -0.5 + y * invHeight);
        scene.rayTrace(tracer, state);

        // Target highlighting.
        int rx = (int) Math.floor(state.ray.o.x + state.ray.d.x * Ray.OFFSET);
        int ry = (int) Math.floor(state.ray.o.y + state.ray.d.y * Ray.OFFSET);
        int rz = (int) Math.floor(state.ray.o.z + state.ray.d.z * Ray.OFFSET);
        if (hit && tx == rx && ty == ry && tz == rz) {
          state.ray.color.x = 1 - state.ray.color.x;
          state.ray.color.y = 1 - state.ray.color.y;
          state.ray.color.z = 1 - state.ray.color.z;
          state.ray.color.w = 1;
        }

        sampleBuffer[offset + 0] = state.ray.color.x;
        sampleBuffer[offset + 1] = state.ray.color.y;
        sampleBuffer[offset + 2] = state.ray.color.z;

        if (sampleNum == 0 && x < (width - 1)) {
          sampleBuffer[offset + 3] = state.ray.color.x;
          sampleBuffer[offset + 4] = state.ray.color.y;
          sampleBuffer[offset + 5] = state.ray.color.z;
        }
      });

      manager.pool.awaitEmpty();
      task.update(i+1);
      if (postRender.getAsBoolean()) return;
    }
  }
}
