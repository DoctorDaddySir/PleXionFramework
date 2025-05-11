package com.doctordaddysir.core.utils;

import com.doctordaddysir.core.model.TriConsumer;
import com.doctordaddysir.core.proxies.PleXionInvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class PleXionProxyBuilder<T> {
    private final T target;
    private final Class<T> iface;
    private BiConsumer<Method, Object[]> beforeInvoke;
    private TriConsumer<Method, Object, Object[]> afterInvoke;
    private BiConsumer<Method, Throwable> onError;
    private Predicate<Method> filter;

    public PleXionProxyBuilder(T target, Class<T> iface) {
        this.target = target;
        this.iface = iface;
    }

    public PleXionProxyBuilder<T> beforeInvoke(BiConsumer<Method, Object[]> consumer) {
        this.beforeInvoke = consumer;
        return this;
    }

    public PleXionProxyBuilder<T> afterInvokeWithResult(TriConsumer<Method, Object, Object[]> consumer) {
        this.afterInvoke = consumer;
        return this;
    }

    public PleXionProxyBuilder<T> onError(BiConsumer<Method, Throwable> consumer) {
        this.onError = consumer;
        return this;
    }

    public PleXionProxyBuilder<T> filterMethod(Predicate<Method> filter) {
        this.filter = filter;
        return this;
    }

    public T build() {
        PleXionInvocationHandler<T> handler = new PleXionInvocationHandler<>(target)
                .beforeInvoke(beforeInvoke)
                .afterInvokeWithResult(afterInvoke)
                .filter(filter)
                .onError(onError);

        return iface.cast(Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                new Class<?>[]{iface},
                handler
        ));
    }
}