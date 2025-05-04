package com.doctordaddysir.proxies;

import com.doctordaddysir.LifeCycleManager;
import com.doctordaddysir.plugins.base.Plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import static java.util.Objects.nonNull;

public class PluginProxyUtils {
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
    public static void executePluginOrProxy(Plugin plugin, Plugin proxy) {
        if(proxy == null) {
            executePluginOrProxy(plugin);
            return;
        }
        try {
            proxy.execute();
        }catch (Exception e) {
            LifeCycleManager.invokeError(plugin, e);
        }
    }

    public static void executePluginOrProxy(Plugin plugin) {
        try {
            plugin.execute();
        }catch (Exception e) {
            LifeCycleManager.invokeError(plugin, e);
        }
    }

}
