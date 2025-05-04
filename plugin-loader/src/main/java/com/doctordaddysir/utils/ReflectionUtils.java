package com.doctordaddysir.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ReflectionUtils {
    public static Class<?> getClass(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
    public static Object newInstance(Constructor<?> constructor, Object[] args ) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (constructor == null) {
            return null;
        }
        if (args == null) {
            args = new Object[0];
        }
        return constructor.newInstance(args);
    }
    public static Object newInstance(Class<?> clazz, Object[] args ) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        if (clazz == null) {
            return null;
        }
        if (args == null) {
            return clazz.getDeclaredConstructor().newInstance(args);
        }
        Object instance;
        final Constructor<?>[] constructor = {null};
        Arrays.stream(clazz.getDeclaredConstructors())
                .forEach(c -> {
                    boolean build = true;
                    Class<?>[] parameterTypes = c.getParameterTypes();
                    if (parameterTypes.length == args.length) {
                    for (int i = 0; i < parameterTypes.length; i++) {
                        if (!parameterTypes[i].isAssignableFrom(args[i].getClass())) {
                            build =  false;
                        }
                    }
                }
                    if(build){
                        constructor[0] =c;
                    }
                });

        if (constructor[0] != null) {
            return constructor[0].newInstance(args);
        }
        throw new NoSuchMethodException("No constructor found for " + clazz.getName() + " with " + args.length + " arguments");

    }
}

