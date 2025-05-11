package com.doctordaddysir.core.utils;

import com.doctordaddysir.core.plugins.Plugin;
import com.doctordaddysir.core.proxies.PleXionInvocationHandler;

import java.lang.reflect.Proxy;

import static java.util.Objects.nonNull;

public class ProxyUtils {
    public static boolean isProxy(Object plugin) {
        return plugin != null && Proxy.isProxyClass(plugin.getClass());
    }


    public static Object stripProxy(Object plugin) {
        if (nonNull(plugin) && Proxy.isProxyClass(plugin.getClass())) {
            PleXionInvocationHandler invocationHandler =
                    (PleXionInvocationHandler) Proxy.getInvocationHandler(plugin);
            plugin = invocationHandler.getTarget();
        }
        return plugin;
    }

    public static void executePluginOrProxy(Plugin plugin, Plugin proxy) {
        if (proxy == null) {
            executePluginOrProxy(plugin);
            return;
        }
        try {
            proxy.execute();
        } catch (Exception e) {
            LifeCycleHandler.invokeError(plugin, e);
        }
    }

    public static void executePluginOrProxy(Plugin plugin) {
        try {
            plugin.execute();
        } catch (Exception e) {
            LifeCycleHandler.invokeError(plugin, e);
        }
    }

}
