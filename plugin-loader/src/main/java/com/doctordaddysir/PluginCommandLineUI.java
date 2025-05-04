package com.doctordaddysir;


import com.doctordaddysir.annotations.AnnotationUtils;
import com.doctordaddysir.plugins.base.Plugin;
import com.doctordaddysir.plugins.base.PluginUI;
import com.doctordaddysir.plugins.loaders.PluginLoader;
import com.doctordaddysir.plugins.utils.ReflectionUtils;
import com.doctordaddysir.proxies.PluginProxyFactory;
import com.doctordaddysir.proxies.PluginProxyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class PluginCommandLineUI extends PluginUI<PluginCommandLineUI> {

    private Scanner scanner;
    private Boolean isDebugMode = false;
    private List<Class<?>> plugins = new ArrayList<>();
    private final Map<String, Plugin> instantiatedPlugins = new HashMap<>();
    private final Map<String, ClassLoader> classLoaders = new HashMap<>();
    private final Map<String, Plugin> proxies = new HashMap<>();

    @Override
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
                    startLoop();
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
                    LifeCycleManager.invokeLoad(plugin);
                    PluginProxyUtils.executePluginOrProxy(plugin, proxy);
                    System.out.println("Plugin executed successfully. Press enter to " +
                            "continue...");
                    scanner.nextLine();
                } catch (InstantiationException | IllegalAccessException |
                         InvocationTargetException |
                         NoSuchMethodException e) {
                    System.err.println("Error instantiating plugin: " + e.getMessage());
                }
            }
        }
        scanner.close();
        PluginLoader.destroyPlugins(instantiatedPlugins, classLoaders, proxies);
    }

    @Override
    public PluginCommandLineUI setDebugMode(Boolean debugMode) {
        this.isDebugMode = debugMode;
        return this;
    }


    private void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }


    @Override
    public void out(String message) {
        System.out.println(message);
    }
}
