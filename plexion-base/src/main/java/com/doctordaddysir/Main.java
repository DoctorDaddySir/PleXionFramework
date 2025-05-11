package com.doctordaddysir;

import com.doctordaddysir.core.annotations.handlers.BeanCollector;
import com.doctordaddysir.core.exceptions.DepndencyInjectionException;
import com.doctordaddysir.core.exceptions.InvalidBeanException;
import com.doctordaddysir.core.exceptions.InvalidFieldExcepton;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InvalidBeanException,
            InvocationTargetException, IllegalAccessException, InstantiationException,
            NoSuchMethodException, IOException, ClassNotFoundException,
            NoSuchFieldException, InvalidFieldExcepton, DepndencyInjectionException {

        BeanCollector beanCollector = new BeanCollector();
        beanCollector.collectBeansAndStartPlexion(true);
    }
}