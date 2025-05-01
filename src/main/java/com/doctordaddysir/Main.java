package com.doctordaddysir;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PluginController controller = new PluginController();
        try {
            PluginDirectoryLoader
                    .loadPluginsFromDirectory()
                    .start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}