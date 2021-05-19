package se.llbit.chunky.renderer.scene;

import java.util.Random;
import se.llbit.chunky.block.Air;
import se.llbit.math.QuickMath;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

/**
 * Factory for creating {@link FogStrategy}s.
 */
public class FogStrategyFactory {

  public FogStrategy create(Scene scene) {
    if (scene.fogEnabled()) {
      if (scene.fastFog) {
        return new FastFogStrategy();
      } else {
        return new FancyFogStrategy();
      }
    }

    // Fog disabled, fog rendering is a no-op.
    return (scene1, ray, random, airDistance) -> {
    };
  }


  public interface FogStrategy {

    /**
     * Calculates effects of fog and apply to the {@code ray}.
     *
     * @param scene       The scene being rendered.
     * @param ray         The ray to apply fog to.
     * @param random      instance for generating random values.
     * @param airDistance TODO
     */
    void fog(Scene scene, Ray ray, Random random, double airDistance);
  }

  private static class FastFogStrategy extends BaseFogStrategy {

    @Override
    double calculateInscatter(double extinction, double airDistance, double offset,
        double fogDensity) {
      return 1 - extinction;
    }
  }

  private static class FancyFogStrategy extends BaseFogStrategy {

    @Override
    double calculateInscatter(double extinction, double airDistance, double offset,
        double fogDensity) {
      return airDistance * fogDensity * Math.exp(-offset * fogDensity);
    }
  }

  /**
   * Base class for default fast/fancy fog rendering methods.
   */
  private abstract static class BaseFogStrategy implements FogStrategy {

    /**
     * Extinction factor for fog rendering.
     */
    private static final double EXTINCTION_FACTOR = 0.04;

    abstract double calculateInscatter(double extinction, double airDistance, double offset,
        double fogDensity);

    @Override
    public void fog(Scene scene, Ray ray, Random random, double airDistance) {
      Vector3 ox = new Vector3(ray.o);
      Vector3 od = new Vector3(ray.d);
      // This is a simplistic fog model which gives greater artistic freedom but
      // less realism. The user can select fog color and density; in a more
      // realistic model color would depend on viewing angle and sun color/position.
      if (airDistance > 0) {
        Sun sun = scene.sun;

        // Pick point between ray origin and intersected object.
        // The chosen point is used to test if the sun is lighting the
        // fog between the camera and the first diffuse ray target.
        // The sun contribution will be proportional to the amount of
        // sunlit fog areas in the ray path, thus giving an approximation
        // of the sun inscatter leading to effects like god rays.
        // The way the sun contribution point is chosen is not
        // entirely correct because the original ray may have
        // travelled through glass or other materials between air gaps.
        // However, the results are probably close enough to not be distracting,
        // so this seems like a reasonable approximation.
        Ray atmos = new Ray();
        double offset = QuickMath.clamp(airDistance * random.nextFloat(),
            Ray.EPSILON, airDistance - Ray.EPSILON);
        atmos.o.scaleAdd(offset, od, ox);
        sun.getRandomSunDirection(atmos, random);
        atmos.setCurrentMaterial(Air.INSTANCE);

        double fogDensity = scene.getFogDensity() * EXTINCTION_FACTOR;
        double extinction = Math.exp(-airDistance * fogDensity);
        ray.color.scale(extinction);

        // Check sun visibility at random point to determine inscatter brightness.
        Vector4 attenuation = PathTracer.getDirectLightAttenuation(scene, atmos);
        if (attenuation.w > Ray.EPSILON) {
          Vector3 fogColor = scene.getFogColor();
          double inscatter;
          inscatter = calculateInscatter(extinction, airDistance, offset, fogDensity);
          ray.color.x += attenuation.x * attenuation.w * fogColor.x * inscatter;
          ray.color.y += attenuation.y * attenuation.w * fogColor.y * inscatter;
          ray.color.z += attenuation.z * attenuation.w * fogColor.z * inscatter;
        }
      }
    }
  }
}
