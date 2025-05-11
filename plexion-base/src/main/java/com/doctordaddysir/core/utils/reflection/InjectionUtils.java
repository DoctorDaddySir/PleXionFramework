package com.doctordaddysir.core.utils.reflection;

import com.doctordaddysir.core.reflection.annotations.Inject;
import com.doctordaddysir.core.reflection.annotations.Injectable;
import com.doctordaddysir.BeanCollector;
import com.doctordaddysir.core.cache.ReflectionCache;
import com.doctordaddysir.core.exceptions.DepndencyInjectionException;
import com.doctordaddysir.core.exceptions.InvalidBeanException;
import com.doctordaddysir.core.exceptions.InvalidFieldExcepton;
import com.doctordaddysir.core.utils.FieldUtils;
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

import static com.doctordaddysir.core.utils.reflection.ReflectionUtils.ReflectionClassError.NO_SUCH_CLASS;
import static com.doctordaddysir.core.utils.reflection.ReflectionUtils.ReflectionFieldError.NO_SUCH_FIELD;
import static com.doctordaddysir.core.utils.reflection.ReflectionUtils.isAbstract;

@Slf4j
public class InjectionUtils {

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

    public static Object getPrimitiveParameter(Class<?> type) {
        return defaultValues.getOrDefault(type, null);
    }

    public static void injectField(Object instance, Field field, Class<?> value,
                                   BeanCollector beanCollector) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, NoSuchFieldException, ClassNotFoundException, InvalidBeanException, DepndencyInjectionException {
        if (value == null) {
            log.debug("Value is null. Unable to inject");
            return;
        }
        if (instance == null) {
            log.debug("Instance is null. Unable to inject");
            return;
        }
        Object injectionValue = beanCollector.getOrCreateBeanInstance(value);
        FieldUtils.injectField(instance, field, injectionValue);
    }

    public static Object injectFields(Object instance, BeanCollector beanCollector) throws InvalidFieldExcepton, IOException, InvalidBeanException, DepndencyInjectionException {
        if (instance == null) {
            log.debug("Instance is null. Unable to inject");
            return null;
        }
        for (Field field : ReflectionCache.getFields(instance.getClass())) {
            if (field.isAnnotationPresent(Inject.class)) {
                try {
                    injectField(instance, field, ReflectionUtils.findClassForField(field),
                            beanCollector);
                } catch (NoSuchMethodException e) {
                    log.error("Unable to inject field", e);
                } catch (InvocationTargetException e) {
                    throw new DepndencyInjectionException(ReflectionUtils.ReflectionDIError.INVOCATION_TARGET_EXCEPTION, e.getMessage());
                } catch (InstantiationException e) {
                    throw new DepndencyInjectionException(ReflectionUtils.ReflectionDIError.INSTANTIATION_EXCEPTION, e.getMessage());
                } catch (IllegalAccessException e) {
                    throw new DepndencyInjectionException(ReflectionUtils.ReflectionDIError.ILLEGAL_ACCESS_EXCEPTION, e.getMessage());
                } catch (NoSuchFieldException e) {
                    throw new InvalidFieldExcepton(NO_SUCH_FIELD, field);
                } catch (ClassNotFoundException e) {
                    throw new InvalidBeanException(NO_SUCH_CLASS, e.getMessage());
                }
            }
        }
        return instance;
    }

    public static Constructor<?> getConstructorForInjection(Class<?> clazz) throws InvalidBeanException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (clazz == null) {
            throw new InvalidBeanException(NO_SUCH_CLASS, "Class is null");
        }
        if (clazz.isInterface()) {
            clazz = (Class<?>) Objects.requireNonNull(ReflectionUtils.findImplementation(clazz)).getDeclaredConstructor().newInstance();
        }
        if (isAbstract(clazz)) {
            clazz = ReflectionUtils.findInjectableClassforAbstractOrInterfaceClass(clazz);
        }


        List<Constructor<?>> injectableConstructors = getInjectableConstructors(clazz);
        if (injectableConstructors.isEmpty()) {
            throw new InvalidBeanException(ReflectionUtils.ReflectionClassError.NO_IMPLEMENTATION, "No injectable constructors found for class " + clazz.getCanonicalName() + " with @Injectable annotation");
        }
        if (injectableConstructors.size() > 1) {
            throw new InvalidBeanException(ReflectionUtils.ReflectionClassError.MULTIPLE_IMPLEMENTATIONS, "Too many constructors annotated with @Injectable");
        }


        return injectableConstructors.getFirst();
    }

    private static void injectMethod(Object instance, BeanCollector beanCollector) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, NoSuchFieldException, ClassNotFoundException, InvalidFieldExcepton, InvalidBeanException {
    }

    public static boolean hasInjectableFields(Class<?> clazz) {
        for (Field field : ReflectionCache.getFields(clazz)) {
            if (field.isAnnotationPresent(Inject.class)) {
                return true;
            }
        }
        return false;
    }

    public static List<Constructor<?>> getInjectableConstructors(Class<?> clazz) throws NoSuchMethodException {
        List<Constructor<?>> injectableConstructors = new ArrayList<>();
        for (Constructor<?> constructor : ReflectionCache.getConstructors(clazz)) {
            if (constructor.isAnnotationPresent(Injectable.class)) {
                if (hasInjectableParameters(constructor)) {
                    injectableConstructors.add(constructor);
                }
            }
        }
        if (injectableConstructors.isEmpty()) {
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
        for (Method method : ReflectionCache.getMethods(clazz)) {
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

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    public static class ConstructorInjector {
        private Constructor<?> constructor;
        private Object[] parameters;
        private Class<?>[] parameterTypes;
        private BeanCollector beanCollector;

        public Object invoke() throws InvocationTargetException, InstantiationException
                , IllegalAccessException, InvalidBeanException {
            return constructor.newInstance(parameters);
        }
    }
}
