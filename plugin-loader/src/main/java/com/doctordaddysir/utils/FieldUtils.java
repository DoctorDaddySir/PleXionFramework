package com.doctordaddysir.utils;

import com.doctordaddysir.exceptions.InvalidFieldExcepton;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FieldUtils {

    public static Field injectField(Object target, Field field, Object value) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException, IOException, ClassNotFoundException {
        if(value.getClass().isInterface()){
            value = (Class<?>) Objects.requireNonNull(ReflectionUtils.findImplementation(value.getClass())).getDeclaredConstructor().newInstance();
        }
        if(ReflectionUtils.isAbstract(value.getClass())){
           value=  ReflectionUtils.findClassForField(field);
        }

        ReflectionUtils.setAccessible(field);
        field.set(target, value);
        return field;

    }
    public static Object getFieldValue(Object target, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        ReflectionUtils.setAccessible(field);
        return field.get(target);
    }


    public static Field getFieldRecursive(Class<?> clazz, String name) {
        while (clazz != null) {
            try {
                Field f = clazz.getDeclaredField(name);
                ReflectionUtils.setAccessible(f);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }


    public static Set<Field> getAnnotatedFieldsRecursive(Class<?> clazz, Class<? extends Annotation> annotation) {
        Set<Field> fields = new HashSet<>();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotation)) {
                    fields.add(field);
                };
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }


    public  enum FieldError{
        NO_SUCH_FIELD,
        INACCESSIBLE_FIELD,
        INACCESSIBLE_CLASS,
        WRONG_TYPE;

    }

}
