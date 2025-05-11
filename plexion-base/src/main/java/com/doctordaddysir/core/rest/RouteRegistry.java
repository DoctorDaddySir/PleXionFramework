package com.doctordaddysir.core.rest;

import com.doctordaddysir.core.SystemInfo;
import com.doctordaddysir.core.reflection.annotations.Bean;
import com.doctordaddysir.core.exceptions.RouteRegistryException;
import com.doctordaddysir.core.utils.reflection.AnnotationScanner;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Bean
@Slf4j
@Getter
public class RouteRegistry {
    private final   Map<Class<? extends Annotation> /*verb*/, Map<Class<?>/* controller
    */, Method>> registry = new ConcurrentHashMap<>();

    public  void init() {
        collectControllersAndRoutes();
    }

    private  void collectControllersAndRoutes() {
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
                                    Map<Class<?>, Method> routeMap = getRouteMap(controller, methods.stream().findFirst().get());
                                    addRouteToRegistry(annotation, routeMap);
                                }
                            } catch (RouteRegistryException e) {
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
    }

    private  void addRouteToRegistry(Class<? extends Annotation> annotation, Map<Class<?>, Method> routeMap) {
        log.debug("Adding route: {} - {} {}",
                annotation.getSimpleName().toUpperCase(),
                routeMap.entrySet().stream().findFirst().get().getKey().getSimpleName(),
                routeMap.entrySet().stream().findFirst().get().getValue().getName());
        registry.put(annotation, routeMap);
    }

    private  Set<Class<?>> CollectControllers() throws IOException,
            ClassNotFoundException {
        return AnnotationScanner.findClassesWithAnnotationFromCollection(SystemInfo.CONTROLLER_ANNOTATIONS,
                SystemInfo.BASE_PACKAGE);
    }


    private  Map<Class<?>,Method> getRouteMap(Class<?> controller, Method method) {
        return Map.of(controller, method);



    }

}
