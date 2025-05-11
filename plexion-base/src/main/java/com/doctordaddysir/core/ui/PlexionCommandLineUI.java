package com.doctordaddysir.core.ui;


import com.doctordaddysir.core.PlexionBootLoader;
import com.doctordaddysir.core.annotations.AnnotationUtils;
import com.doctordaddysir.core.annotations.Bean;
import com.doctordaddysir.core.annotations.Inject;
import com.doctordaddysir.core.annotations.Injectable;
import com.doctordaddysir.core.annotations.handlers.BeanCollector;
import com.doctordaddysir.core.exceptions.DepndencyInjectionException;
import com.doctordaddysir.core.exceptions.InvalidBeanException;
import com.doctordaddysir.core.exceptions.InvalidFieldExcepton;
import com.doctordaddysir.core.plugins.Plugin;
import com.doctordaddysir.core.plugins.loaders.PluginLoader;
import com.doctordaddysir.core.utils.LifeCycleHandler;
import com.doctordaddysir.core.utils.PleXionProxyBuilder;
import com.doctordaddysir.core.utils.ProxyUtils;
import com.doctordaddysir.core.utils.reflection.ReflectionUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.util.Objects.nonNull;

@Getter
@Slf4j
@Bean
@NoArgsConstructor
public class PlexionCommandLineUI extends PlexionUI {
    private final Map<String, Plugin> instantiatedPlugins = new HashMap<>();
    private final Map<String, ClassLoader> classLoaders = new HashMap<>();
    private final Map<String, Plugin> proxies = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);
    private Boolean isDebugMode = false;
    private List<Class<?>> plugins = new ArrayList<>();
    @Inject
    private PluginLoader loader;
    @Inject
    private BeanCollector beanCollector;
    @Inject
    private PlexionBootLoader bootLoader;

    @Injectable
    public PlexionCommandLineUI(@Inject PlexionBootLoader bootLoader,
                                @Inject BeanCollector beanCollector,
                                @Inject PluginLoader loader) {
        super();
        this.bootLoader = bootLoader;
        this.beanCollector = beanCollector;
        this.loader = loader;
    }


    public PlexionCommandLineUI(Scanner scanner, Boolean isDebugMode,
                                @Inject PluginLoader loader,
                                @Inject BeanCollector beanCollector,
                                @Inject PlexionBootLoader bootLoader) {
        this.scanner = scanner;
        this.isDebugMode = isDebugMode;
        this.plugins = plugins;
    }

    @Override
    public void setDebugMode(Boolean debugMode) {
        this.isDebugMode = debugMode;

    }


    @Override
    public void start(Boolean debugMode, PluginLoader loader) {
        try {
            startUI();
        } catch (DepndencyInjectionException e) {
            log.error("Error starting UI: {} {}", e.getReflectionDIError(),
                    e.getMessage());
        }
    }

    public void startUI() throws DepndencyInjectionException {
        startLoop();
    }

    private void startLoop() throws DepndencyInjectionException {
        printHeader();
        System.out.println("Press enter to continue...");
        scanner.nextLine();
        startMainLoop();
    }

    private void printHeader() {
        clearConsole();
        System.out.println("Plexion Framework CLI");
        System.out.println("=======================");
    }

    private void startMainLoop() throws DepndencyInjectionException {
        int choice = -1;
        while (choice != 0) {
            printHeader();
            displayCommandOptions();
            choice = getCommandChoice(loader);
            processCommandChoice(choice);
        }

        scanner.close();

        loader.destroyPlugins(instantiatedPlugins, classLoaders, proxies);
    }

    private void processCommandChoice(int choice) throws DepndencyInjectionException {
        switch (choice) {
            case 1:
                startPluginLoop();
                break;
            case 2:
                System.out.println("Destroying plugins Command not implemented yet");
                System.out.println("Press enter to continue...");
                scanner.nextLine();
                break;
            case 3:
                System.out.println("Printing plugin info Command not implemented yet");
                System.out.println("Press enter to continue...");
                scanner.nextLine();
                break;
            case 4:
                startInjectorLoop();
                break;
        }
    }

    private void startInjectorLoop() throws DepndencyInjectionException {
        int choice = -1;
        while (choice != 0) {
            printHeader();
            Map<String, Class<?>> beans =
                    loader.getBootLoader().getBeanCollector().getBeans();
            printInjectionOptions(beans);
            try {
                choice = getInjectionChoice();
            } catch (NumberFormatException e) {
                log.error("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
            choice = processInjectionChoice(choice, beans);
        }
    }

    private int processInjectionChoice(int choice, Map<String, Class<?>> beans) throws DepndencyInjectionException {
        if (choice == 0) {
            return choice;
        }
        Class<?> bean = beans.values().stream().toList().get(choice - 1);
        if (nonNull(bean)) {
            startParameterLoop(bean);
        }
        return choice;

    }

    private void startParameterLoop(Class<?> bean) throws DepndencyInjectionException {
        try {

            Object instance = beanCollector.resolve(bean);
            log.info("Created instance of bean: {}", bean.getName());
            log.info(instance.toString());
            scanner.nextLine();
        } catch (InvalidBeanException e) {
            log.error("Invalid bean: {}", e.getMessage());
        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            log.error("Error instantiating bean: {}", e.getMessage());
        } catch (NoSuchMethodException e) {
            log.error("No suitable constructor found for bean: {}", e.getMessage());
        } catch (IOException e) {
            log.error("Error reading plugin info: {}", e.getMessage());
        } catch (ClassNotFoundException e) {
            log.error("Class not found: {}", e.getMessage());
        } catch (NoSuchFieldException e) {
            log.error("No suitable field found for bean: {}", e.getMessage());
        } catch (InvalidFieldExcepton e) {
            log.error("Invalid field: {} for Class: {}", e.getMessage());
        }
    }

    private int getInjectionChoice() {
        return Integer.parseInt(scanner.nextLine());
    }

    private void printInjectionOptions(Map<String, Class<?>> beans) {
        List<String> beanValues = beans.keySet().stream().toList();
        for (int i = 0; i < beans.size(); i++) {
            System.out.println(i + 1 + " : " + beanValues.get(i));
        }
    }

    private int getCommandChoice(PluginLoader loader) {
        String choice = scanner.nextLine();
        int command = -1;
        try {
            command = Integer.parseInt(choice);
            if (choice.isEmpty()) {
                System.out.println("Input cannot be empty. Try again.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid input. Please enter a number.");
        }

        return command;
    }

    private void displayCommandOptions() {
        printHeader();
        System.out.println("1 - Load plugins");
        System.out.println("2 - Destroy plugins");
        System.out.println("3 - Print plugin info");
        System.out.println("4 - Inject dependencies");
        System.out.println("Select a command (1-4) or 0 to quit:");
    }

    private void startPluginLoop() {
        int choice = -1;
        while (choice != 0) {
            printHeader();
            listPlugins();
            choice = getPluginChoice();
        }
    }

    private int getPluginChoice() {

        String input = scanner.nextLine().trim();
        int choice = -1;
        try {
            choice = Integer.parseInt(input);
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Try again.");
                startLoop();
            }
        } catch (NumberFormatException | DepndencyInjectionException e) {
            System.err.println("Invalid input. Please enter a number.");
        }
        if (choice < 1 || choice > plugins.size()) {
            System.out.println("Invalid choice. Try again.");
        } else {
            try {
                getPluginOrProxy(choice);
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException |
                     NoSuchMethodException e) {
                log.error("Error instantiating plugin: {}", e.getMessage());
            }
        }
        return choice;
    }

    private void getPluginOrProxy(int choice) throws IllegalAccessException,
            InstantiationException, InvocationTargetException, NoSuchMethodException {
        Plugin plugin =
                (Plugin) ReflectionUtils.newInstance(plugins.get(choice - 1), null);
        Plugin proxy = null;
        if (isDebugMode) {
            proxy = new PleXionProxyBuilder<>(plugin, Plugin.class)
                    .beforeInvoke((method, args) ->log.debug("Before: {}", method.getName()))
                    .afterInvokeWithResult((method, result, args) ->
                            log.debug("After: {} returned: {}", method.getName(), result))
                    .onError((method, ex) -> log.error("Error in {} : ", method.getName(), ex))
                    .build();
            instantiatedPlugins.put(plugin.getClass().getName(), proxy);
            proxies.put(proxy.getClass().getName(), plugin);
        } else {
            instantiatedPlugins.put(plugin.getClass().getName(), plugin);
        }
        LifeCycleHandler.invokeLoad(plugin);
        ProxyUtils.executePluginOrProxy(plugin, proxy);
        System.out.println("Plugin executed successfully. Press enter to " +
                "continue...");
        scanner.nextLine();
    }

    private void listPlugins() {
        System.out.println("Available plugins:");
        for (int i = 0; i < plugins.size(); i++) {
            System.out.printf("%d : %s \n", (i + 1),
                    AnnotationUtils.readPluginInfo(plugins.get(i)));
        }
        System.out.printf("Select a plugin to execute (1-%s) or enter 0 to quit: " +
                "\n", plugins.size());
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
