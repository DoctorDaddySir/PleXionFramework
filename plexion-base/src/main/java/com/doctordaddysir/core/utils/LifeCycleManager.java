package com.doctordaddysir.core.utils;

import com.doctordaddysir.core.reflection.annotations.OnDestroy;
import com.doctordaddysir.core.reflection.annotations.OnError;
import com.doctordaddysir.core.reflection.annotations.OnLoad;
import com.doctordaddysir.core.cache.ReflectionCache;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

import static com.doctordaddysir.core.utils.reflection.MethodInvoker.invokeNoArgAnnotatedMethod;
import static com.doctordaddysir.core.utils.ProxyUtils.stripProxy;

@Slf4j
public class LifeCycleManager {
    public static void invokeLoad(Object instance) {
        invokeNoArgAnnotatedMethod(instance, OnLoad.class);
        instance= stripProxy(instance);
        log.debug("Invoking @OnLoad on instance: {}", instance.getClass().getName());
    }

    public static void invokeDestroy(Object instance) {
        invokeNoArgAnnotatedMethod(instance, OnDestroy.class);
        instance= stripProxy(instance);
        log.debug("Invoking @OnDestroy on instance: {}", instance.getClass().getName());
    }

    public static void invokeError(Object instance, Throwable t) {
        instance = stripProxy(instance);
        log.debug("Invoking @OnError on instance: {}", instance.getClass().getName());
        log.error("Error occurred: {}", t.getMessage());

        for (Method method : ReflectionCache.getMethods(instance.getClass())) {
            if (method.isAnnotationPresent(OnError.class) &&
                    method.getParameterCount() == 1 &&
                    method.getParameterTypes()[0].equals(Throwable.class)) {

                try {
                    method.invoke(instance, t);
                } catch (Exception e) {
                    log.error("Failed to invoke @OnError: {}", e.getMessage());
                }
            }
        }

    }


}
