package org.axway.grapes.server.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class InjectionUtils {

    /**
     * Injects a field value using reflection
     * @param target
     * @param c
     * @param fieldName
     * @param value
     * @param <T>
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static <T> void injectField(final T target,
                                       final Class<T> c,
                                       final String fieldName,
                                       final Object value)
        throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f;

        try {
            f = c.getDeclaredField(fieldName);
        } catch (final NoSuchFieldException e) {
            f = c.getSuperclass().getDeclaredField(fieldName);
        }

        f.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);

        f.set(target, value);
    }

    public static <T, U> U getFieldValue(final T target, final Class<T> targetClass, final Class<U> fieldClass,
                                         final String fieldName) throws SecurityException, NoSuchFieldException,
                                             IllegalArgumentException, IllegalAccessException {

        final Field f = targetClass.getDeclaredField(fieldName);
        f.setAccessible(true);

        final Object result = f.get(target);
        return fieldClass.cast(result);
    }

    public static <T, U> U invokePrivateMethod(final T target, final Class<T> c, final Class<U> resultClass,
                                               final String methodName)
                                                   throws SecurityException, NoSuchMethodException, IllegalArgumentException,
                                                   IllegalAccessException, InvocationTargetException {

        final Method m = c.getDeclaredMethod(methodName);
        m.setAccessible(true);
        final Object result = m.invoke(target);

        return resultClass.cast(result);
    }

    public static <T, U> U invokePrivateMethodWithArguments(final T target, final Class<T> c, final Class<U> resultClass,
                                                            final String methodName, final Class<?>[] argTypes,
                                                            final Object[] args)
                                                                throws RuntimeException, NoSuchMethodException,
                                                                IllegalAccessException, InvocationTargetException {

        final Method m = c.getDeclaredMethod(methodName, argTypes);
        m.setAccessible(true);
        try {
            final Object result = m.invoke(target, args);
            return resultClass.cast(result);
        } catch (final InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                throw e;
            }
        }

    }
}
