package com.doctordaddysir;

import com.doctordaddysir.annotations.handlers.BeanCollector;
import com.doctordaddysir.exceptions.DepndencyInjectionException;
import com.doctordaddysir.exceptions.InvalidBeanException;
import com.doctordaddysir.exceptions.InvalidFieldExcepton;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InvalidBeanException,
            InvocationTargetException, IllegalAccessException, InstantiationException,
            NoSuchMethodException, IOException, ClassNotFoundException,
            NoSuchFieldException, InvalidFieldExcepton, DepndencyInjectionException {

        BeanCollector beanCollector = new BeanCollector();
        beanCollector.collectBeansAndStartBootLoader();
    }
}