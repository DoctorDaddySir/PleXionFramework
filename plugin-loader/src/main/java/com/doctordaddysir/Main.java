package com.doctordaddysir;

import com.doctordaddysir.plugins.loaders.PluginLoader;

public class Main {
    public static void main(String[] args) {
        PluginLoader.loadPluginsAndReportErrors(true).start();
    }
}