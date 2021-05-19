package se.llbit.chunky.renderer.scene;

import java.util.Random;
import se.llbit.chunky.world.Material;
import se.llbit.math.Grid;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * Factory for creating {@link EmitterSampler}s.
 */
public class EmitterSamplerFactory {

  public EmitterSampler create(Scene scene) {
    switch (scene.getEmitterSamplingStrategy()) {
      case ALL:
        return new AllEmittersSampler();
      case ONE:
        return new SingleEmitterSampler();
      case NONE:
      default:
        return (s, ray, random) -> new Vector4(0, 0, 0, 0);
    }
  }


  interface EmitterSampler {

    /**
     * Sample emitters for the current ray and return the indirect emitter color.
     */
    Vector4 sample(Scene scene, Ray ray, Random random);
  }

  private static class SingleEmitterSampler implements EmitterSampler {

    @Override
    public Vector4 sample(Scene scene, Ray ray, Random random) {
      Vector4 indirectEmitterColor = new Vector4(0, 0, 0, 0);

      Grid.EmitterPosition pos = scene.getEmitterGrid()
          .sampleEmitterPosition((int) ray.o.x, (int) ray.o.y, (int) ray.o.z, random);
      if (pos != null) {
        indirectEmitterColor = sampleEmitter(scene, ray, pos, random);
      }

      return indirectEmitterColor;
    }
  }

  private static class AllEmittersSampler implements EmitterSampler {

    @Override
    public Vector4 sample(Scene scene, Ray ray, Random random) {
      Vector4 indirectEmitterColor = new Vector4(0, 0, 0, 0);

      for (Grid.EmitterPosition pos : scene.getEmitterGrid()
          .getEmitterPositions((int) ray.o.x, (int) ray.o.y, (int) ray.o.z)) {
        indirectEmitterColor.scaleAdd(1, sampleEmitter(scene, ray, pos, random));
      }

      return indirectEmitterColor;
    }
  }

  /**
   * Cast a shadow ray from the intersection point (given by ray) to the emitter at position pos.
   * Returns the contribution of this emitter (0 if the emitter is occluded)
   *
   * @param scene  The scene being rendered
   * @param ray    The ray that generated the intersection
   * @param pos    The position of the emitter to sample
   * @param random RNG
   * @return The contribution of the emitter
   */
  static Vector4 sampleEmitter(Scene scene, Ray ray, Grid.EmitterPosition pos,
      Random random) {
    Vector4 indirectEmitterColor = new Vector4();
    Ray emitterRay = new Ray();
    emitterRay.set(ray);
    // TODO Sampling a random point on the model would be better than using a random point in the middle of the cube
    Vector3 target = new Vector3(pos.x + (random.nextDouble() - 0.5) * pos.radius,
        pos.y + (random.nextDouble() - 0.5) * pos.radius,
        pos.z + (random.nextDouble() - 0.5) * pos.radius);
    emitterRay.d.set(target);
    emitterRay.d.sub(emitterRay.o);
    double distance = emitterRay.d.length();
    emitterRay.d.normalize();
    double indirectEmitterCoef = emitterRay.d.dot(emitterRay.n);
    if (indirectEmitterCoef > 0) {
      // Here We need to invert the material.
      // The fact that the dot product is > 0 guarantees that the ray is going away from the surface
      // it just met. This means the ray is going from the block just hit to the previous material (usually air or water)
      // TODO If/when normal mapping is implemented, indirectEmitterCoef will be computed with the mapped normal
      //      but the dot product with the original geometry normal will still need to be computed
      //      to ensure the emitterRay isn't going through the geometry
      Material prev = emitterRay.getPrevMaterial();
      int prevData = emitterRay.getPrevData();
      emitterRay.setPrevMaterial(emitterRay.getCurrentMaterial(), emitterRay.getCurrentData());
      emitterRay.setCurrentMaterial(prev, prevData);
      emitterRay.emittance.set(0, 0, 0);
      emitterRay.o.scaleAdd(Ray.EPSILON, emitterRay.d);
      RayTracers.nextIntersection(scene, emitterRay);
      if (emitterRay.getCurrentMaterial().emittance > Ray.EPSILON) {
        indirectEmitterColor.set(emitterRay.color);
        indirectEmitterColor.scale(emitterRay.getCurrentMaterial().emittance);
        // TODO Take fog into account
        indirectEmitterCoef *= scene.emitterIntensity;
        // Dont know if really realistic but offer better convergence and is better artistically
        indirectEmitterCoef /= Math.max(distance * distance, 1);
      }
    } else {
      indirectEmitterCoef = 0;
    }
    indirectEmitterColor.scale(indirectEmitterCoef);
    return indirectEmitterColor;
  }
}
