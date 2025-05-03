package com.doctordaddysir;


import com.doctordaddysir.annotations.AnnotationUtils;
import com.doctordaddysir.base.Plugin;
import com.doctordaddysir.proxies.PluginInvocationHandler;
import com.doctordaddysir.proxies.PluginProxyFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URLClassLoader;
import java.util.*;

public class PluginController {
    private Boolean isDebugMode = false;
    private List<Class<?>> plugins = new ArrayList<>();
    private final Map<String, Plugin> instantiatedPlugins = new HashMap<>();
    private final Map<String, URLClassLoader> classLoaders = new HashMap<>();
    private final Map<String, Plugin> proxies = new HashMap<>();
    private Scanner scanner;


    public void registerPlugin(String fullyQualifiedClassName, URLClassLoader loader) {
        try {
            Class<?> clazz = Class.forName(fullyQualifiedClassName);
            if (Plugin.class.isAssignableFrom(clazz)) {
                plugins.add(clazz);
                classLoaders.put(fullyQualifiedClassName, loader);
                System.out.println(AnnotationUtils.readPluginInfo(clazz));

            } else {
                System.out.println("Class does not implement PluginInterface");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Plugin class not found: " + fullyQualifiedClassName);
        }

    }

    public void registerPlugin(Class<?> clazz, URLClassLoader loader) {
        if (Plugin.class.isAssignableFrom(clazz)) {
            plugins.add(clazz);
            classLoaders.put(clazz.getName(), loader);
            System.out.println(AnnotationUtils.readPluginInfo(clazz));

        } else {
            System.out.println("Class does not implement PluginInterface");
        }


    }

    public void start() {
        this.scanner = new Scanner(System.in);
        System.out.println("PluginController started. press enter to continue");
        scanner.nextLine();
        startLoop();
    }

    private void startLoop() {
        while (true) {
            clearConsole();
            System.out.println("Available plugins:");
            for (int i = 0; i < plugins.size(); i++) {
                System.out.printf("%d : %s \n", (i + 1), AnnotationUtils.readPluginInfo(plugins.get(i)));
            }
            System.out.printf("Select a plugin to execute (1-%s) or enter 0 to quit: \n", plugins.size());
            String input = scanner.nextLine().trim();
            int choice = 0;
            try {
                choice = Integer.parseInt(input);
                if (input.isEmpty()) {
                    System.out.println("Input cannot be empty. Try again.");
                    startLoop();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }
            if (choice == 0) {
                System.out.println("Exiting...");
                break;
            } else if (choice < 1 || choice > plugins.size()) {
                System.out.println("Invalid choice. Try again.");
            } else {
                try {
                    Plugin plugin;
                    Plugin proxy = null;
                    if (isDebugMode) {
                       proxy  = instantiateProxy(choice);
                       PluginInvocationHandler invocationHandler = (PluginInvocationHandler) Proxy.getInvocationHandler(proxy);
                        plugin = invocationHandler.getTarget();
                        instantiatedPlugins.put(plugin.getClass().getName(), proxy);
                        proxies.put(proxy.getClass().getName(), plugin);
                    } else {
                        plugin = instantiatePlugin(choice);
                        instantiatedPlugins.put(plugin.getClass().getName(), plugin);
                    }
                    LifeCycleManager.invokeLoad(plugin);

                    executePlugin(plugin, proxy);
                    System.out.println("Plugin executed successfully. Press enter to continue...");
                    scanner.nextLine();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    System.out.printf("Error instantiating plugin: %s\n", e.getMessage());
                }
            }
        }
        scanner.close();
        PluginDirectoryLoader.destroyPlugins(instantiatedPlugins, classLoaders, proxies);
    }


    private void executePlugin(Plugin plugin, Plugin proxy) {
        if(proxy == null) {
            executePlugin(plugin);
            return;
        }
        try {
            proxy.execute();
        }catch (Exception e) {
            LifeCycleManager.invokeError(plugin, e);
        }
    }

    private void executePlugin(Plugin plugin) {
        try {
            plugin.execute();
        }catch (Exception e) {
            LifeCycleManager.invokeError(plugin, e);
        }
    }

    private void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
    public PluginController setDebugMode(Boolean debugMode) {
        this.isDebugMode = debugMode;
        return this;
    }

    private Plugin instantiateProxy(int choice) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return PluginProxyFactory.createProxy(instantiatePlugin(choice));
    }

    private Plugin instantiatePlugin(int choice) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return (Plugin) plugins.get(choice - 1).getDeclaredConstructor().newInstance();
    }
}
