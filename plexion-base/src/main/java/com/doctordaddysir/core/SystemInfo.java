package com.doctordaddysir.core;

import com.doctordaddysir.core.reflection.annotations.*;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.Set;


@Data
public class SystemInfo {
    public static final String BASE_PACKAGE = "com.doctordaddysir";
    public static Class<? extends Annotation> INJECTION_ANNOTATION = Inject.class;
    public static Set<Class<? extends Annotation>> BEAN_ANNOTATIONS = Set.of(Bean.class, Service.class);
    public static Set<Class<? extends Annotation>> CONTROLLER_ANNOTATIONS = Set.of(RestController.class);
    public static Set<Class<? extends Annotation>> ROUTE_ANNOTATIONS = Set.of(Get.class, Post.class, Put.class, Delete.class);

}
