package com.doctordaddysir.core.annotations.handlers;

import com.doctordaddysir.core.SystemInfo;
import com.doctordaddysir.core.PlexionBootLoader;
import com.doctordaddysir.core.exceptions.DepndencyInjectionException;
import com.doctordaddysir.core.exceptions.InvalidBeanException;
import com.doctordaddysir.core.exceptions.InvalidFieldExcepton;
import com.doctordaddysir.core.utils.reflection.AnnotationScanner;
import com.doctordaddysir.core.utils.reflection.InjectionUtils;
import com.doctordaddysir.core.utils.reflection.ReflectionUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static com.doctordaddysir.core.utils.reflection.InjectionUtils.getConstructorForInjection;
import static com.doctordaddysir.core.utils.reflection.InjectionUtils.getPrimitiveParameter;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Getter
public class BeanCollector {
    private final Map<String, Class<?>> beans = new HashMap<>();
    private final Map<Class<?>, ResolvingBean> resolvingBeans = new HashMap<>();
    private final Map<String, Object> instantiatedBeans = new HashMap<>();
    private PlexionBootLoader bootLoader;

    private static Class<?> updateClassIfAbstractOrInterface(Class<?> clazz) throws IOException, ClassNotFoundException {
        if (ReflectionUtils.isAbstract(clazz)) {
            clazz = ReflectionUtils.findInjectableClassforAbstractOrInterfaceClass(clazz);
        }
        return clazz;
    }

    public void collectBeansAndStartPlexion(boolean isDebug) throws InvalidBeanException,
            InvocationTargetException, IllegalAccessException, InstantiationException,
            NoSuchMethodException, IOException, ClassNotFoundException,
            NoSuchFieldException, InvalidFieldExcepton, DepndencyInjectionException {
        collectBeansAndResolveBootloader();
        bootLoader.boot(isDebug);
    }

    private void collectBeansAndResolveBootloader() throws InvalidBeanException,
            IOException, NoSuchFieldException, ClassNotFoundException,
            InvocationTargetException, InvalidFieldExcepton, InstantiationException,
            IllegalAccessException, NoSuchMethodException, DepndencyInjectionException {
        collectBeans();
        bootLoader = (PlexionBootLoader) resolve(PlexionBootLoader.class);
    }

