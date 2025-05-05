package com.doctordaddysir.annotations.handlers;

import com.doctordaddysir.annotations.Inject;
import com.doctordaddysir.exceptions.InvalidBeanException;
import com.doctordaddysir.exceptions.InvalidFieldExcepton;
import com.doctordaddysir.utils.FieldUtils;
import com.doctordaddysir.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class InjectionUtils {

    public static void inject(Object instance, Field field, Class<?> value, BeanCollector beanCollector) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, NoSuchFieldException, ClassNotFoundException, InvalidFieldExcepton, InvalidBeanException {
        if (value == null) {
            log.debug("Value is null. Unable to inject");
            return;
        }
        if (instance == null) {
            log.debug("Instance is null. Unable to inject");
            return;
        }
        Object injectionValue = beanCollector.getOrCreateBeanInstance(value);
        FieldUtils.injectField(instance,field,injectionValue);
    }

    public static void inject(Object instance, BeanCollector beanCollector) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, NoSuchFieldException, ClassNotFoundException, InvalidFieldExcepton, InvalidBeanException {
        if (instance == null) {
            log.debug("Instance is null. Unable to inject");
            return;
        }
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                inject(instance, field, ReflectionUtils.findClassForField(field), beanCollector);
            }
        }
    }

    public static boolean hasInjectables(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                return true;
            }
        }
        return false;
    }
}
