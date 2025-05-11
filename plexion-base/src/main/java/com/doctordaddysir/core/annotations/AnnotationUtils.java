package com.doctordaddysir.core.annotations;

public class AnnotationUtils {
    public static String readPluginInfo(Class<?> clazz) {

        PluginInfo info = clazz.getAnnotation(PluginInfo.class);
        if (info != null) {
            return String.format("Plugin Name: %s, Version: %s, Author %s, Description:" +
                            " %s", info.name(), info.version(),
                    info.author(), info.description());
        } else {
            return "No PluginInfo annotation found.";
        }

    }
}
