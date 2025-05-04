package com.doctordaddysir.classScanner;


import com.doctordaddysir.plugins.PluginClassLoader;
import com.doctordaddysir.plugins.base.Plugin;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class FilteredClassScanner {


    public static List<Class<?>> scanJarForClasses(File jarFile, ClassFilter filter) {
        List<Class<?>> matchedClasses = new ArrayList<>();
        try (PluginClassLoader loader =
                     new PluginClassLoader(new URL[]{jarFile.toURI().toURL()},
                             Plugin.class.getClassLoader())){
try(JarFile jarfile = new JarFile(jarFile)) {
    Enumeration<JarEntry> entries = jarfile.entries();


    while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        String className = convertToClassName(entry);
        boolean isClassFile = isClassFile(entry);
        boolean isDirectory = entry.isDirectory();

        if (isClassFile && !isDirectory && filter.accept(className, loader)) {
            matchedClasses.add(loader.loadClass(className));
        }
    }
} catch (ClassNotFoundException e) {
    log.debug("Class not found: {}", e.getMessage());
}

        } catch (IOException e) {
            log.error("Error while scanning jar file: {} : {}", jarFile, e.getMessage());
        }

        return matchedClasses;
    }

    private static boolean isClassFile(JarEntry entry) {
        return entry.getName().endsWith(".class");
    }

    private static String convertToClassName(JarEntry entry) {
        return entry.getName().replace("/", ".").replace(".class", "");
    }
}