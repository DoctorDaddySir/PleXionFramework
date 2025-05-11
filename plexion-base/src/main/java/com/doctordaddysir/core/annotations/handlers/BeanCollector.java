package com.doctordaddysir.core.annotations.handlers;

import com.doctordaddysir.core.SystemInfo;
import com.doctordaddysir.core.PlexionBootLoader;
import com.doctordaddysir.core.exceptions.DepndencyInjectionException;
import com.doctordaddysir.core.exceptions.InvalidBeanException;
import com.doctordaddysir.core.exceptions.InvalidFieldExcepton;
import com.doctordaddysir.core.reflection.ResolvingBean;
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
import static com.doctordaddysir.core.utils.reflection.ReflectionUtils.updateClassIfAbstractOrInterface;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Getter
public class BeanCollector {
    private final Map<String, Class<?>> beans = new HashMap<>();
    private final Map<Class<?>, ResolvingBean> resolvingBeans = new HashMap<>();
    private final Map<String, Object> instantiatedBeans = new HashMap<>();
    private PlexionBootLoader bootLoader;



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

        ResolvingBean checkCurrentResolution = checkCurrentResolutionStatus(clazz);

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
    private ResolvingBean checkCurrentResolutionStatus(Class<?> clazz) throws IOException, ClassNotFoundException {
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
    private ResolvingBean createNewResolvingBean(Class<?> clazz) throws InvalidBeanException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = getConstructorForInjection(clazz);
        Object[] args = new Object[constructor.getParameterTypes().length];
        Object placeholder = CircularDependencyManager.createInstance(clazz,
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


    private void resolveParamAndUpdateResolvingBean(Class<?> paramType,
                                                    ResolvingBean resolvingBean, int i,
                                                    Object[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException, InvalidBeanException, IOException, ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, InvalidFieldExcepton, DepndencyInjectionException {
        Object param = resolve(paramType);
        resolvingBean.getArgs()[i] = param;
        resolvingBean.setArgs(args);
    }

    private Object generateInstanceAndInjectFields(ResolvingBean resolvingBean,
                                                   Object[] args) throws InvalidBeanException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException, InvalidFieldExcepton, DepndencyInjectionException {
        resolvingBean.updateArgsAndGenerateNewResolvingInstance(args);
        Object instance = resolvingBean.invoke();
        instance = InjectionUtils.injectFields(instance, this);
        return instance;
    }

    private void stopResolutionAndAddBeanInstanceToInstantiatedBeans(ResolvingBean resolvingBean, Object instance) {
        stopBeanResolving(resolvingBean.getType());
        addBeanInstance(resolvingBean.getType().getCanonicalName(), instance);
    }



    public void stopBeanResolving(Class<?> clazz) {
        setBeanResolving(clazz, null, false);
    }


    public void setBeanResolving(Class<?> clazz, ResolvingBean resolvingBean,
                                 Boolean isResolving) {
        if (isResolving) {
            resolvingBeans.put(clazz, resolvingBean);
        } else {
            resolvingBeans.remove(clazz);
        }
    }

    public void addBeanInstance(String name, Object instance) {
        instantiatedBeans.put(name, instance);

    }

    public ResolvingBean updateResolvingBean(ResolvingBean bean, Object[] args) throws InvalidBeanException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        bean.setArgs(args);
        bean.setResolvingInstance(CircularDependencyManager.updateResolvingBeanInstance(bean));
        return bean;
    }


    public void addBean(Class<?> clazz) {
        beans.put(clazz.getName(), clazz);
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
        boolean exists = containsBeanInstance(clazz.getName());
        if(exists) {
            return getBeanInstance(clazz.getName());
        }

        return createBeanInstance(clazz);
    }

    private Object createBeanInstance(Class<?> clazz) throws NoSuchMethodException, DepndencyInjectionException, InvocationTargetException, IllegalAccessException, InvalidBeanException, IOException, InstantiationException {
        Object instance;
        if (InjectionUtils.hasInjectableFields(clazz)) {
                instance = clazz.getDeclaredConstructor().newInstance();
                addBeanInstance(clazz.getName(), instance);
                injectRemainingFields(instance);
        } else  {
            instance =  ReflectionUtils.newInstance(clazz, null);
            addBeanInstance(clazz.getName(), instance);
        }
        return instance;
    }

    private void injectRemainingFields(Object instance) throws IOException, InvalidBeanException, DepndencyInjectionException {
        try {
            InjectionUtils.injectFields(instance, this);
        } catch (InvalidFieldExcepton e) {
            throw new RuntimeException(e);
        }
    }

    public Object getBeanInstance(String name) {

        return instantiatedBeans.get(name);
    }

    public ResolvingBean getResolvingBean(Class<?> clazz) {
        return resolvingBeans.get(clazz);
    }

    public void startBeanResolving(Class<?> type, ResolvingBean resolvingBean) {
        setBeanResolving(type, resolvingBean, true);
    }
    public void removeBean(Class<?> clazz) {
        beans.remove(clazz.getName());
    }

    public boolean containsBean(Class<?> clazz) {
        return beans.containsKey(clazz.getName());
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



}
