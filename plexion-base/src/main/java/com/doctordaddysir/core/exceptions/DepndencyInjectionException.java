package com.doctordaddysir.core.exceptions;


import com.doctordaddysir.core.utils.ExceptionUtils.ReflectionDIError;
import lombok.Getter;

@Getter
public class DepndencyInjectionException extends Exception {
    private ReflectionDIError reflectionDIError;

    public DepndencyInjectionException(ReflectionDIError reflectionDIError,
                                       String message) {
        super(message);
        this.reflectionDIError = reflectionDIError;
    }
}
