package com.doctordaddysir.utils.classScanner.filters;


import com.doctordaddysir.annotations.PluginInfo;
import com.doctordaddysir.exceptions.InvalidPluginException;
import lombok.extern.slf4j.Slf4j;
import com.doctordaddysir.plugins.Plugin;

@Slf4j
public class PluginFilter implements ClassFilter {
    @Override
    public  boolean accept(String className, ClassLoader classLoader) {

        Class<?> clazz = null;
        try {
            clazz = classLoader.loadClass(className);
            if(checkForPluginInfo(clazz) && checkForPluginInterface(clazz)){
                return true;
            }
        } catch (ClassNotFoundException e) {
            log.error("Class not found: {}", className);
        } catch (InvalidPluginException e) {
            log.error("Invalid plugin: {} {}", className, e.getMessage());
        }

        return false;
    }

    private Boolean checkForPluginInterface(Class<?> clazz) throws InvalidPluginException {
        if(Plugin.class.isAssignableFrom(clazz)){
            return true;
        }
        log.debug("Class {} does not implement Plugin interface", clazz.getName());
        return false;
    }

    private Boolean checkForPluginInfo(Class<?> clazz) throws InvalidPluginException {
        if(clazz.isAnnotationPresent(PluginInfo.class)){
            return true;
        }
        log.debug("Class {} does not have PluginInfo annotation", clazz.getName());
        return false;
    }
}
