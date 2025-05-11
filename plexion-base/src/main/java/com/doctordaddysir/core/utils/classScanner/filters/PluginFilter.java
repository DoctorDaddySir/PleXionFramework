package com.doctordaddysir.core.utils.classScanner.filters;


import com.doctordaddysir.core.reflection.annotations.PluginInfo;
import com.doctordaddysir.core.exceptions.InvalidPluginException;
import com.doctordaddysir.core.plugins.Plugin;
import com.doctordaddysir.core.plugins.loaders.PluginLoader;
import com.doctordaddysir.core.utils.reflection.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluginFilter implements ClassFilter {
    @Override
    public boolean accept(String className, ClassLoader classLoader) {

        Class<?> clazz = null;
        try {
            clazz = classLoader.loadClass(className);
            hasInterface(clazz, Plugin.class);
            checkForPluginInfo(clazz);
            return true;

        } catch (ClassNotFoundException e) {
            log.error("Class not found: {}", className);
        } catch (InvalidPluginException e) {
            log.error("Invalid plugin: {} {}", className, e.getMessage());
        }

        return false;
    }

    private void hasInterface(Class<?> clazz, Class<?> intr) throws InvalidPluginException {
        if (ReflectionUtils.hasInterface(clazz, intr)) {
            return;
        }
        throw new InvalidPluginException(PluginLoader.PluginLoadResult.INVALID_PLUGIN_TYPE);

    }

    private void checkForPluginInfo(Class<?> clazz) throws InvalidPluginException {
        if (ReflectionUtils.hasAnnotation(clazz, PluginInfo.class)) {
            return;
        }
        throw new InvalidPluginException(PluginLoader.PluginLoadResult.MISSING_ANNOTATION);
    }
}
