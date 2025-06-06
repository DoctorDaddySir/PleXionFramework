package com.doctordaddysir.core.reflection.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PluginInfo {
    String name();

    String version() default "1.0";

    String author() default "Trent Shelton";

    String description() default "No description provided.";
}
