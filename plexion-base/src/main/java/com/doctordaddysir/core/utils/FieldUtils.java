package com.doctordaddysir.core.utils;

import com.doctordaddysir.core.cache.ReflectionCache;
import com.doctordaddysir.core.utils.reflection.ReflectionUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.nonNull;

public class FieldUtils {

    public static Field injectField(Object target, Field field, Object value) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException, IOException, ClassNotFoundException {
        if (nonNull(value)) {
            if (value.getClass().isInterface()) {
                value = Objects.requireNonNull(ReflectionUtils.findImplementation(value.getClass())).getDeclaredConstructor().newInstance();
            }
            if (ReflectionUtils.isAbstract(value.getClass())) {
                value = ReflectionUtils.findClassForField(field);
            }
        }
        field.set(target, value);
        return field;

    }

    public static Object getFieldValue(Object target, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        return field.get(target);
    }


    public static Field getFieldRecursive(Class<?> clazz, String name) {
        while (clazz != null) {
            try {
                Field f = clazz.getDeclaredField(name);
                return f;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }


    public static Set<Field> getAnnotatedFieldsRecursive(Class<?> clazz, Class<?
            extends Annotation> annotation) {
        Set<Field> fields = new HashSet<>();
        while (clazz != null) {
            for (Field field : ReflectionCache.getFields(clazz)) {
                if (field.isAnnotationPresent(annotation)) {
                    fields.add(field);
                }
                ;
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }


}
