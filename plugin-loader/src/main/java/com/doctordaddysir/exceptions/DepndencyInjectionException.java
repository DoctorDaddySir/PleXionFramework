package com.doctordaddysir.exceptions;

import com.doctordaddysir.utils.ReflectionUtils.ReflectionDIError;
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
