package com.doctordaddysir;

import com.doctordaddysir.annotations.PluginInfo;
import com.doctordaddysir.base.Plugin;
import com.doctordaddysir.exceptions.InvalidPluginException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;


public class PluginDirectoryLoader {
    private final static String DIRECTORY_NAME = "plugins";
    private final static String FQCN_PREFIX = "plugins.";
    private final static PluginController controller = new PluginController();

    public static void destroyPlugins(Map<String, Plugin> instantiatedPlugins, Map<String, URLClassLoader> classLoaders, Map<String, Plugin> proxies) {
        instantiatedPlugins.keySet().forEach(fqcn -> {
            Plugin plugin = instantiatedPlugins.get(fqcn);
            LifeCycleManager.invokeDestroy(plugin);
            instantiatedPlugins.put(fqcn, null);
            classLoaders.put(fqcn, null);
            proxies.put(fqcn, null);

        });
    }

    public PluginController getController() {
        return controller;
    }

    public static PluginController loadPluginsFromDirectory() throws IOException {
        File pluginDir = new File(DIRECTORY_NAME);
        if (!pluginDir.exists()) {
            System.out.println("No plugins found in " + DIRECTORY_NAME);
            return controller;
        }

        URL[] urls = {pluginDir.toURI().toURL()};
        registerClassFilesFromDirectory(urls, pluginDir);


        List<File> jarFiles = List.of(Objects.requireNonNull(pluginDir.listFiles(f -> f.getName().endsWith(".jar"))));

        registerJars(jarFiles);
        return controller;

    }

    private static void registerClassFilesFromDirectory(URL[] urls, File pluginDir) throws IOException {
        List<File> files = Files.walk(pluginDir.toPath()).map(Path::toFile)
                .filter(f -> f.getName().endsWith(".class"))
                .toList();

        registerClasses(urls, files);
    }

    private static void registerJars(List<File> files) {
        files.forEach(jarFile -> {
            URL[] urls = null;
            try {
                urls = new URL[]{jarFile.toURI().toURL()};
            } catch (MalformedURLException e) {
                System.out.println("Unable to load jar file " + jarFile.getName());
                return;
            }
            try (PluginClassLoader loader = new PluginClassLoader(urls, Plugin.class.getClassLoader())) {
                InputStream is = loader.getResourceAsStream("META-INF/plugin.properties");
                if (is == null) {
                    System.out.println("Missing plugin.properties in " + jarFile.getName());
                    return;
                }
                Properties props = new Properties();
                props.load(is);
                String fqcn = props.getProperty("plugin-class");

                if (fqcn == null) {
                    System.out.println("plugin-class not defined in " + jarFile.getName());
                    return;
                }

                Class<?> clazz = loader.loadClass(fqcn);

                if (clazz.isAnnotationPresent(PluginInfo.class) &&
                        Plugin.class.isAssignableFrom(clazz) &&
                        clazz.getName().equals(FQCN_PREFIX + clazz.getSimpleName())) {

                    controller.registerPlugin(clazz, loader);
                    System.out.println("Loaded plugin " + clazz.getName() + " from " + jarFile.getName());
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Unable to load class " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Unable to load plugin.properties from " + jarFile.getName());
            }
        });


    }

    private static void registerClasses(URL[] urls, List<File> files) throws IOException {
        try {

            files.forEach(file -> {
                String fqcn = FQCN_PREFIX + file.getName().replace(".class", "");
                URLClassLoader loader = new PluginClassLoader(urls, Plugin.class.getClassLoader());
                Class<?> c;
                try {
                    Boolean hasError = false;
                    c = loader.loadClass(fqcn);
                    Boolean isPlugin = c.isAnnotationPresent(PluginInfo.class);
                    Boolean isSubclass = Plugin.class.isAssignableFrom(c);
                    Boolean isCorrectFQCN = c.getName().equals(FQCN_PREFIX + c.getSimpleName());
                    if (!isPlugin) {
                        System.out.println("Class " + c.getName() + " is not annotated with @PluginInfo");
                        hasError = true;
                    }
                    if (!isSubclass) {
                        System.out.println("Class " + c.getName() + " does not extend Plugin Interface");
                        hasError = true;
                    }
                    if (!isCorrectFQCN) {
                        System.out.printf("Class %s does not have the correct fully qualified class name: " +
                                        "expected %s and got %s", c.getName(), FQCN_PREFIX + c.getSimpleName(),
                                c.getName());
                        hasError = true;
                    }
                    if (hasError) {
                        throw new InvalidPluginException();
                    }

                    controller.registerPlugin(c, loader);
                } catch (ClassNotFoundException e) {
                    System.out.println("Unable to load class " + fqcn);
                }

            });

        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

}
