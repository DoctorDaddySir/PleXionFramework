package com.doctordaddysir.core.utils;

public class ExceptionUtils {
    public enum ReflectionFieldError {
    NO_SUCH_FIELD,
    INACCESSIBLE_FIELD,
    INACCESSIBLE_CLASS,
    WRONG_TYPE;

}

    public enum ReflectionClassError {
        NO_SUCH_CLASS,
        WRONG_TYPE,
        MULTIPLE_IMPLEMENTATIONS,
        NO_IMPLEMENTATION;
    }

    public enum ReflectionDIError {
        INSTANTIATION_EXCEPTION,
        ILLEGAL_ACCESS_EXCEPTION,
        INVOCATION_TARGET_EXCEPTION,
        NO_SUCH_METHOD_EXCEPTION;

    }
}
