package com.doctordaddysir.ui;


import ch.qos.logback.classic.Logger;
import com.doctordaddysir.annotations.AnnotationUtils;
import com.doctordaddysir.annotations.Bean;
import com.doctordaddysir.plugins.Plugin;
import com.doctordaddysir.plugins.loaders.PluginLoader;
import com.doctordaddysir.utils.LifeCycleHandler;
import com.doctordaddysir.utils.ReflectionUtils;
import com.doctordaddysir.proxies.PluginProxyFactory;
import com.doctordaddysir.utils.PluginProxyUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Getter
@Slf4j
@Bean
public class PlexionCommandLineUI extends PlexionUI {
    private Scanner scanner = new Scanner(System.in);
    private Boolean isDebugMode = false;
    private List<Class<?>> plugins = new ArrayList<>();
    private final Map<String, Plugin> instantiatedPlugins = new HashMap<>();
    private final Map<String, ClassLoader> classLoaders = new HashMap<>();
    private final Map<String, Plugin> proxies = new HashMap<>();

    public PlexionCommandLineUI() {
        super();
    }


    @Override
    public void setDebugMode(Boolean debugMode) {
        this.isDebugMode = debugMode;

    }


    @Override
    public void start(Boolean debugMode, PluginLoader loader) {
        startUI(loader);
    }
    public void startUI(PluginLoader loader) {
        System.out.println("PluginController started. press enter to continue");
        scanner.nextLine();
        startLoop(loader);
    }


    private void startLoop(PluginLoader loader) {
        while (true) {
            clearConsole();
            System.out.println("Available plugins:");
            for (int i = 0; i < plugins.size(); i++) {
                System.out.printf("%d : %s \n", (i + 1),
                        AnnotationUtils.readPluginInfo(plugins.get(i)));
            }
            System.out.printf("Select a plugin to execute (1-%s) or enter 0 to quit: " +
                    "\n", plugins.size());
            String input = scanner.nextLine().trim();
            int choice = 0;
            try {
                choice = Integer.parseInt(input);
                if (input.isEmpty()) {
                    System.out.println("Input cannot be empty. Try again.");
                    startLoop(loader);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid input. Please enter a number.");
                continue;
            }
            if (choice == 0) {
                System.out.println("Exiting...");
                break;
            } else if (choice < 1 || choice > plugins.size()) {
                System.out.println("Invalid choice. Try again.");
            } else {
                try {
                    Plugin plugin =
                            (Plugin) ReflectionUtils.newInstance(plugins.get(choice - 1), null);
                    ;
                    Plugin proxy = null;
                    if (isDebugMode) {
                        proxy = PluginProxyFactory.createProxy(plugin);
                        instantiatedPlugins.put(plugin.getClass().getName(), proxy);
                        proxies.put(proxy.getClass().getName(), plugin);
                    } else {
                        instantiatedPlugins.put(plugin.getClass().getName(), plugin);
                    }
                    LifeCycleHandler.invokeLoad(plugin);
                    PluginProxyUtils.executePluginOrProxy(plugin, proxy);
                    System.out.println("Plugin executed successfully. Press enter to " +
                            "continue...");
                    scanner.nextLine();
                } catch (InstantiationException | IllegalAccessException |
                         InvocationTargetException |
                         NoSuchMethodException e) {
                    log.error("Error instantiating plugin: {}", e.getMessage());
                }
            }
        }
        scanner.close();
        loader.destroyPlugins(instantiatedPlugins, classLoaders, proxies);
    }

    private void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }


    @Override
    public void out(String message) {
        //not implemented in CLI
    }

    @Override
    public void registerPlugin(Class<?> clazz) {

        if (Plugin.class.isAssignableFrom(clazz)) {
            plugins.add(clazz);
            classLoaders.put(clazz.getName(), clazz.getClassLoader());
            log.debug(AnnotationUtils.readPluginInfo(clazz));

        } else {
            log.error("Class: {} does not implement PluginInterface", clazz.getName());
        }
    }

}
