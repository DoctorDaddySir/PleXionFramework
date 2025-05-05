package com.doctordaddysir.annotations;

import ch.qos.logback.classic.Logger;
import com.doctordaddysir.annotations.handlers.BeanCollector;
import com.doctordaddysir.exceptions.InvalidBeanException;
import com.doctordaddysir.plugins.loaders.PluginLoader;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Bean
public class PlexionBootLoader {
    private final List<Object> instances = new ArrayList<>();
    private final List<Class<?>> loadedClasses = new ArrayList<>();
    private final BeanCollector beanCollector = new BeanCollector();
    private final PluginLoader loader;

    public PlexionBootLoader() throws InvalidBeanException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        this.loader =
                (PluginLoader) beanCollector.getOrCreateBeanInstance(PluginLoader.class);
        loader.setBootLoader(this);
    }

    public void boot(Boolean isDebug) {
        Logger logger = (Logger) log;
        if(isDebug){
            logger.setLevel(ch.qos.logback.classic.Level.DEBUG);
            log.debug("Debug mode enabled");
        } else {
            logger.setLevel(ch.qos.logback.classic.Level.INFO);
        }
        beanCollector.collectBeans();
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
