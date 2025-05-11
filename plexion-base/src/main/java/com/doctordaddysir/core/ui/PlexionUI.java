package com.doctordaddysir.core.ui;

import com.doctordaddysir.core.plugins.Plugin;
import com.doctordaddysir.core.plugins.loaders.PluginLoader;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@NoArgsConstructor
public abstract class PlexionUI {
    Boolean isDebugMode;
    List<Class<?>> plugins;
    Map<String, Plugin> instantiatedPlugins;
    Map<String, ClassLoader> classLoaders;
    Map<String, Plugin> proxies;


    public abstract void out(String message);

    public abstract void registerPlugin(Class<?> clazz);

    public abstract void start(Boolean debugMode, PluginLoader loader);

    public abstract void setDebugMode(Boolean debugMode);

}
