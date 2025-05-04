package com.doctordaddysir.ui;

import com.doctordaddysir.plugins.Plugin;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@SuperBuilder
public abstract class PluginUI {
    Boolean isDebugMode;
    List<Class<?>> plugins;
    Map<String, Plugin> instantiatedPlugins;
    Map<String, ClassLoader> classLoaders;
    Map<String, Plugin> proxies;


    public abstract void out(String message);

    public abstract void registerPlugin(Class<?> clazz);
    public abstract void start();
    public abstract void start(Boolean debugMode);
    public abstract void setDebugMode(Boolean debugMode);

}
