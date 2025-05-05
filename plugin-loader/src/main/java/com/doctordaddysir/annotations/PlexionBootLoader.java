package com.doctordaddysir.annotations;

import com.doctordaddysir.annotations.handlers.BeanCollector;
import com.doctordaddysir.plugins.loaders.PluginLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Injectable

public class PlexionBootLoader {
    private final List<Object> instances = new ArrayList<>();
    private final List<Class<?>> loadedClasses = new ArrayList<>();
    private final BeanCollector beanCollector = new BeanCollector();
    public void boot(Boolean isDebug) {
        beanCollector.collectBeans();
        PluginLoader.loadPluginsAndReportErrors(isDebug).start();

    }
    private void addInstance(Object instance) {
        if(instances.contains(instance)){
            return;
        }
        instances.add(instance);
    }
    private void addLoadedClass(Class<?> clazz) {
        if(loadedClasses.contains(clazz)){
            return;
        }
        loadedClasses.add(clazz);
    }
    private void removeInstance(Object instance) {
        instances.remove(instance);
    }
    private void removeLoadedClass(Class<?> clazz) {
        loadedClasses.remove(clazz);
    }

}
