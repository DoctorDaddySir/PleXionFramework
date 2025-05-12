package com.doctordaddysir.core.exceptions;


import com.doctordaddysir.core.utils.ExceptionUtils;
import com.doctordaddysir.core.utils.ExceptionUtils.ReflectionClassError;

public class InvalidBeanException extends Exception {
    private ReflectionClassError reflectionClassError;

    public InvalidBeanException(ReflectionClassError reflectionClassError,
                                String message) {
        super(message);
        this.reflectionClassError = reflectionClassError;
    }
}
