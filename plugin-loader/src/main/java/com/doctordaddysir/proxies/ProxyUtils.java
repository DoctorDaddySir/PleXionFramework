package com.doctordaddysir.proxies;

import com.doctordaddysir.base.Plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import static java.util.Objects.nonNull;

public class ProxyUtils {
    public static boolean isProxy(Object plugin) {
        return plugin != null && Proxy.isProxyClass(plugin.getClass());
    }

    public static Plugin getTarget(Object plugin) {
        if (isProxy(plugin)) {
            InvocationHandler handler = Proxy.getInvocationHandler(plugin);
            if (handler instanceof PluginInvocationHandler ph) {
                return ph.getTarget();
            }
        }
        return (Plugin) plugin;
    }

    public static Object stripProxy(Object plugin) {
        if (nonNull(plugin) && Proxy.isProxyClass(plugin.getClass())) {
            PluginInvocationHandler invocationHandler = (PluginInvocationHandler) Proxy.getInvocationHandler(plugin);
            plugin = invocationHandler.getTarget();
        }
        return plugin;
    }
}
