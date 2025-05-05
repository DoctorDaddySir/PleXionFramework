package com.doctordaddysir;

import com.doctordaddysir.annotations.PlexionBootLoader;
import com.doctordaddysir.exceptions.InvalidBeanException;
import com.doctordaddysir.plugins.loaders.PluginLoader;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InvalidBeanException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {

        PlexionBootLoader plexion = new PlexionBootLoader();
        plexion.boot(false);
    }
}