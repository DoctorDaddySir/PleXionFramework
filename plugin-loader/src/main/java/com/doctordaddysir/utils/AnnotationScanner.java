package com.doctordaddysir.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class AnnotationScanner {

    public static Set<Class<?>> findAllAnnotatedClasses(String basePackage) throws IOException, ClassNotFoundException {
        return findClassesWithAnnotation(Annotation.class, basePackage);
    }
    public static Set<Class<?>> findClassesWithAnnotation(Class<? extends Annotation> annotation, String basePackage) throws IOException, ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();

        String path = basePackage.replace('.', '/');
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();

            if (resource.getProtocol().equals("file")) {
                classes.addAll(scanDirectory(new File(resource.getFile()), basePackage, annotation));
            } else if (resource.getProtocol().equals("jar")) {
                JarURLConnection connection = (JarURLConnection) resource.openConnection();
                try (JarFile jarFile = connection.getJarFile()) {
                    classes.addAll(scanJar(jarFile, path, annotation));
                }
            }
        }

        return classes;
    }

    private static Set<Class<?>> scanDirectory(File directory, String basePackage, Class<? extends Annotation> annotation) throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        if (!directory.exists()) return classes;

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                classes.addAll(scanDirectory(file, basePackage + "." + file.getName(), annotation));
            } else if (file.getName().endsWith(".class")) {
                String className = basePackage + "." + file.getName().replace(".class", "");
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(annotation)) {
                    classes.add(clazz);
                }
            }
        }

        return classes;
    }

    private static Set<Class<?>> scanJar(JarFile jar, String path, Class<? extends Annotation> annotation) throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.startsWith(path) && name.endsWith(".class") && !entry.isDirectory()) {
                String className = name.replace('/', '.').replace(".class", "");
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(annotation)) {
                    classes.add(clazz);
                }
            }
        }

        return classes;
    }

}
