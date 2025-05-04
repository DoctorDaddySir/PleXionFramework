package com.doctordaddysir.plugins.loaders;

import com.doctordaddysir.PluginCommandLineUI;
import com.doctordaddysir.classScanner.ClassFilter;
import com.doctordaddysir.classScanner.FilteredClassScanner;
import com.doctordaddysir.classScanner.PluginFilter;
import com.doctordaddysir.exceptions.InvalidPluginException;
import com.doctordaddysir.plugins.PluginClassLoader;
import com.doctordaddysir.plugins.base.Plugin;
import com.doctordaddysir.plugins.base.PluginUI;
import com.doctordaddysir.plugins.utils.LifeCycleUtils;
import lombok.Data;
import lombok.Getter;
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
    @Getter
    private final static Map<PluginLoadResult, List<PluginLoadError>> pluginLoadErrors =
            new HashMap<>();
    @Getter
    private final static Set<String> successfullyRegisteredPlugins = new HashSet<>();
    @Getter
    private final static ClassFilter pluginFilter = new PluginFilter();


    public static PluginUI loadPluginsAndReportErrors(Boolean debugMode) {
        try {
            PluginUI pluginUI = loadPlugins(debugMode);
            reportErrors();
            return pluginUI;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PluginUI loadPlugins(Boolean debugMode) throws IOException {
        ui.setDebugMode(debugMode);
        File pluginDir = new File(DIRECTORY_NAME);
        if (!pluginDir.exists()) {
            log.info("No plugins found in " + DIRECTORY_NAME);
            return ui;
        }


        List<File> jarFiles =
                List.of(Objects.requireNonNull(pluginDir.listFiles(f -> f.getName().endsWith(".jar"))));

        resetPluginErrorMap();
        registerJars(jarFiles);
        return ui;

    }
    private static void reportErrors() {
        if(hasPluginErrors()){
            log.error("The following errors occurred while loading plugins:");
            pluginLoadErrors.forEach((result, errors) -> {
                if(errors.isEmpty()){return;}
                log.error("{}: {} Plugins", result, errors.size());
            });
        }
    }

    private static boolean hasPluginErrors() {
        return pluginLoadErrors.values().stream().anyMatch(errors-> !errors.isEmpty());
    }


    @Getter
    private static class PluginLoadError {
        private PluginLoadResult result;
        private Throwable throwable;


        public PluginLoadError(PluginLoadResult result) {
            this.result = result;
            this.throwable = new InvalidPluginException(result);
        }

        public PluginLoadError(Throwable throwable) {
            this.result = result;
            this.throwable = throwable;
        }

    }

    public enum PluginLoadResult {
        SUCCESS,
        CLASS_NOT_FOUND,
        MISSING_ANNOTATION,
        INVALID_PLUGIN_TYPE,
        IO_ERROR
    }

    private static void resetPluginErrorMap() {
        Arrays.stream(PluginLoadResult.values()).forEach(result -> pluginLoadErrors.put(result, new ArrayList<>()));
    }

    private static void registerJars(List<File> files) {
        files.forEach(jarFile -> {
            List<Class<?>> filteredPlugins =
                    FilteredClassScanner.scanJarForClasses(jarFile, pluginFilter);
            registerPlugins(filteredPlugins);
        });


    }

    private static void registerPlugins(List<Class<?>> filteredPlugins) {
        filteredPlugins.forEach(ui::registerPlugin);
    }


    public static void destroyPlugins(Map<String, Plugin> instantiatedPlugins,
                                      Map<String, ClassLoader> classLoaders, Map<String
                    , Plugin> proxies) {
        instantiatedPlugins.keySet().forEach(fqcn -> {
            Plugin plugin = instantiatedPlugins.get(fqcn);
            LifeCycleUtils.invokeDestroy(plugin);
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


}
