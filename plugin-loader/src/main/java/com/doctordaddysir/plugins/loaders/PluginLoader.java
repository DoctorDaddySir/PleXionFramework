package com.doctordaddysir.plugins.loaders;

import com.doctordaddysir.LifeCycleManager;
import com.doctordaddysir.PluginCommandLineUI;
import com.doctordaddysir.annotations.PluginInfo;
import com.doctordaddysir.plugins.PluginClassLoader;
import com.doctordaddysir.plugins.base.Plugin;
import com.doctordaddysir.exceptions.InvalidPluginException;
import com.doctordaddysir.plugins.base.PluginUI;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class PluginLoader {
    private final static String DIRECTORY_NAME = "plugins";
    private final static PluginUI ui = PluginCommandLineUI.builder().build();
    private final static Map<String, List<PluginLoadError>> pluginLoadErrors =
            new HashMap<>();
    private final static Set<String> successfullyRegisteredPlugins = new HashSet<>();


    public static void destroyPlugins(Map<String, Plugin> instantiatedPlugins,
                                      Map<String, ClassLoader> classLoaders, Map<String
                    , Plugin> proxies) {
        instantiatedPlugins.keySet().forEach(fqcn -> {
            Plugin plugin = instantiatedPlugins.get(fqcn);
            LifeCycleManager.invokeDestroy(plugin);
            instantiatedPlugins.put(fqcn, null);
            try {
                ((URLClassLoader) classLoaders.get(fqcn)).close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            classLoaders.put(fqcn, null);
            proxies.put(fqcn, null);

        });
    }

    public PluginUI getController() {
        return ui;
    }

    public static PluginUI loadPlugins() throws IOException {
        File pluginDir = new File(DIRECTORY_NAME);
        if (!pluginDir.exists()) {
            log.info("No plugins found in " + DIRECTORY_NAME);
            return ui;
        }

        List<File> jarFiles =
                List.of(Objects.requireNonNull(pluginDir.listFiles(f -> f.getName().endsWith(".jar"))));

        registerJars(jarFiles);
        return ui;

    }


    private static class PluginLoadError {
        private PluginLoadResult result;
        private Throwable t;


        public PluginLoadError(PluginLoadResult result) {
            this.result = result;
            this.t = null;
        }

        public PluginLoadError(PluginLoadResult result,
                               Throwable t) {
            this.result = result;
            this.t = t;
        }

        public PluginLoadResult getResult() {
            return result;
        }

        public Throwable getT() {
            return t;
        }
    }

    public enum PluginLoadResult {
        SUCCESS,
        CLASS_NOT_FOUND,
        MISSING_ANNOTATION,
        INVALID_PLUGIN_TYPE,
        IO_ERROR
    }

    private static class PluginValidator {
        private Set<PluginLoadError> results = new HashSet<>();
    }


    private static List<Class<?>> findAnnotatedPlugins(Class<? extends Annotation> annotation, File jarFile) {
        List<Class<?>> pluginsToRegister = new ArrayList<>();

        try (PluginClassLoader loader =
                     new PluginClassLoader(new URL[]{jarFile.toURI().toURL()},
                             Plugin.class.getClassLoader())) {
            try (JarFile jarfile = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jarfile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        String className = entry.getName()
                                .replace('/', '.')
                                .replace(".class", "");
                        try {
                            Class<?> clazz = loader.loadClass(className);
                            if (!successfullyRegisteredPlugins.contains(className)) {
                                pluginLoadErrors.put(className, new ArrayList<>());
                                checkPluginForAnnotation(annotation, clazz);
                                checkPluginForPluginInterface(clazz);
                                if(pluginLoadErrors.get(className).isEmpty()){
                                    pluginsToRegister.add(clazz);
                                }

                            }else{
                                log.debug("Plugin {} already registered. Skipping.", clazz.getName());
                            }
                        } catch (ClassNotFoundException e) {
                            pluginLoadErrors.get(className).add(new PluginLoadError(PluginLoadResult.CLASS_NOT_FOUND, e));
                        }
                        pluginLoadErrors.get(className).stream().forEach(
                                attempt->{
                                    log.debug("Plugin {} failed to load. Reason: {}",className, attempt.getResult());
                                }
                        );
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pluginsToRegister;
    }

    private static void checkPluginForPluginInterface(Class<?> clazz){
        if (!Plugin.class.isAssignableFrom(clazz)) {
            pluginLoadErrors.get(clazz.getName()).add(new PluginLoadError(PluginLoadResult.INVALID_PLUGIN_TYPE, new InvalidPluginException()));
        }

    }

    private static void checkPluginForAnnotation(Class<? extends Annotation> annotation
            , Class<?> clazz){
        if (!clazz.isAnnotationPresent(annotation)) {
            pluginLoadErrors.get(clazz.getName()).add(new PluginLoadError(PluginLoadResult.MISSING_ANNOTATION, new InvalidPluginException()));
        }
    }

    private static void registerJars(List<File> files) {
        files.forEach(jarFile -> {
            List<Class<?>> annotatedPlugins = findAnnotatedPlugins(PluginInfo.class,
                    jarFile);
            registerPlugins(annotatedPlugins);
        });


    }

    private static void registerPlugins(List<Class<?>> annotatedPlugins) {
        annotatedPlugins.forEach(clazz -> ui.registerPlugin(clazz))
        ;
    }


}
