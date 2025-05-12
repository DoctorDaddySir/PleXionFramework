package com.doctordaddysir.core.exceptions;


import com.doctordaddysir.core.utils.ExceptionUtils.ReflectionFieldError;

import java.lang.reflect.Field;

public class InvalidFieldExcepton extends Exception {
    private ReflectionFieldError reflectionFieldError;
    private Field field;

    public InvalidFieldExcepton(ReflectionFieldError reflectionFieldError, Field field) {
        this.reflectionFieldError = reflectionFieldError;
        this.field = field;
    }

    @Override
    public String getMessage() {
        return reflectionFieldError.name();
    }
}
