package com.doctordaddysir.core.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@AllArgsConstructor
@Getter
public class RouteDefinition {
    private final Object controllerInstance;
    private final Method method;
}