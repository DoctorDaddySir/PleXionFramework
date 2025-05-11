package com.doctordaddysir.core.utils.reflection;

import com.doctordaddysir.core.exceptions.RouteRegistryException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Slf4j
public class AnnotationScanner {

    public static Set<Class<?>> findAllAnnotatedClasses(String basePackage) throws IOException, RouteRegistryException {
        return findClassesWithAnnotation(Annotation.class, basePackage);
    }

    public static Set<Class<?>> findClassesWithAnnotation(Class<? extends Annotation> annotation, String basePackage) throws RouteRegistryException, IOException {

        Enumeration<URL> resources = null;
        Set<Class<?>> classes = new HashSet<>();
        String path = basePackage.replace('.', '/');
        resources = getResources(path);


        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();

            if (resource.getProtocol().equals("file")) {
                try {
                    classes.addAll(scanDirectory(new File(resource.getFile()),
                            basePackage,
                            annotation));
                } catch (ClassNotFoundException e) {
                    throw new RouteRegistryException();
                }
            } else if (resource.getProtocol().equals("jar")) {
                JarURLConnection connection =
                        (JarURLConnection) resource.openConnection();
                try (JarFile jarFile = connection.getJarFile()) {
                    classes.addAll(scanJar(jarFile, path, annotation));
                } catch (ClassNotFoundException e) {
                    throw new RouteRegistryException();
                }
            }
        }

        return classes;
    }

    private static Enumeration<URL> getResources(String path) throws IOException {
        return Thread.currentThread().getContextClassLoader().getResources(path);
    }

    public static Set<Class<?>> findClassesWithAnnotationFromCollection(Collection<Class<? extends Annotation>> annotations, String basePackage) throws IOException, ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();

        String path = basePackage.replace('.', '/');
        Enumeration<URL> resources =
                getResources(path);

        while (resources.hasMoreElements()) {

            URL resource = resources.nextElement();
            for (Class<? extends Annotation> annotation : annotations) {
                if (resource.getProtocol().equals("file")) {
                    classes.addAll(scanDirectory(new File(resource.getFile()),
                            basePackage,
                            annotation));
                } else if (resource.getProtocol().equals("jar")) {
                    JarURLConnection connection =
                            (JarURLConnection) resource.openConnection();
                    try (JarFile jarFile = connection.getJarFile()) {
                        classes.addAll(scanJar(jarFile, path, annotation));
                    }
                }
            }
        }

        return classes;
    }

    private static Set<Class<?>> scanDirectory(File directory, String basePackage,
                                               Class<? extends Annotation> annotation) throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        if (!directory.exists()) return classes;

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                classes.addAll(scanDirectory(file, basePackage + "." + file.getName(),
                        annotation));
            } else if (file.getName().endsWith(".class")) {
                String className = basePackage + "." + file.getName().replace(".class",
                        "");
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(annotation)) {
                    classes.add(clazz);
                }
            }
        }

        return classes;
    }

    private static Set<Class<?>> scanJar(JarFile jar, String path, Class<?
            extends Annotation> annotation) throws ClassNotFoundException {
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

    public static Set<Method> findMethodsWithAnnotation(Class<? extends Annotation> annotation, Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(annotation)).collect(Collectors.toSet());
    }
}
