package com.doctordaddysir.annotations;

import ch.qos.logback.classic.Logger;
import com.doctordaddysir.annotations.handlers.BeanCollector;
import com.doctordaddysir.plugins.loaders.PluginLoader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Bean
@Data
public class PlexionBootLoader {
    private final List<Object> instances = new ArrayList<>();
    private final List<Class<?>> loadedClasses = new ArrayList<>();
    @Inject
    private final BeanCollector beanCollector;
    @Inject
    private final PluginLoader loader;

    public PlexionBootLoader() {
        this(null, null);
    }

    @Injectable
    public PlexionBootLoader(@Inject PluginLoader loader,
                             @Inject BeanCollector beanCollector) {
        this.loader = loader;
        this.beanCollector = beanCollector;
    }


    public void boot(Boolean isDebug) {
        Logger logger = (Logger) log;
        if(isDebug){
            logger.setLevel(ch.qos.logback.classic.Level.DEBUG);
            log.debug("Debug mode enabled");
        } else {
            logger.setLevel(ch.qos.logback.classic.Level.INFO);
        }
        loader.load(isDebug);
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
