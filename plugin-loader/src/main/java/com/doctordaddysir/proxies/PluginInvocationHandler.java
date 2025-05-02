package com.doctordaddysir.proxies;

import com.doctordaddysir.base.Plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class PluginInvocationHandler implements InvocationHandler {
    private final Plugin target;
    public PluginInvocationHandler(Plugin target) {
        this.target = target;
    }
    public Plugin getTarget() {
        return target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.currentTimeMillis();
        System.out.println("Executing method: " + method.getName() + " in  plugin " + target.getClass().getName());
        Object result = method.invoke(target, args);
        long duration = System.currentTimeMillis() - start;
        System.out.println("Executed method: " + method.getName() + " in plugin: " + target.getClass().getName() + " in " + duration + "ms");
        return result;
    }
}
