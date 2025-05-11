package com.doctordaddysir.core;

import ch.qos.logback.classic.Logger;
import com.doctordaddysir.core.annotations.Bean;
import com.doctordaddysir.core.annotations.Inject;
import com.doctordaddysir.core.annotations.Injectable;
import com.doctordaddysir.core.annotations.handlers.BeanCollector;
import com.doctordaddysir.core.plugins.loaders.PluginLoader;
import com.doctordaddysir.core.rest.PLexionRestServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Bean
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class PlexionBootLoader {
    private final List<Object> instances = new ArrayList<>();
    private final List<Class<?>> loadedClasses = new ArrayList<>();
    @Inject
    private final BeanCollector beanCollector;
    @Inject
    private final PluginLoader loader;
    @Inject
    final PLexionRestServer server;


    @Injectable
    public PlexionBootLoader(@Inject PluginLoader loader,
                             @Inject BeanCollector beanCollector, @Inject PLexionRestServer server) {
        this.loader = loader;
        this.beanCollector = beanCollector;
        this.server = server;
    }


    public void boot(Boolean isDebug) {
        Logger logger = (Logger) log;
        if (isDebug) {
            logger.setLevel(ch.qos.logback.classic.Level.DEBUG);
            log.debug("Debug mode enabled");
        } else {
            logger.setLevel(ch.qos.logback.classic.Level.INFO);
        }
        server.start();
        loader.load(isDebug);
    }

    private void addInstance(Object instance) {
        if (instances.contains(instance)) {
            return;
        }
        instances.add(instance);
    }

    private void addLoadedClass(Class<?> clazz) {
        if (loadedClasses.contains(clazz)) {
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
