package com.doctordaddysir.plugins.loaders;

import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader {
    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        Class<?> clazz = findLoadedClass(name);
        if (clazz == null) {
            try {
                clazz = findClass(name);
            } catch (ClassNotFoundException e) {
                clazz = super.loadClass(name, resolve);
            }
        }
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }
}
