package com.doctordaddysir;

import com.doctordaddysir.annotations.PlexionBootLoader;
import com.doctordaddysir.plugins.loaders.PluginLoader;

public class Main {
    public static void main(String[] args) {

        PlexionBootLoader plexion = new PlexionBootLoader();
        plexion.boot(false);
    }
}