package com.doctordaddysir.utils.classScanner.filters;

@FunctionalInterface
public interface ClassFilter {
    boolean accept(String className, ClassLoader classLoader);
}
