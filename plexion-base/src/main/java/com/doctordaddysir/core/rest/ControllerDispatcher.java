package com.doctordaddysir.core.rest;

import com.doctordaddysir.core.SystemInfo;
import com.doctordaddysir.core.exceptions.RouteRegistryException;
import com.doctordaddysir.core.reflection.annotations.Post;
import com.doctordaddysir.core.utils.reflection.AnnotationScanner;
import com.doctordaddysir.rest.controllers.UserController;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Slf4j
public class ControllerDispatcher implements HttpHandler {
    private final Map<String, RouteDefinition> routes;

    public ControllerDispatcher(Map<String, RouteDefinition> routes) {
        this.routes = routes;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange){
        try {
            String path = exchange.getRequestPath();
            String method = exchange.getRequestMethod().toString();

            RouteDefinition route = routes.get(method + ":" + path);
            if (route == null) {
                exchange.setStatusCode(404);
                exchange.getResponseSender().send("Not Found");
                return;
            }

            Object controller = route.getControllerInstance();
            Method targetMethod = route.getMethod();

            Object[] args = RequestBodyHandler.resolveMethodParameters(targetMethod,
                    exchange);

            Object result = targetMethod.invoke(controller, args);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(result.toString());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
