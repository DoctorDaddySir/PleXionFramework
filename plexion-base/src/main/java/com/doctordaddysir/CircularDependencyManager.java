package com.doctordaddysir;

import com.doctordaddysir.core.reflection.ResolvingBean;
import lombok.NoArgsConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.doctordaddysir.core.utils.reflection.InjectionUtils.getPrimitiveParameter;
import static java.util.Objects.nonNull;

@NoArgsConstructor
public class CircularDependencyManager {
    public static Object createInstance(Class<?> clazz, Class<?>[] types,
                                        Object[] args) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        for (int i = 0; i < types.length; i++) {
            if (nonNull(args[i])) {
                types[i] = args[i].getClass();
            }
            if (types[i].isPrimitive()) {
                args[i] = getPrimitiveParameter(types[i]);
            }
        }
        return clazz.getDeclaredConstructor(types).newInstance(args);
    }

    public static Object updateResolvingBeanInstance(ResolvingBean resolvingBean) throws
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = resolvingBean.getConstructor();
        return constructor.newInstance(resolvingBean.getArgs());
    }
}
