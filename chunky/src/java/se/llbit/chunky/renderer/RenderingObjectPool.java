package se.llbit.chunky.renderer;

import se.llbit.math.Ray;
import se.llbit.math.Vector3;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * An object pool to reduce allocation and deallocation of objects during rendering.
 * To obtain an object, call the `get` method for that object. Once done with that object, call `release(object)`.
 */
public class RenderingObjectPool {
    /**
     * A pool for a single object type.
     */
    protected static class ObjectPool<T> {
        private final ArrayList<T> cachedObjects = new ArrayList<>();
        private final Supplier<T> objectFactory;
        private final int maxObjects;

        protected ObjectPool(int maxObjects, Supplier<T> objectFactory) {
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

    /**
     * A wrapper for a double array of length 3. Accessible through `Double3.vec`.
     */
    public static class Double3 {
        public final double[] vec = new double[3];
    }

    protected final ObjectPool<Vector3> vec3 = new ObjectPool<>(64, Vector3::new);
    protected final ObjectPool<Ray> ray = new ObjectPool<>(64, Ray::new);

    protected final ObjectPool<Double3> double3 = new ObjectPool<>(64, Double3::new);

    private final static ThreadLocal<RenderingObjectPool> providerThreadLocal =
        ThreadLocal.withInitial(RenderingObjectPool::new);

    private RenderingObjectPool() {
    }

    protected static RenderingObjectPool get() {
        return providerThreadLocal.get();
    }

    /**
     * @return Vector3
     */
    public static Vector3 getVec3() {
        return get().vec3.get();
    }

    public static void release(Vector3 object) {
        get().vec3.release(object);
    }

    /**
     * @return Ray
     */
    public static Ray getRay() {
        return get().ray.get();
    }

    public static void release(Ray object) {
        get().ray.release(object);
    }

    /**
     * @return A wrapper of a double array of length 3.
     */
    public static Double3 getDouble3() {
        return get().double3.get();
    }

    public static void release(Double3 object) {
        get().double3.release(object);
    }
}
