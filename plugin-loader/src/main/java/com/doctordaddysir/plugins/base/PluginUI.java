package com.doctordaddysir.plugins.base;

import com.doctordaddysir.annotations.AnnotationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class PluginUI<T> {
    private Boolean isDebugMode = false;
    private List<Class<?>> plugins = new ArrayList<>();
    private final Map<String, Plugin> instantiatedPlugins = new HashMap<>();
    private final Map<String, ClassLoader> classLoaders = new HashMap<>();
    private final Map<String, Plugin> proxies = new HashMap<>();


    public abstract void out(String message);
    public void registerPlugin(String fullyQualifiedClassName) {
        try {
            Class<?> clazz = Class.forName(fullyQualifiedClassName);
            if (Plugin.class.isAssignableFrom(clazz)) {
                plugins.add(clazz);
                classLoaders.put(fullyQualifiedClassName, clazz.getClassLoader());
                System.out.println(AnnotationUtils.readPluginInfo(clazz));

            } else {
                System.out.println("Class does not implement PluginInterface");//validation
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Plugin class not found: " + fullyQualifiedClassName);
        }

    }

    public void registerPlugin(Class<?> clazz) {
        if (Plugin.class.isAssignableFrom(clazz)) {
            plugins.add(clazz);
            classLoaders.put(clazz.getName(), clazz.getClassLoader());
            System.out.println(AnnotationUtils.readPluginInfo(clazz));

        } else {
            System.out.println("Class does not implement PluginInterface");
        }


    }
    public abstract void start();
    public abstract T setDebugMode(Boolean debugMode);

}
