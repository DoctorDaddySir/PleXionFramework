package com.doctordaddysir;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            PluginDirectoryLoader
                    .loadPluginsFromDirectory()
                    .setDebugMode(true)
                    .start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}