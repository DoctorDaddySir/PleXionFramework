package com.doctordaddysir.core.plugins.loaders;


import com.doctordaddysir.core.annotations.Bean;
import com.doctordaddysir.core.annotations.Inject;
import com.doctordaddysir.core.annotations.Injectable;
import com.doctordaddysir.core.PlexionBootLoader;
import com.doctordaddysir.core.exceptions.InvalidPluginException;
import com.doctordaddysir.core.plugins.Plugin;
import com.doctordaddysir.core.ui.PlexionUI;
import com.doctordaddysir.core.utils.LifeCycleHandler;
import com.doctordaddysir.core.utils.classScanner.FilteredClassScanner;
import com.doctordaddysir.core.utils.classScanner.filters.ClassFilter;
import com.doctordaddysir.core.utils.classScanner.filters.PluginFilter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.*;

@Slf4j
@Bean
@NoArgsConstructor
public class PluginLoader {
    private final String DIRECTORY_NAME = "plugins";
    @Getter
    private final Map<PluginLoadResult, List<PluginLoadError>> pluginLoadErrors =
            new HashMap<>();
    @Getter
    private final Set<String> successfullyRegisteredPlugins = new HashSet<>();
    @Getter
    private final ClassFilter pluginFilter = new PluginFilter();
    @Inject
    private PlexionUI ui;
    @Setter
    @Getter
    @Inject
    private PlexionBootLoader bootLoader;

    @Injectable
    public PluginLoader(@Inject PlexionUI ui, @Inject PlexionBootLoader bootLoader) {
        this.ui = ui;
        this.bootLoader = bootLoader;
    }

    public void load(Boolean isDebug) {
        loadPluginsAndReportErrors(isDebug).start(isDebug, this);
    }


    public PlexionUI loadPluginsAndReportErrors(Boolean debugMode) {
        try {
            PlexionUI plexionUI = loadPlugins(debugMode);
            reportErrors();
            return plexionUI;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PlexionUI loadPlugins(Boolean debugMode) throws IOException {
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

    private void reportErrors() {
        if (hasPluginErrors()) {
            log.error("The following errors occurred while loading plugins:");
            pluginLoadErrors.forEach((result, errors) -> {
                if (errors.isEmpty()) {
                    return;
                }
                log.error("{}: {} Plugins", result, errors.size());
            });
        }
    }

    private boolean hasPluginErrors() {
        return pluginLoadErrors.values().stream().anyMatch(errors -> !errors.isEmpty());
    }


    private void resetPluginErrorMap() {
        Arrays.stream(PluginLoadResult.values()).forEach(result -> pluginLoadErrors.put(result, new ArrayList<>()));
    }

    private void registerJars(List<File> files) {
        files.forEach(jarFile -> {
            List<Class<?>> filteredPlugins =
                    FilteredClassScanner.scanJarForClasses(jarFile, pluginFilter);
            registerPlugins(filteredPlugins);
        });


    }

    private void registerPlugins(List<Class<?>> filteredPlugins) {
        filteredPlugins.forEach(ui::registerPlugin);
    }


    public void destroyPlugins(Map<String, Plugin> instantiatedPlugins,
                               Map<String, ClassLoader> classLoaders, Map<String
                    , Plugin> proxies) {
        instantiatedPlugins.keySet().forEach(fqcn -> {
            Plugin plugin = instantiatedPlugins.get(fqcn);
            LifeCycleHandler.invokeDestroy(plugin);
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

    public PluginLoader setBootLoader(PlexionBootLoader plexionBootLoader) {
        return this;
    }

    public enum PluginLoadResult {
        SUCCESS,
        CLASS_NOT_FOUND,
        MISSING_ANNOTATION,
        INVALID_PLUGIN_TYPE,
        IO_ERROR
    }

    @Getter
    private class PluginLoadError {
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


}
