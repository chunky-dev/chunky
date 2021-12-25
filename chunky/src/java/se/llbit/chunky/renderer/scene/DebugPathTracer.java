package se.llbit.chunky.renderer.scene;

import org.apache.commons.math3.util.FastMath;
import se.llbit.chunky.renderer.EmitterSamplingStrategy;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.chunky.world.Material;
import se.llbit.math.Grid;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;
import se.llbit.math.Vector4;

import java.util.Random;

public class DebugPathTracer implements RayTracer {
    @Override
    public void trace(Scene scene, WorkerState state) {
        Ray ray = state.ray;
        Random rand = state.random;
        Vector3 emitterContribution = new Vector3();
        ray.color.set(0, 0, 0, 1);

        if (PreviewRayTracer.nextIntersection(scene, ray)) {
            if (scene.getEmitterSamplingStrategy() != EmitterSamplingStrategy.NONE) {
                Ray emitterRay = new Ray();
//                for (Grid.EmitterPosition pos : scene.getEmitterGrid().getEmitterPositions(
//                    (int) ray.o.x,
//                    (int) ray.o.y,
//                    (int) ray.o.z
//                )) {
                Grid.EmitterPosition pos = scene.getEmitterGrid().sampleEmitterPosition(
                    (int) ray.o.x,
                    (int) ray.o.y,
                    (int) ray.o.z,
                    rand
                );
                for (Vector3 target : pos.sampleAll(rand)) {
                    emitterRay.set(ray);
                    emitterRay.d.set(target);
                    emitterRay.d.sub(emitterRay.o);
                    double distance = emitterRay.d.length();
                    emitterRay.d.scale(1 / distance);

                    if (emitterRay.d.dot(ray.getN()) > 0) {
                        emitterRay.o.scaleAdd(Ray.OFFSET, emitterRay.d);
                        emitterRay.distance += Ray.OFFSET;
                        PreviewRayTracer.nextIntersection(scene, emitterRay);

                        if (Math.abs(emitterRay.distance - distance) < Ray.OFFSET) {
                            double e = Math.abs(emitterRay.d.dot(emitterRay.getN()));
                            e /= Math.max(distance * distance, 1);
                            e *= pos.block.emittance;
                            e *= scene.emitterIntensity;

                            emitterContribution.x += emitterRay.color.x * e;
                            emitterContribution.y += emitterRay.color.y * e;
                            emitterContribution.z += emitterRay.color.z * e;
                        }
                    }
                }
//                }
            }
        }

        ray.color.x *= emitterContribution.x;
        ray.color.y *= emitterContribution.y;
        ray.color.z *= emitterContribution.z;
    }
}
