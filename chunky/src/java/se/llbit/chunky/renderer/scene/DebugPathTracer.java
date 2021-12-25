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
        ray.color.set(0, 0, 0, 1);

        if (PreviewRayTracer.nextIntersection(scene, ray)) {
            if (scene.getEmitterSamplingStrategy() != EmitterSamplingStrategy.NONE) {
                Grid.EmitterPosition pos = scene.getEmitterGrid().sampleEmitterPosition(
                    (int) ray.o.x,
                    (int) ray.o.y,
                    (int) ray.o.z,
                    rand
                );
                if (pos != null) {
                    Ray emitterRay = new Ray();
                    emitterRay.set(ray);
                    Vector3 target = new Vector3();

                    pos.sample(target, rand);
                    emitterRay.d.set(target);
                    emitterRay.d.sub(emitterRay.o);
                    double distance = emitterRay.d.length();
                    emitterRay.d.scale(1 / distance);

                    if (emitterRay.d.dot(ray.getN()) > 0) {
                        emitterRay.o.scaleAdd(Ray.OFFSET, emitterRay.d);
                        emitterRay.distance += Ray.OFFSET;
                        PreviewRayTracer.nextIntersection(scene, emitterRay);

                        emitterRay.o.sub(target);
                        if (emitterRay.o.lengthSquared() < Ray.OFFSET) {
                            double e = Math.abs(emitterRay.d.dot(emitterRay.getN()));
                            e /= Math.max(distance * distance, 1);
                            e *= pos.block.emittance;
                            e *= scene.emitterIntensity;

                            ray.color.x *= emitterRay.color.x * e;
                            ray.color.y *= emitterRay.color.y * e;
                            ray.color.z *= emitterRay.color.z * e;
                        } else {
                            ray.color.set(0, 0, 0, 1);
                        }
                    } else {
                        ray.color.set(0, 0, 0, 1);
                    }

//
//                    ray.color.x *= indirectEmitterColor.x;
//                    ray.color.y *= indirectEmitterColor.y;
//                    ray.color.z *= indirectEmitterColor.z;
                }
            }
        }
    }
}
