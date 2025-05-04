package com.doctordaddysir;

import com.doctordaddysir.plugins.loaders.PluginLoader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            PluginLoader
                    .loadPlugins()
                    .setDebugMode(true)
                    .start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}