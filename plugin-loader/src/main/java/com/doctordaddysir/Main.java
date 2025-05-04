package com.doctordaddysir;

import com.doctordaddysir.plugins.base.PluginUI;
import com.doctordaddysir.plugins.loaders.PluginLoader;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        PluginLoader.loadPluginsAndReportErrors(true).start();
    }
}