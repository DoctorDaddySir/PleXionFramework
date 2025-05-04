package com.doctordaddysir.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.doctordaddysir.utils.PluginProxyUtils.stripProxy;
@Slf4j
public class MethodInvoker {

    public static void invokeNoArgMethod(Object plugin, String methodName) {
        plugin = stripProxy(plugin);
        for (Method method : plugin.getClass().getDeclaredMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == 0) {
                try {
                    method.setAccessible(true);
                    method.invoke(plugin);
                } catch (Exception e) {
                    log.error("Failed to invoke {}: {}", methodName, e.getMessage());
                }
            }
        }
    }

    public static void invokeNoArgAnnotatedMethod(Object plugin, Class<? extends Annotation> annotation) {

        plugin = stripProxy(plugin);


        for (Method method : plugin.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation) && method.getParameterCount() == 0) {
                try {
                    method.setAccessible(true);
                    method.invoke(plugin);
                } catch (Exception e) {
                    log.error("Failed to invoke {}: {}", annotation.getSimpleName(), e.getMessage());
                }
            }
        }
    }
}
