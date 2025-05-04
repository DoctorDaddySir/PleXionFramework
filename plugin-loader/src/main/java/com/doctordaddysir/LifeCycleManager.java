package com.doctordaddysir;

import com.doctordaddysir.annotations.OnDestroy;
import com.doctordaddysir.annotations.OnError;
import com.doctordaddysir.annotations.OnLoad;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.doctordaddysir.proxies.PluginProxyUtils.stripProxy;

@Slf4j
public class LifeCycleManager {
    public static void invokeLoad(Object plugin) {
        invokeNoArgAnnotatedMethod(plugin, OnLoad.class);
    }
    public static void invokeDestroy(Object plugin) {
        invokeNoArgAnnotatedMethod(plugin, OnDestroy.class);
    }



    public static void invokeError(Object plugin, Throwable t){

        plugin = stripProxy(plugin);

        for (Method method : plugin.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(OnError.class) &&
                    method.getParameterCount() == 1 &&
                    method.getParameterTypes()[0].equals(Throwable.class)) {

                try {
                    method.setAccessible(true);
                    method.invoke(plugin, t);
                } catch (Exception e) {
                    log.error("Failed to invoke @OnError: {}", e.getMessage());
                }
            }
        }
        
    }


    private static void invokeNoArgAnnotatedMethod(Object plugin, Class<? extends Annotation> annotation) {

        plugin = stripProxy(plugin);


        for (Method method : plugin.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation) && method.getParameterCount() == 0) {
                try {
                    method.setAccessible(true);
                    method.invoke(plugin);
                } catch (Exception e) {
                    log.error("Failed to invoke @OnDestroy: {}", e.getMessage());
                }
            }
        }
    }
}
