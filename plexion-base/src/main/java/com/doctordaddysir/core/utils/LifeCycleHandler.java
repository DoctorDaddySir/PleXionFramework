package com.doctordaddysir.core.utils;

import com.doctordaddysir.core.annotations.OnDestroy;
import com.doctordaddysir.core.annotations.OnError;
import com.doctordaddysir.core.annotations.OnLoad;
import com.doctordaddysir.core.cache.ReflectionCache;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

import static com.doctordaddysir.core.utils.reflection.MethodInvoker.invokeNoArgAnnotatedMethod;
import static com.doctordaddysir.core.utils.ProxyUtils.stripProxy;

@Slf4j
public class LifeCycleHandler {
    public static void invokeLoad(Object plugin) {
        log.debug("Invoking @OnLoad on plugin: {}", plugin.getClass().getName());
        invokeNoArgAnnotatedMethod(plugin, OnLoad.class);
    }

    public static void invokeDestroy(Object plugin) {
        log.debug("Invoking @OnDestroy on plugin: {}", plugin.getClass().getName());
        invokeNoArgAnnotatedMethod(plugin, OnDestroy.class);
    }

    public static void invokeError(Object plugin, Throwable t) {
        log.debug("Invoking @OnError on plugin: {}", plugin.getClass().getName());

        plugin = stripProxy(plugin);

        for (Method method : ReflectionCache.getMethods(plugin.getClass())) {
            if (method.isAnnotationPresent(OnError.class) &&
                    method.getParameterCount() == 1 &&
                    method.getParameterTypes()[0].equals(Throwable.class)) {

                try {
                    method.invoke(plugin, t);
                } catch (Exception e) {
                    log.error("Failed to invoke @OnError: {}", e.getMessage());
                }
            }
        }

    }


}
