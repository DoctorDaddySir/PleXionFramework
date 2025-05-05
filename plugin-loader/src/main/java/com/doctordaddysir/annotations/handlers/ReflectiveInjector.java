package com.doctordaddysir.annotations.handlers;

import com.doctordaddysir.annotations.Bean;
import com.doctordaddysir.annotations.Inject;
import com.doctordaddysir.exceptions.InvalidFieldExcepton;
import com.doctordaddysir.utils.AnnotationScanner;
import com.doctordaddysir.utils.FieldUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ReflectiveInjector {

    private static final Class<? extends Annotation> INJECT_ANNOTATION = Inject.class;
    private static final Class<? extends Annotation> INTECTABLE_ANNOTATION = Bean.class;
    private static final String BASE_PACKAGE = "com.doctordaddysir";


    public static Set<Field> findInjectableFields(Object obj) throws IOException, ClassNotFoundException {
        Set<Field> fields = new HashSet<>();
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(INJECT_ANNOTATION)) {
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
    public static Set<Method> findInjectableMethods(Object obj) throws IOException, ClassNotFoundException {
        Set<Method> methods = new HashSet<>();
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(INJECT_ANNOTATION)) {
                    methods.add(method);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return methods;
    }
    public static Set<Constructor> findInjectableConstructors(Object obj) throws IOException, ClassNotFoundException {
        Set<Constructor> constructors = new HashSet<>();
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            for (Constructor constructor : clazz.getDeclaredConstructors()) {
                if (constructor.isAnnotationPresent(INJECT_ANNOTATION)) {
                    constructors.add(constructor);
                }
            }
        }
        return constructors;
    }

    private static void injectField(Object instance, Field field, Object value) throws InvalidFieldExcepton {
        try {
            FieldUtils.injectField(instance, field, value.getClass());
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException | InstantiationException | IOException |
                 ClassNotFoundException e) {
            throw new InvalidFieldExcepton(FieldUtils.FieldError.NO_SUCH_FIELD);
        }
    }
    private static void injectMethod(Method method, Object value)  {

    }
    private static  void injectConstructor(Field field, Object value) {

    }

    public static void inject(Object instance)  {

    }

}
