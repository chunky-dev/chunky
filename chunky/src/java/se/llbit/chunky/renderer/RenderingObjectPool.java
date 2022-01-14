package se.llbit.chunky.renderer;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
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
        private final ArrayDeque<T> cachedObjects;
        private final Supplier<T> objectFactory;
        private final int maxObjects;

        protected ObjectPool(int maxObjects, Supplier<T> objectFactory) {
            cachedObjects = new ArrayDeque<>(maxObjects);
            this.maxObjects = maxObjects;
            this.objectFactory = objectFactory;
        }

        public T get() {
            if (cachedObjects.isEmpty()) {
                return objectFactory.get();
            } else {
                return cachedObjects.removeFirst();
            }
        }

        public void release(T object) {
            if (cachedObjects.size() < maxObjects) {
                cachedObjects.addFirst(object);
            }
        }
    }

    protected static class ThreadLocalObjectPool<T> {
        private final ThreadLocal<ObjectPool<T>> poolThreadLocal;

        public ThreadLocalObjectPool(int maxObjects, Supplier<T> objectFactory) {
            poolThreadLocal = ThreadLocal.withInitial(() -> new ObjectPool<>(maxObjects, objectFactory));
        }

        public T get() {
            return poolThreadLocal.get().get();
        }

        public void release(T object) {
            poolThreadLocal.get().release(object);
        }
    }

    /**
     * A wrapper for a double array of length 3. Accessible through `Double3.vec`.
     */
    public static class Double3 {
        public final double[] vec = new double[3];
    }

    private RenderingObjectPool() {
    }

    protected static final Reference2ObjectOpenHashMap<Class<?>, ThreadLocalObjectPool<?>> poolMap = new Reference2ObjectOpenHashMap<>();
    protected static final int MAX_POOL_OBJECTS = 64;

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<?> cls) {
        ThreadLocalObjectPool<T> pool = (ThreadLocalObjectPool<T>) poolMap.getOrDefault(cls, null);
        if (pool == null) {
            synchronized (poolMap) {
                pool = (ThreadLocalObjectPool<T>) poolMap.getOrDefault(cls, null);
                if (pool == null) {
                    try {
                        Constructor<?> constructor = cls.getConstructor();
                        pool = new ThreadLocalObjectPool<>(MAX_POOL_OBJECTS, () -> {
                            try {
                                return (T) constructor.newInstance();
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                String message = String.format("Failed to create object %s. Constructor %s.", cls, constructor);
                                throw new RuntimeException(message, e);
                            }
                        });
                        poolMap.put(cls, pool);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(String.format("Could not obtain constructor for class %s", cls), e);
                    }
                }
            }
        }
        return pool.get();
    }

    @SuppressWarnings("unchecked")
    public static <T> void release(T object) {
        ThreadLocalObjectPool<T> pool = (ThreadLocalObjectPool<T>) poolMap.getOrDefault(object.getClass(), null);
        if (pool != null) {
            pool.release(object);
        }
    }
}
