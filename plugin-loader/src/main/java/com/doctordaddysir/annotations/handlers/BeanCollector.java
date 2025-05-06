package com.doctordaddysir.annotations.handlers;

import com.doctordaddysir.SystemInfo;
import com.doctordaddysir.annotations.PlexionBootLoader;
import com.doctordaddysir.exceptions.InvalidBeanException;
import com.doctordaddysir.exceptions.InvalidFieldExcepton;
import com.doctordaddysir.utils.AnnotationScanner;
import com.doctordaddysir.utils.ReflectionUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Getter
public class BeanCollector {
    private final Map<String, Class<?>> beans = new HashMap<>();
    private final Map<String, Object> instantiatedBeans = new HashMap<>();


    public void collectBeans() {
        log.info("Collecting beans");
        addBean(this.getClass());
        addBeanInstance(BeanCollector.class.getName(), this);
        try {
            AnnotationScanner.findClassesWithAnnotation(SystemInfo.BEAN_ANNOTATION,
                    SystemInfo.BASE_PACKAGE).forEach(this::addBean);
            log.info("Found {} beans", beans.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            log.error("Class not found", e);
        }
    }

    private void addBeanInstance(String name, Object instance) {
        instantiatedBeans.put(name, instance);
    }

    public void removeBeanInstance(Object instance) {
        instantiatedBeans.remove(instance.getClass().getCanonicalName());
    }

    public boolean containsBeanInstance(String name) {
        return instantiatedBeans.containsKey(name);
    }

    public boolean containsBeanInstance(Class<?> clazz) {
        return instantiatedBeans.containsKey(clazz.getCanonicalName());
    }

    public int sizeBeanInstances() {
        return instantiatedBeans.size();
    }


    public void clearBeans() {
        beans.clear();
    }

    public void addBean(Class<?> clazz) {
        beans.put(clazz.getName(), clazz);
    }

    public void removeBean(Class<?> clazz) {
        beans.remove(clazz.getName());
    }

    public boolean containsBean(Class<?> clazz) {
        return beans.containsKey(clazz.getName());
    }

    public int size() {
        return beans.size();
    }

    public boolean isEmpty() {
        return beans.isEmpty();
    }

    public Object getOrCreateBeanInstance(Class<?> clazz) throws
            InvalidBeanException, InvocationTargetException, IllegalAccessException,
            InstantiationException, NoSuchMethodException, RuntimeException {
        Object instance = getBeanInstance(clazz.getName());
        boolean doesNotExist = !containsBeanInstance(clazz.getName());
        if (doesNotExist && InjectionUtils.hasInjectableFields(clazz)) {
            try {
                instance = clazz.getDeclaredConstructor().newInstance();
                addBeanInstance(clazz.getName(), instance);
                InjectionUtils.injectFields(instance, this);
            } catch (IOException | NoSuchFieldException | ClassNotFoundException |
                     InvalidFieldExcepton e) {
                throw new InvalidBeanException(e.getMessage());
            }
        } else if(doesNotExist) {
            instance = ReflectionUtils.newInstance(clazz, null);
            addBeanInstance(clazz.getName(), instance);
        }

        return instance;
    }

    private Object getBeanInstance(String name) {

        return instantiatedBeans.get(name);
    }

    public void collectBeansAndStartBootLoader() throws InvalidBeanException, InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        collectBeans();
        PlexionBootLoader plexion = (PlexionBootLoader) getOrCreateBeanInstance(PlexionBootLoader.class);
        plexion.boot(false);
    }
}
