package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.renderer.projection.ParallelProjector;
import se.llbit.chunky.renderer.projection.ProjectionMode;
import se.llbit.log.Log;
import se.llbit.math.Ray;
import se.llbit.util.TaskTracker;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * The AlphaBuffer acts as a cache for the alpha layer and will only be calculated on demand.
 */
public class AlphaBuffer {

  public enum Type {
    UNSUPPORTED(0,
      (buffer, index, alphaValue) -> {
      }
    ),
    UINT8(1,
      (buffer, index, alphaValue) -> buffer.put(index, (byte) (255 * alphaValue + 0.5))
    ),
    FP32(4,
      (buffer, index, alphaValue) -> buffer.putFloat(index<<2, (float) alphaValue)
    );

    final byte byteSize;
    final AlphaWriter writer;

    Type(int byteSize, AlphaWriter writer) {
      this.byteSize = (byte) byteSize;
      this.writer = writer;
    }

    @FunctionalInterface
    interface AlphaWriter {
      /**
       * @param alphaValue 1 = occluded, 0 = transparent
       */
      void write(ByteBuffer buffer, int index, double alphaValue);
    }
  }

  private Type type = Type.UNSUPPORTED;
  private ByteBuffer buffer = null;

  public Type getType() {
    return type;
  }

  public ByteBuffer getBuffer() {
    return buffer;
  }

  public void reset() {
    type = Type.UNSUPPORTED;
    buffer = null;
  }

  /**
   * Compute the alpha channel.
   */
  void computeAlpha(Scene scene, Type type, TaskTracker taskTracker) {
    if(type == Type.UNSUPPORTED) return;
    if(this.type == type && buffer != null) return;

    try (TaskTracker.Task task = taskTracker.task("Computing alpha channel")) {
      this.type = type;
      buffer = ByteBuffer.allocate(scene.width * scene.height * type.byteSize);

      AtomicInteger done = new AtomicInteger(0);
      Chunky.getCommonThreads().submit(() -> {
        IntStream.range(0, scene.width).parallel().forEach(x -> {
          WorkerState state = new WorkerState();

          for (int y = 0; y < scene.height; y++) {
            computeAlpha(scene, x, y, state);
          }

          task.update(scene.width, done.incrementAndGet());
        });
      }).get();
    } catch (InterruptedException | ExecutionException e) {
      Log.error("Failed to compute alpha channel", e);
    }
  }

  /**
   * Compute the alpha channel based on sky visibility.
   */
  public void computeAlpha(Scene scene, int x, int y, WorkerState state) {
    Ray ray = state.ray;
    double halfWidth = scene.width / (2.0 * scene.height);
    double invHeight = 1.0 / scene.height;

    // Rotated grid supersampling.
    double[][] offsets = new double[][]{
      {-3.0 / 8.0, 1.0 / 8.0},
      {1.0 / 8.0, 3.0 / 8.0},
      {-1.0 / 8.0, -3.0 / 8.0},
      {3.0 / 8.0, -1.0 / 8.0},
    };

    double occlusion = 0.0;
    for (double[] offset : offsets) {
      scene.camera.calcViewRay(ray,
        -halfWidth + (x + offset[0]) * invHeight,
        -0.5 + (y + offset[1]) * invHeight);
      ray.o.x -= scene.origin.x;
      ray.o.y -= scene.origin.y;
      ray.o.z -= scene.origin.z;

      if (scene.camera.getProjectionMode() == ProjectionMode.PARALLEL) {
        ParallelProjector.fixRay(state.ray, scene);
      }
      ray.setCurrentMedium(scene.getWorldMaterial(ray));
      occlusion += PreviewRayTracer.skyOcclusion(scene, state);
    }
    occlusion /= 4.0;

    type.writer.write(buffer, y * scene.width + x, occlusion);
  }
}
