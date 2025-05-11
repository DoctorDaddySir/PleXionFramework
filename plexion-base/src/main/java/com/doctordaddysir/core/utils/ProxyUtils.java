package com.doctordaddysir.core.utils;

import com.doctordaddysir.core.proxies.PleXionInvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static java.util.Objects.nonNull;

public class ProxyUtils {


    public static void executeNoArgMethodOnInstanceOrProxy(Object instance, Method method) {
        if (instance == null || method == null) {
            return;
        }
        instance = stripProxy(instance);
        try {
            instance.getClass().getDeclaredMethod(method.getName()).invoke(instance);
        } catch (Exception e) {
            LifeCycleManager.invokeError(instance, e);
        }
    }
    public static Object stripProxy(Object instance) {
        if (nonNull(instance) && Proxy.isProxyClass(instance.getClass())) {
            PleXionInvocationHandler invocationHandler =
                    (PleXionInvocationHandler) Proxy.getInvocationHandler(instance);
            instance = invocationHandler.getTarget();
        }
        return instance;
    }


    public static boolean isProxy(Object plugin) {
        return plugin != null && Proxy.isProxyClass(plugin.getClass());
    }

}
