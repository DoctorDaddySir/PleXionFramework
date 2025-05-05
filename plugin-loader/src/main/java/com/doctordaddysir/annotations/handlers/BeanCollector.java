package com.doctordaddysir.annotations.handlers;

import com.doctordaddysir.SystemInfo;
import com.doctordaddysir.utils.AnnotationScanner;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class BeanCollector {
    private final Set<Class<?>> beans = new HashSet<>();

    public void collectBeans() {
        log.info("Collecting beans");
        try {
            beans.addAll(AnnotationScanner.findClassesWithAnnotation(SystemInfo.BEAN_ANNOTATION, SystemInfo.BASE_PACKAGE).stream().toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            log.error("Class not found", e);
        }
    }
}
