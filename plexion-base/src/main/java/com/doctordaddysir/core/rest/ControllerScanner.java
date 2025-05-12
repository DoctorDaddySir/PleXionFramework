package com.doctordaddysir.core.rest;

import com.doctordaddysir.core.SystemInfo;
import com.doctordaddysir.core.exceptions.RouteRegistryException;
import com.doctordaddysir.core.reflection.annotations.Bean;
import com.doctordaddysir.core.utils.reflection.AnnotationScanner;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Bean
@Slf4j
@Getter
public class ControllerScanner {
    private static final Map<String, RouteDefinition[]> registry = new HashMap<>();

    public static  Map<String, RouteDefinition> collectControllersAndRoutes() {
        Map<String, RouteDefinition> routes = new HashMap<>();
        try {
            Set<Class<?>> controllers = CollectControllers();
            controllers.forEach(controller -> {
                SystemInfo.ROUTE_ANNOTATIONS.forEach(annotation ->
                        {
                            try {
                                Set<Method> methods =
                                        AnnotationScanner.findMethodsWithAnnotation(annotation, controller);
                                if (!methods.isEmpty()) {
                                    if (methods.size() > 1) throw new RouteRegistryException();
                                    Method method = methods.stream().findFirst().get();
                                    RouteDefinition route = new RouteDefinition(controller.getDeclaredConstructor().newInstance(), method);
                                    String path = method.getAnnotation(annotation).annotationType().getMethod("value").invoke(method.getAnnotation(annotation)).toString();
                                    String key = String.format("%s:%s",annotation.getSimpleName().toUpperCase(), path);
                                    routes.put(key, route);
                                }
                            } catch (RouteRegistryException | NoSuchMethodException |
                                     InstantiationException | IllegalAccessException |
                                     InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }

                        }
                );
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            log.error("Class not found", e);
        }
        return routes;
    }
    private static Set<Class<?>> CollectControllers() throws IOException,
            ClassNotFoundException {
        return AnnotationScanner.findClassesWithAnnotationFromCollection(SystemInfo.CONTROLLER_ANNOTATIONS,
                SystemInfo.BASE_PACKAGE);
    }

}
