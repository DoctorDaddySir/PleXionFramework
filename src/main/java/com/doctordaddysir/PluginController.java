package com.doctordaddysir;


import com.doctordaddysir.annotations.AnnotationUtils;
import com.doctordaddysir.plugins.base.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PluginController {
    private List<Class<?>> plugins = new ArrayList<>();
    private Scanner scanner;


    public void registerPlugin(String fullyQualifiedClassName) {
        try {
            Class<?> clazz = Class.forName(fullyQualifiedClassName);
            if (Plugin.class.isAssignableFrom(clazz)) {
                plugins.add(clazz);
                System.out.println(AnnotationUtils.readPluginInfo(clazz));

            } else {
                System.out.println("Class does not implement PluginInterface");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Plugin class not found: " + fullyQualifiedClassName);
        }

    }

    public void start(Scanner scanner) {
        this.scanner = scanner;
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
                    Plugin plugin = instantiate(choice);
                    plugin.execute();
                    System.out.println("Plugin executed successfully. Press enter to continue...");
                    scanner.nextLine();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    System.out.printf("Error instantiating plugin: %s\n", e.getMessage());
                }
            }
        }
        scanner.close();
    }

    private void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }


    private Plugin instantiate(int choice) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return (Plugin) plugins.get(choice - 1).getDeclaredConstructor().newInstance();
    }
}
