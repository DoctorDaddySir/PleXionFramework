package com.doctordaddysir.core.rest;

import com.doctordaddysir.core.model.RequestBody;
import com.google.gson.Gson;
import io.undertow.server.HttpServerExchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RequestBodyHandler {
    private static final Gson gson = new Gson();

    public static Object[] resolveMethodParameters(Method method, HttpServerExchange exchange) throws Exception {
        exchange.startBlocking();
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];


        String jsonBody;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getInputStream(), StandardCharsets.UTF_8))) {
            jsonBody = reader.lines().collect(Collectors.joining("\n"));
        }

        for (int i = 0; i < parameters.length; i++) {
            if (hasAnnotation(parameters[i], RequestBody.class)) {
                args[i] = gson.fromJson(jsonBody, parameters[i].getType());
            } else {
                args[i] = null;
            }
        }

        return args;
    }

    private static boolean hasAnnotation(Parameter parameter, Class<? extends Annotation> annotationClass) {
        return parameter.isAnnotationPresent(annotationClass);
    }
}

