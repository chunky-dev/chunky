package se.llbit.chunky.renderer;

import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.ArrayList;
import java.util.function.Supplier;

public class CachedObjectProvider {
    public static class ObjectProvider<T> {
        private final ArrayList<T> cachedObjects = new ArrayList<>();
        private final Supplier<T> objectFactory;
        private final int maxObjects;

        protected ObjectProvider(int maxObjects, Supplier<T> objectFactory) {
            cachedObjects.ensureCapacity(maxObjects);
            this.maxObjects = maxObjects;
            this.objectFactory = objectFactory;
        }

        public T get() {
            if (cachedObjects.isEmpty()) {
                return objectFactory.get();
            } else {
                return cachedObjects.remove(cachedObjects.size()-1);
            }
        }

        public void release(T object) {
            if (cachedObjects.size() < maxObjects) {
                cachedObjects.add(object);
            }
        }
    }

    public static class Double3 {
        public final double[] vec = new double[3];
    }

    protected final ObjectProvider<Vector3> vec3 = new ObjectProvider<>(64, Vector3::new);
    protected final ObjectProvider<Ray> ray = new ObjectProvider<>(64, Ray::new);

    protected final ObjectProvider<Double3> double3 = new ObjectProvider<>(64, Double3::new);

    private final static ThreadLocal<CachedObjectProvider> providerThreadLocal =
        ThreadLocal.withInitial(CachedObjectProvider::new);

    private CachedObjectProvider() {
    }

    public static CachedObjectProvider get() {
        return providerThreadLocal.get();
    }

    public static Vector3 getVec3() {
        return get().vec3.get();
    }

    public static void release(Vector3 object) {
        get().vec3.release(object);
    }

    public static Ray getRay() {
        return get().ray.get();
    }

    public static void release(Ray object) {
        get().ray.release(object);
    }

    public static Double3 getDouble3() {
        return get().double3.get();
    }

    public static void release(Double3 object) {
        get().double3.release(object);
    }
}
