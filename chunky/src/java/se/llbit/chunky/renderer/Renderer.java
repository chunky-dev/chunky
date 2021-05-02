package se.llbit.chunky.renderer;

import java.util.function.BooleanSupplier;

public interface Renderer {
  /**
   * Get the short id string representing this renderer.
   */
  String getIdString();

  /**
   * Get the name string of this renderer.
   */
  String getNameString();

  /**
   * The post render callback. This should be run after rendering a frame.
   * It will return {@code true} if the render should terminate.
   *
   * Generally the render loop will look like:
   * {@code
   *   while (scene.spp < scene.getTargetSpp()) {
   *     submitTiles(manager, (state, pixel) -> {});
   *     manager.pool.awaitEmpty();
   *     scene.spp += 1; // update spp
   *     if (postRender.getAsBoolean()) break;
   *   }
   * }
   *
   * Implementation details, this deals with:
   *  * Checking if the render mode has changed
   *  * Updating the task-tracker
   *  * Repainting the canvas
   *  * Updating the {@code bufferedScene}
   */
  void setPostRender(BooleanSupplier callback);

  /**
   * This is called when a render is initiated.
   *
   * * It should render a frame, merge that frame with {@code manager.bufferedScene}, and update the spp values.
   * * It should call the post-render callback (set in {@code setPostRender(callback)}) and terminate if it returns {@code true}.
   */
  void render(DefaultRenderManager manager) throws InterruptedException;

  /**
   * This is called when the scene is reset. The default implementation does nothing.
   * This is for {@code Renderer}s which need to export the scene data in some way.
   */
  default void sceneReset(DefaultRenderManager manager, ResetReason reason) {}
}
