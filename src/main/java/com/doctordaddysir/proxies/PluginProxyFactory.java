package com.doctordaddysir.proxies;

import com.doctordaddysir.base.Plugin;

import java.lang.reflect.Proxy;

public class PluginProxyFactory {
    public static Plugin createProxy(Plugin plugin) {
        return (Plugin) Proxy.newProxyInstance(Plugin.class.getClassLoader(), new Class<?>[]{
            Plugin.class
        },new PluginInvocationHandler(plugin));
    }
}
