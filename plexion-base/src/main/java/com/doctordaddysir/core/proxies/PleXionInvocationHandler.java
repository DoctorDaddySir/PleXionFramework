package com.doctordaddysir.core.proxies;

import com.doctordaddysir.core.model.TriConsumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static java.util.Objects.isNull;

@Getter
@Slf4j
public class PleXionInvocationHandler<T> implements InvocationHandler {
    private final T target;
    private BiConsumer<Method, Object[]> beforeInvoke;
    private TriConsumer<Method, Object, Object[]> afterInvoke;
    private BiConsumer<Method, Throwable> onError;
    private Predicate<Method> filter;

    public PleXionInvocationHandler(T target) {
        this.target = target;
    }

    public PleXionInvocationHandler<T> beforeInvoke(BiConsumer<Method, Object[]> consumer) {
        this.beforeInvoke = consumer;
        return this;
    }

    public PleXionInvocationHandler<T> afterInvokeWithResult(TriConsumer<Method, Object, Object[]> consumer) {
        this.afterInvoke = consumer;
        return this;
    }

    public PleXionInvocationHandler<T> onError(BiConsumer<Method, Throwable> consumer) {
        this.onError = consumer;
        return this;
    }

    public PleXionInvocationHandler<T> filter(Predicate<Method> filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        try {
            if (beforeInvoke != null && (isNull(filter) || filter.test(method))) beforeInvoke.accept(method, args);
            Object result = method.invoke(target, args);
            if (afterInvoke != null && (isNull(filter) || filter.test(method))) afterInvoke.accept(method, result, args);
            return result;
        } catch (Throwable e) {
            if (onError != null && (isNull(filter) || filter.test(method))) onError.accept(method, e);
            throw e;
        }
    }
}
