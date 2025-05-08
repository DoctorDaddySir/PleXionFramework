package com.doctordaddysir.annotations.handlers;

import com.doctordaddysir.annotations.Inject;
import com.doctordaddysir.annotations.Injectable;
import com.doctordaddysir.exceptions.InvalidBeanException;
import com.doctordaddysir.exceptions.InvalidFieldExcepton;
import com.doctordaddysir.utils.FieldUtils;
import com.doctordaddysir.utils.ReflectionUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.doctordaddysir.utils.ReflectionUtils.isAbstract;

@Slf4j
public class InjectionUtils {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class ConstructorInjector{
        private Constructor<?> constructor;
        private Object[] parameters;
        private Class<?>[] parameterTypes;
        private BeanCollector beanCollector;

        public Object invoke() throws InvocationTargetException, InstantiationException
                , IllegalAccessException, InvalidBeanException {
            constructor.setAccessible(true);
            return constructor.newInstance(parameters);
        }
    }

    private static final Map<Class<?>, Object> defaultValues = Map.of(
            int.class, 0,
            boolean.class, false,
            double.class, 0.0,
            long.class, 0L,
            float.class, 0f,
            short.class, 0,
            byte.class, (byte) 0,
            char.class, '\0'
    );

    public static Object getPrimitiveParameter(Class<?> type){
        return defaultValues.getOrDefault(type, null);
    }


    public static void injectField(Object instance, Field field, Class<?> value,
                                   BeanCollector beanCollector) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, NoSuchFieldException, ClassNotFoundException,InvalidBeanException {
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

    public static Object injectFields(Object instance, BeanCollector beanCollector) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, NoSuchFieldException, ClassNotFoundException, InvalidFieldExcepton, InvalidBeanException {
        if (instance == null) {
            log.debug("Instance is null. Unable to inject");
            return null;
        }
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                injectField(instance, field, ReflectionUtils.findClassForField(field),
                        beanCollector);
            }
        }
        return instance;
    }

    public static Constructor<?> getConstructorForInjection(Class<?> clazz) throws InvalidBeanException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (clazz == null) {
            throw new InvalidBeanException("Class is null");
        }
        if (clazz.isInterface()) {
            clazz = (Class<?>) Objects.requireNonNull(ReflectionUtils.findImplementation(clazz)).getDeclaredConstructor().newInstance();
        }
        if (isAbstract(clazz)) {
            clazz = ReflectionUtils.findInjectableClassforAbstractOrInterfaceClass(clazz);
        }


        List<Constructor<?>> injectableConstructors = getInjectableConstructors(clazz);
        if (injectableConstructors.isEmpty()) {
            throw new InvalidBeanException("No injectable constructors found");
        }
        if (injectableConstructors.size() > 1) {
            throw new InvalidBeanException("Multiple injectable constructors found");
        }


        return injectableConstructors.getFirst();
    }

    public static Object injectConstructor(Class<?> clazz, BeanCollector beanCollector) throws InvocationTargetException, InstantiationException, IllegalAccessException, InvalidBeanException, IOException, ClassNotFoundException, NoSuchMethodException {
        if (beanCollector.containsBeanInstance(clazz)) {
            return beanCollector.getBeanInstance(clazz.getCanonicalName());
        };
        ;
        return beanCollector.getOrCreateBeanInstance(clazz);
    }

    private static void injectMethod(Object instance, BeanCollector beanCollector) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, NoSuchFieldException, ClassNotFoundException, InvalidFieldExcepton, InvalidBeanException {
    }

    public static boolean hasInjectableFields(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                return true;
            }
        }
        return false;
    }


    public static List<Constructor<?>> getInjectableConstructors(Class<?> clazz) throws NoSuchMethodException {
        List<Constructor<?>> injectableConstructors = new ArrayList<>();
        Boolean hasNoArgConstructor = false;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                hasNoArgConstructor = true;
            }
            if (constructor.isAnnotationPresent(Injectable.class)) {
                if (hasInjectableParameters(constructor)) {
                    injectableConstructors.add(constructor);
                }
            }
        }
        if (injectableConstructors.isEmpty() && hasNoArgConstructor) {
            return List.of(clazz.getDeclaredConstructor());
        }
        return injectableConstructors;
    }

    private static boolean hasInjectableParameters(Constructor<?> constructor) {
        for (Parameter parameter : constructor.getParameters()) {
            if (parameter.isAnnotationPresent(Inject.class)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasInjectableMethods(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Injectable.class)) {
                if (hasInjectableParameters(method)) {
                    return true;

                }
            }
        }
        return false;
    }

    private static boolean hasInjectableParameters(Method method) {
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(Inject.class)) {
                return true;
            }
        }
        return false;
    }
}
