package com.doctordaddysir.exceptions;


import com.doctordaddysir.utils.ReflectionUtils.ReflectionClassError;

public class InvalidBeanException extends Exception {
    private ReflectionClassError reflectionClassError;

    public InvalidBeanException(ReflectionClassError reflectionClassError,
                                String message) {
        super(message);
        this.reflectionClassError = reflectionClassError;
    }
}
