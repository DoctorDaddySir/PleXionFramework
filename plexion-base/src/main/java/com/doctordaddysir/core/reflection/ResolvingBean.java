package com.doctordaddysir.core.reflection;

import com.doctordaddysir.core.annotations.handlers.BeanCollector;
import com.doctordaddysir.core.exceptions.InvalidBeanException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ResolvingBean {
    private Class<?> type;
    private Object resolvingInstance;
    private Boolean isResolving;
    private Object[] args;
    private Constructor<?> constructor;
    private BeanCollector beanCollector;
    @Builder.Default
    private Boolean isPrimitive = false;
    @Builder.Default
    private Boolean isResolved = false;

    public void startResolving() {
        this.isResolving = true;
        beanCollector.startBeanResolving(type, this);
    }

    public void stopResolving() {
        this.isResolving = false;
        beanCollector.stopBeanResolving(type);
    }

    public Object invoke() throws InvocationTargetException, InstantiationException
            , IllegalAccessException, InvalidBeanException {
        if (isPrimitive) {
            return resolvingInstance;
        }
        return constructor.newInstance(args);
    }

    public ResolvingBean updateArgsAndGenerateNewResolvingInstance(Object[] args) throws InvalidBeanException,
            IOException, ClassNotFoundException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        return beanCollector.updateResolvingBean(this, args);
    }

}