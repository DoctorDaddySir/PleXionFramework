package com.doctordaddysir.exceptions;

import com.doctordaddysir.utils.ReflectionUtils;

import java.lang.reflect.Field;

public class InvalidFieldExcepton extends Exception {
    private ReflectionUtils.ReflectionFieldError reflectionFieldError;
    private Field field;

    public InvalidFieldExcepton(ReflectionUtils.ReflectionFieldError reflectionFieldError, Field field) {
        this.reflectionFieldError = reflectionFieldError;
        this.field = field;
    }

    @Override
    public String getMessage() {
        return reflectionFieldError.name();
    }
}
