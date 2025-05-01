package com.doctordaddysir;

import com.doctordaddysir.annotations.PluginInfo;
import com.doctordaddysir.base.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class PluginDirectoryLoader {
    private final static String DIRECTORY_NAME = "plugins";
    private final static String FQCN_PREFIX = "plugins.";
    private final static PluginController controller = new PluginController();

    public PluginController getController() {
        return controller;
    }

    public static PluginController loadPluginsFromDirectory() throws IOException {
        File pluginDir = new File(DIRECTORY_NAME);
        if (!pluginDir.exists()) {
            System.out.println("No plugins found in " + DIRECTORY_NAME);
            return controller;
        }

        List<File> files = Files.walk(pluginDir.toPath()).map(Path::toFile)
                .filter(f->f.getName().endsWith(".class"))
                .toList();

        URL[] urls = {pluginDir.toURI().toURL()};

        try (URLClassLoader loader = new URLClassLoader(urls)) {
            files.stream()
                    .map(f -> FQCN_PREFIX + f.getName().replace(".class", ""))
                    .map((String name) -> {
                        try {
                            return loader.loadClass(name);
                        } catch (ClassNotFoundException e) {
                            System.out.println("Unable to load class " + name);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(c -> {
                        Boolean isPlugin = c.isAnnotationPresent(PluginInfo.class);
                        Boolean isSubclass = Plugin.class.isAssignableFrom(c);
                        Boolean isCorrectFQCN = c.getName().equals(FQCN_PREFIX + c.getSimpleName());
                        if (!isPlugin) {
                            System.out.println("Class " + c.getName() + " is not annotated with @PluginInfo");
                        }
                        if (!isSubclass) {
                            System.out.println("Class " + c.getName() + " does not extend Plugin Interface");
                        }
                        if (!isCorrectFQCN) {
                            System.out.printf("Class %s does not have the correct fully qualified class name: expected %s and got %s", c.getName(), FQCN_PREFIX + c.getSimpleName(), c.getName());
                        }
                        return isPlugin && isSubclass && isCorrectFQCN;

                    })
                    .forEach(controller::registerPlugin);
        }
        return controller;

    }

}
