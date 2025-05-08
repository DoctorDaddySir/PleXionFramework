package com.doctordaddysir.cache;

import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@Getter
public class ReflectionCache {
    private static final Map<Class<?>, Constructor<?>[]> constructorCache =
            new ConcurrentHashMap<>();
    private static final Map<Class<?>, Field[]> fieldCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Method[]> methodCache = new ConcurrentHashMap<>();

    public static Field[] getFields(Class<?> clazz) {
        Field[] fields = fieldCache.get(clazz);
        if (isNull(fields)) {
            fields = clazz.getDeclaredFields();
            for (Field field : fields) field.setAccessible(true);
            fieldCache.put(clazz, fields);
            return fields;
        }
        return fields;
    }

    public static Constructor<?>[] getConstructors(Class<?> clazz) {
        Constructor<?>[] constructors = constructorCache.get(clazz);
        if (isNull(constructors)) {
            constructors = clazz.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors)
                constructor.setAccessible(true);
            constructorCache.put(clazz, constructors);
            return constructors;
        }
        return constructors;
    }

    public static Method[] getMethods(Class<?> clazz) {
        Method[] methods = methodCache.get(clazz);
        if (isNull(methods)) {
            methods = clazz.getDeclaredMethods();
            for (Method method : methods) method.setAccessible(true);
            methodCache.put(clazz, methods);
            return methods;
        }
        return methods;
    }

}
