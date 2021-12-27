package se.llbit.chunky.renderer.scene;

import se.llbit.chunky.renderer.CachedObjectProvider;
import se.llbit.chunky.renderer.EmitterSamplingStrategy;
import se.llbit.chunky.renderer.WorkerState;
import se.llbit.math.Grid;
import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.Random;

public class DebugPathTracer implements RayTracer {
    @Override
    public void trace(Scene scene, WorkerState state) {
        Ray ray = state.ray;
        Random rand = state.random;
        Vector3 emitterContribution = CachedObjectProvider.getVec3();
        emitterContribution.set(0, 0, 0);
        Vector3 target = CachedObjectProvider.getVec3();
        Ray emitterRay = CachedObjectProvider.getRay();
        ray.color.set(0, 0, 0, 1);

        if (PreviewRayTracer.nextIntersection(scene, ray)) {
            if (scene.getEmitterSamplingStrategy() != EmitterSamplingStrategy.NONE) {
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

                double invFaces = 1.0 / pos.block.numFaces();
                for (int i = 0; i < pos.block.numFaces(); i++) {
                    pos.sampleFace(i, target, rand);

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
                            e *= invFaces;

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

        CachedObjectProvider.release(emitterContribution);
        CachedObjectProvider.release(target);
        CachedObjectProvider.release(emitterRay);
    }
}
