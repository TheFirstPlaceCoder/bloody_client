package com.client.utils.misc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

//  v2
public class ZConstruct {
    public Constructor<?> constructor;

    public ZConstruct(Constructor<?> constructor) {
        this.constructor = constructor;
        constructor.setAccessible(true);
    }

    public static ZConstruct construct(Constructor<?> constructor) {
        return new ZConstruct(constructor);
    }

    public static ZConstruct construct(String clazz, int index) {
        try {
            return construct(Class.forName(clazz).getDeclaredConstructors()[index]);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static ZConstruct construct(Object target, int index) {
        Class<?> clazz = target instanceof Class v ? v : target.getClass();

        Constructor<?>[] all = clazz.getDeclaredConstructors();
        try {
            return new ZConstruct(all[index]);
        } catch (IndexOutOfBoundsException ex) {
            throw new RuntimeException();
        }
    }

    public <A> A create(Object... object) {
        try {
            return (A) constructor.newInstance(object);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}