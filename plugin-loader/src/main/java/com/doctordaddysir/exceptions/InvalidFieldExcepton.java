package com.doctordaddysir.exceptions;

import com.doctordaddysir.utils.FieldUtils;

public class InvalidFieldExcepton extends Exception {
    private FieldUtils.FieldError fieldError;

    public InvalidFieldExcepton(FieldUtils.FieldError fieldError) {
        this.fieldError = fieldError;
    }

    @Override
    public String getMessage() {
        return fieldError.name();
    }
}
