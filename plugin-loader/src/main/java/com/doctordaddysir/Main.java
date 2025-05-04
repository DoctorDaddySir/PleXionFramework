package com.doctordaddysir;

import com.doctordaddysir.plugins.base.PluginUI;
import com.doctordaddysir.plugins.loaders.PluginLoader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            PluginUI pluginUi = PluginLoader.loadPlugins();
//            pluginUi.setDebugMode(true);
            pluginUi.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}