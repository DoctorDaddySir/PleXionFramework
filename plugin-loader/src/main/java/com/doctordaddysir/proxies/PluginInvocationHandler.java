package com.doctordaddysir.proxies;

import com.doctordaddysir.plugins.base.Plugin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Getter
@Slf4j
public class PluginInvocationHandler implements InvocationHandler {
    private final Plugin target;
    public PluginInvocationHandler(Plugin target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.currentTimeMillis();
        log.debug("Executing method: {} in  plugin {}", method.getName(), target.getClass().getSimpleName());
        Object result = method.invoke(target, args);
        long duration = System.currentTimeMillis() - start;
        log.debug("Executed method: {} in plugin: {}",target.getClass().getName(), duration + "ms");
        return result;
    }
}
