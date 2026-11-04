package io.quarkiverse.jackson.jq;

/**
 * Reflection helpers.
 */
public final class Utils {

    private Utils() {
    }

    /**
     * Creates an instance with the no-args constructor, loading the class with the context class loader.
     *
     * @param className the fully qualified class name.
     * @param <T> the expected type.
     * @return the new instance.
     * @throws ReflectiveOperationException if the class can't be loaded or instantiated.
     */
    @SuppressWarnings("unchecked")
    public static <T> T createInstance(String className) throws ReflectiveOperationException {
        Class<?> clazz = Class.forName(
                className,
                true,
                Thread.currentThread().getContextClassLoader());
        return (T) clazz.getDeclaredConstructor().newInstance();
    }

    /**
     * Creates an instance with the constructor that has the given parameter types, loading the class with the context
     * class loader.
     *
     * @param className the fully qualified class name.
     * @param parameterTypes the constructor parameter types.
     * @param initargs the constructor arguments.
     * @param <T> the expected type.
     * @return the new instance.
     * @throws ReflectiveOperationException if the class can't be loaded or instantiated.
     */
    @SuppressWarnings("unchecked")
    public static <T> T createInstance(String className, Class<?>[] parameterTypes, Object... initargs)
            throws ReflectiveOperationException {
        Class<?> clazz = Class.forName(
                className,
                true,
                Thread.currentThread().getContextClassLoader());
        return (T) clazz.getDeclaredConstructor(parameterTypes).newInstance(initargs);
    }
}
