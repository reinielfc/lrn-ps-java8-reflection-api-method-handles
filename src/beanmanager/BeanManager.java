package beanmanager;

import annotation.Inject;
import annotation.Provides;
import provider.H2ConnectionProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BeanManager {

    private static final BeanManager instance = new BeanManager();
    private final Map<Class<?>, Supplier<?>> registry = new HashMap<>();

    private BeanManager() {
        // we are hardcoding the list of classes here for demonstration purposes
        // on a real app you would find this list programmatically,
        // or use a library outside the jdk w/ this functionality
        List<Class<?>> classes = Collections.singletonList(H2ConnectionProvider.class);
        for (Class<?> aClass : classes)
        {
            for (Method method : aClass.getDeclaredMethods())
            {
                if (method.getAnnotation(Provides.class) != null)
                {
                    Class<?> returnType = method.getReturnType();
                    Supplier<?> supplier = () -> {
                        try {
                            if (!Modifier.isStatic(method.getModifiers())) {
                                Object object = aClass.getConstructor().newInstance();
                                return method.invoke(object);
                            } else {
                                return method.invoke(null);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    };
                    registry.put(returnType, supplier);
                }
            }
        }
    }

    public static BeanManager getInstance() {
        return instance;
    }

    public <T> T getInstance(Class<T> tClass) {
        try {
            T t = tClass.getConstructor().newInstance();
            Field[] fields = tClass.getDeclaredFields();
            for (Field field: fields) {
                Inject inject = field.getAnnotation(Inject.class);
                if (inject != null) {
                    Class<?> injectedFieldType = field.getType();
                    Supplier<?> supplier = registry.get(injectedFieldType);
                    Object objectToInject = supplier.get();
                    field.setAccessible(true);
                    field.set(t, objectToInject);
                }
            }

            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