    public void collectBeans() {
        log.info("Collecting beans");
        addBean(this.getClass());
        addBeanInstance(BeanCollector.class.getName(), this);
        try {
            AnnotationScanner.findClassesWithAnnotationFromCollection(SystemInfo.BEAN_ANNOTATIONS,
                    SystemInfo.BASE_PACKAGE).forEach(this::addBean);
            log.info("Found {} beans", beans.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            log.error("Class not found", e);
        }
    }

    public Object resolve(Class<?> clazz) throws InvocationTargetException,
            InstantiationException, IllegalAccessException, InvalidBeanException,
            IOException, ClassNotFoundException, NoSuchMethodException,
            NoSuchFieldException, InvalidFieldExcepton, DepndencyInjectionException {

        clazz = updateClassIfAbstractOrInterface(clazz);

        ResolvingBean checkCurrentResolution = checkForBreakingConditionInResolve(clazz);

        if (nonNull(checkCurrentResolution)) {
            return checkCurrentResolution.getResolvingInstance();
        }
        ResolvingBean resolvingBean;
        Object[] args;

        if (isBeanResolving(clazz)) {
            return getResolvingBean(clazz).getResolvingInstance();
        } else {
            resolvingBean = createNewResolvingBean(clazz);
            args = resolvingBean.getArgs();

        }


        for (int i = 0; i < resolvingBean.getConstructor().getParameterTypes().length; i++) {
            Class<?> paramType = resolvingBean.getConstructor().getParameterTypes()[i];
            resolveParamAndUpdateResolvingBean(paramType, resolvingBean, i, args);
        }

        Object instance = generateInstanceAndInjectFields(resolvingBean, args);

        stopResolutionAndAddBeanInstanceToInstantiatedBeans(resolvingBean, instance);

        return instance;
    }

    private void stopResolutionAndAddBeanInstanceToInstantiatedBeans(ResolvingBean resolvingBean, Object instance) {
        stopBeanResolving(resolvingBean.getType());
        addBeanInstance(resolvingBean.getType().getCanonicalName(), instance);
    }

    private Object generateInstanceAndInjectFields(ResolvingBean resolvingBean,
                                                   Object[] args) throws InvalidBeanException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException, InvalidFieldExcepton, DepndencyInjectionException {
        resolvingBean.updateArgsAndGenerateNewResolvingInstance(args);
        Object instance = resolvingBean.invoke();
        instance = InjectionUtils.injectFields(instance, this);
        return instance;
    }

    private void resolveParamAndUpdateResolvingBean(Class<?> paramType,
                                                    ResolvingBean resolvingBean, int i,
                                                    Object[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException, InvalidBeanException, IOException, ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, InvalidFieldExcepton, DepndencyInjectionException {
        Object param = resolve(paramType);
        resolvingBean.getArgs()[i] = param;
        resolvingBean.setArgs(args);
    }

    private ResolvingBean createNewResolvingBean(Class<?> clazz) throws InvalidBeanException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = getConstructorForInjection(clazz);
        Object[] args = new Object[constructor.getParameterTypes().length];
        Object placeholder = ResolvingBeanInstanceGenerator.createInstance(clazz,
                constructor.getParameterTypes(), args);
        clazz = constructor.getDeclaringClass();

        ResolvingBean resolvingBean = ResolvingBean.
                builder()
                .type(clazz)
                .resolvingInstance(placeholder)
                .isResolving(true)
                .args(args)
                .constructor(constructor)
                .beanCollector(this)
                .build();
        resolvingBean.startResolving();
        return resolvingBean;
    }

    private ResolvingBean updateResolvingBean(ResolvingBean bean, Object[] args) throws InvalidBeanException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        bean.setArgs(args);
        bean.setResolvingInstance(ResolvingBeanInstanceGenerator.updateResolvingBeanInstance(bean));
        return bean;
    }

    private ResolvingBean checkForBreakingConditionInResolve(Class<?> clazz) throws IOException, ClassNotFoundException {
        if (clazz.isPrimitive()) {
            Object primitiveParameter = getPrimitiveParameter(clazz);
            return ResolvingBean.builder()
                    .args(new Object[0])
                    .isPrimitive(true)
                    .resolvingInstance(primitiveParameter)
                    .isResolved(true)
                    .build();
        }
        if (containsBeanInstance(clazz.getCanonicalName())) {
            return ResolvingBean.builder()
                    .resolvingInstance(getBeanInstance(clazz.getCanonicalName()))
                    .isResolved(true)
                    .build();
        }
        return null;
    }

    public boolean containsBeanInstance(String name) {
        return instantiatedBeans.containsKey(name);
    }

    public Boolean isBeanResolving(Class<?> clazz) {
        return resolvingBeans.containsKey(clazz);
    }

    public void setBeanResolving(Class<?> clazz, ResolvingBean resolvingBean,
                                 Boolean isResolving) {
        if (isResolving) {
            resolvingBeans.put(clazz, resolvingBean);
        } else {
            resolvingBeans.remove(clazz);
        }
    }

    public void stopBeanResolving(Class<?> clazz) {
        setBeanResolving(clazz, null, false);
    }


    public void addBeanInstance(String name, Object instance) {
        instantiatedBeans.put(name, instance);

    }

    public void addBeanInstance(Class<?> clazz, Object instance) {
        instantiatedBeans.put(clazz.getName(), instance);
    }

    public void removeBeanInstance(Object instance) {
        instantiatedBeans.remove(instance.getClass().getCanonicalName());
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
            InstantiationException, NoSuchMethodException, RuntimeException,
            DepndencyInjectionException, IOException {
        Object instance = getBeanInstance(clazz.getName());
        boolean doesNotExist = !containsBeanInstance(clazz.getName());
        if (doesNotExist && InjectionUtils.hasInjectableFields(clazz)) {
            try {
                instance = clazz.getDeclaredConstructor().newInstance();
                addBeanInstance(clazz.getName(), instance);
                try {
                    InjectionUtils.injectFields(instance, this);
                } catch (InvalidFieldExcepton e) {
                    throw new RuntimeException(e);
                }
            } catch (InstantiationException e) {
                throw new DepndencyInjectionException(ReflectionUtils.ReflectionDIError.INSTANTIATION_EXCEPTION, e.getMessage());
            }
        } else if (doesNotExist) {
            instance = ReflectionUtils.newInstance(clazz, null);
            addBeanInstance(clazz.getName(), instance);
        }

        return instance;
    }

    public Object getBeanInstance(String name) {

        return instantiatedBeans.get(name);
    }

    public ResolvingBean getResolvingBean(Class<?> clazz) {
        return resolvingBeans.get(clazz);
    }

    private void startBeanResolving(Class<?> type, ResolvingBean resolvingBean) {
        setBeanResolving(type, resolvingBean, true);
    }

    @Builder
    @Data
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class ResolvingBean {
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

}
