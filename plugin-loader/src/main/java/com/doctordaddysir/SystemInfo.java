package com.doctordaddysir;

import com.doctordaddysir.annotations.Bean;
import com.doctordaddysir.annotations.Inject;
import lombok.Data;

import java.lang.annotation.Annotation;


@Data
public class SystemInfo {
    public static final String BASE_PACKAGE = "com.doctordaddysir";
    public static Class<? extends Annotation> INJECTION_ANNOTATION = Inject.class;
    public static Class<? extends Annotation> BEAN_ANNOTATION = Bean.class;
}
