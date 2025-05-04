package com.doctordaddysir.classScanner;

@FunctionalInterface
public interface ClassFilter {
    boolean accept(String className, ClassLoader classLoader);
}
