package com.doctordaddysir.utils;


import com.doctordaddysir.annotations.Bean;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
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

    public static Boolean hasInterface(Class<?> clazz, Class<?> intr)  {
        if(intr.isAssignableFrom(clazz)){
            return true;
        }
        return false;
    }

    public static Boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        if(clazz.isAnnotationPresent(annotation)){
            return true;
        }
        return false;
    }

    public static void setAccessible(Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof Field) {
            ((Field) obj).setAccessible(true);
            log.debug("Field {} set accessible", ((Field) obj).getName());
        }
        if (obj instanceof Constructor) {
            ((Constructor) obj).setAccessible(true);
            log.debug("Constructor {} set accessible", ((Constructor) obj).getName());
        }
        if (obj instanceof Method) {
            ((Method) obj).setAccessible(true);
            log.debug("Method {} set accessible", ((Method) obj).getName());
        }

    }

    public static Class<?> findImplementation(Class<?> intr) {
        if (!intr.isInterface()) {
            return null;
        }
        Class<?>[] declaredClasses = intr.getDeclaredClasses();
        Class<?> clazz = null;
        if (declaredClasses.length > 0) {
            return null;
        }
        for (Class<?> c : declaredClasses) {
            if (clazz.isInterface()) {
                findImplementation(clazz);
            }
            clazz = c;
        }
        return clazz;
    }

    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    public static Class<?> findClassForField(Field field) throws IOException, ClassNotFoundException {
        Class<?> clazz = field.getType();
        if (clazz.isPrimitive()) {
            return clazz;
        }
        return findConcreteClassForInjection(field);
    }

    private static Class<?> findConcreteClassForInjection(Field field) throws IOException, ClassNotFoundException {
        return findInjectableClassforAbstractOrInterfaceClass(field.getType());

    }

    public static Class<?> findInjectableClassforAbstractOrInterfaceClass(Class<?> clazz) throws IOException, ClassNotFoundException {
        Class<?> resultClass = null;
        if (clazz.isPrimitive()) {
            return clazz;
        }
        if (clazz.isInterface()) {
            resultClass = findImplementation(clazz);
        }
        if (isAbstract(clazz)) {
            List<Class<?>> collect = AnnotationScanner.findClassesWithAnnotation(Bean.class, "com.doctordaddysir").stream()
                    .filter(clazz::isAssignableFrom)
                    .filter(c -> !Modifier.isAbstract(c.getModifiers())).toList();
            return collect.getFirst();
        }
        return clazz;
    }
}

