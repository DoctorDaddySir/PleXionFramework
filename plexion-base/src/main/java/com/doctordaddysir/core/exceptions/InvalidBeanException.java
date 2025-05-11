package com.doctordaddysir.core.exceptions;


import com.doctordaddysir.core.utils.reflection.ReflectionUtils.ReflectionClassError;

public class InvalidBeanException extends Exception {
    private ReflectionClassError reflectionClassError;

    public InvalidBeanException(ReflectionClassError reflectionClassError,
                                String message) {
        super(message);
        this.reflectionClassError = reflectionClassError;
    }
}
