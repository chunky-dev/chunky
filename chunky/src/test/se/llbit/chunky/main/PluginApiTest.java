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

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

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
    chunky.setPreviewRayTracerFactory(myFactory);

    // Get ray tracer through reflection
    Renderer renderer = DefaultRenderManager.previewRenderers.get("Plugin Preview Renderer");
    Field rayTracer = renderer.getClass().getDeclaredField("tracer");
    rayTracer.setAccessible(true);
    assertSame(tracer, rayTracer.get(renderer));
  }

  @Test public void testSetRayTracerFactory() throws Exception {
    Chunky chunky = new Chunky(ChunkyOptions.getDefaults());
    RayTracer tracer = (scene, state) -> state.ray.color.set(0, 0, 0, 1);
    RayTracerFactory myFactory = () -> tracer;
    chunky.setRayTracerFactory(myFactory);

    // Get ray tracer through reflection
    Renderer renderer = DefaultRenderManager.renderers.get("Plugin Renderer");
    Field rayTracer = renderer.getClass().getDeclaredField("tracer");
    rayTracer.setAccessible(true);
    assertSame(tracer, rayTracer.get(renderer));
  }

  @Test
  public void testSetCustomPreviewRenderer() {
    Renderer renderer = new PreviewRenderer(null, "Test Preview Renderer", "TestPreviewRenderer");
    Chunky.addPreviewRenderer(renderer);
    assertSame(renderer, DefaultRenderManager.previewRenderers.get("Test Preview Renderer"));
  }

  @Test
  public void testSetCustomRenderer() {
    Renderer renderer = new PathTracingRenderer(null, "Test Renderer", "TestRenderer");
    Chunky.addRenderer(renderer);
    assertSame(renderer, DefaultRenderManager.renderers.get("Test Renderer"));
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
}
