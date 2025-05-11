package com.doctordaddysir.core.reflection.annotations.handlers;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

public interface ReflectionScanner {
    Set<Field> findTargetsByAnnotation(Class<? extends Annotation> annotation) throws IOException, ClassNotFoundException;

    void findTargetsBySuperClass(Class<?> superClass);

    void findTargetsByInterface(Class<?> interfaceClass);

    void execute() throws IOException, ClassNotFoundException;
}
