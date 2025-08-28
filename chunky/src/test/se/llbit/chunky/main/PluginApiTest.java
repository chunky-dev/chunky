/* Copyright (c) 2016-2021 Jesper Öqvist <jesper@llbit.se>
 * Copyright (c) 2016-2021 Chunky contributors
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.main;

import org.junit.Test;
import se.llbit.chunky.plugin.TabTransformer;
import se.llbit.chunky.renderer.*;
import se.llbit.chunky.renderer.scene.RayTracer;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.renderer.scene.SceneFactory;
import se.llbit.chunky.ui.render.RenderControlsTabTransformer;
import se.llbit.util.Mutable;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertSame;
import static com.google.common.truth.Truth.assertThat;

public class PluginApiTest {
  @Test public void testSetRenderContextFactory() {
    Chunky chunky = new Chunky(ChunkyOptions.getDefaults());
    RenderContextFactory myFactory = chunky1 -> null;
    chunky.setRenderContextFactory(myFactory);
    assertSame(myFactory, chunky.getRenderContextFactory());
  }

  @Test public void testSetSceneFactory() {
    Chunky chunky = new Chunky(ChunkyOptions.getDefaults());
    SceneFactory myFactory = new SceneFactory() {
      @Override public Scene newScene() {
        return null;
      }

      @Override public Scene copyScene(Scene scene) {
        return null;
      }
    };
    chunky.setSceneFactory(myFactory);
    assertSame(myFactory, chunky.getSceneFactory());
  }

  @Test public void testSetPreviewRayTracerFactory() throws Exception {
    Chunky chunky = new Chunky(ChunkyOptions.getDefaults());
    RayTracer tracer = (scene, state) -> state.ray.color.set(0, 0, 0, 1);
    RayTracerFactory myFactory = () -> tracer;
    //noinspection deprecation
    chunky.setPreviewRayTracerFactory(myFactory);

    // Get ray tracer through reflection
    Renderer renderer = DefaultRenderManager.previewRenderers.get("PluginPreviewRenderer");
    Field rayTracer = renderer.getClass().getDeclaredField("tracer");
    rayTracer.setAccessible(true);
    assertSame(tracer, rayTracer.get(renderer));
  }

  @Test public void testSetRayTracerFactory() throws Exception {
    Chunky chunky = new Chunky(ChunkyOptions.getDefaults());
    RayTracer tracer = (scene, state) -> state.ray.color.set(0, 0, 0, 1);
    RayTracerFactory myFactory = () -> tracer;
    //noinspection deprecation
    chunky.setRayTracerFactory(myFactory);

    // Get ray tracer through reflection
    Renderer renderer = DefaultRenderManager.renderers.get("PluginRenderer");
    Field rayTracer = renderer.getClass().getDeclaredField("tracer");
    rayTracer.setAccessible(true);
    assertSame(tracer, rayTracer.get(renderer));
  }

  @Test
  public void testSetCustomPreviewRenderer() {
    Renderer renderer = new PreviewRenderer("TestPreviewRenderer", "Test Preview Renderer",
        "Test preview renderer.", null);
    Chunky.addPreviewRenderer(renderer);
    assertSame(renderer, DefaultRenderManager.previewRenderers.get("TestPreviewRenderer"));
  }

  @Test
  public void testSetCustomRenderer() {
    Renderer renderer = new PathTracingRenderer("TestRenderer", "Test Renderer",
        " Test renderer.",null);
    Chunky.addRenderer(renderer);
    assertSame(renderer, DefaultRenderManager.renderers.get("TestRenderer"));
  }

  @Test public void testSetRenderControlsTabTransformer() {
    Chunky chunky = new Chunky(ChunkyOptions.getDefaults());
    RenderControlsTabTransformer transformer = tabs -> Collections.emptyList();
    chunky.setRenderControlsTabTransformer(transformer);
    assertSame(transformer, chunky.getRenderControlsTabTransformer());
  }

  @Test public void testSetMainTabTransformer() {
    Chunky chunky = new Chunky(ChunkyOptions.getDefaults());
    TabTransformer transformer = tabs -> Collections.emptyList();
    chunky.setMainTabTransformer(transformer);
    assertSame(transformer, chunky.getMainTabTransformer());
  }

  @Test(timeout = 10000)
  // 10 second timeout (should only happen with a test programming error,
  // or a very, very slow computer)
  public void testSetSceneResetListener() throws InterruptedException {
    Mutable<ResetReason> cbReason = new Mutable<>(null);
    Mutable<Scene> cbScene = new Mutable<>(null);

    Chunky chunky = new Chunky(ChunkyOptions.getDefaults());
    DefaultRenderManager rm = (DefaultRenderManager) chunky.getRenderController().getRenderManager();
    rm.shutdown();
    rm.join();

    SceneProvider provider = chunky.getSceneManager().getSceneProvider();

    // Create the listener
    BiConsumer<ResetReason, Scene> listener = (resetReason, scene) -> {
      cbReason.set(resetReason);
      cbScene.set(scene);
    };

    provider.addChangeListener(listener);

    // Enqueue a scene refresh event
    provider.withEditSceneProtected(scene -> {
      scene.refresh();
      scene.setResetReason(ResetReason.SCENE_LOADED);
      scene.setRenderMode(RenderMode.PREVIEW);
    });

    // Verify scene refresh has not been called yet
    assertThat(cbReason.get()).isNull();
    assertThat(cbScene.get()).isNull();

    // Accept the scene refresh
    provider.awaitSceneStateChange();

    // Verify scene refresh has been called
    assertThat(cbReason.get()).isNotNull();
    assertThat(cbScene.get()).isNotNull();

    cbReason.set(null);
    cbScene.set(null);

    provider.removeChangeListener(listener);

    // Create a scene refresh event
    provider.withEditSceneProtected(scene -> {
      scene.refresh();
      scene.setResetReason(ResetReason.SCENE_LOADED);
      scene.setRenderMode(RenderMode.PREVIEW);
    });

    // Accept the scene refresh event
    provider.awaitSceneStateChange();

    // Verify listener was removed successfully
    assertThat(cbReason.get()).isNull();
    assertThat(cbScene.get()).isNull();
  }
}
