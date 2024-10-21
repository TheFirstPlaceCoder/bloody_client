package com.client.utils.misc;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ZMethod {
//    VERSION 4

    public Method method;
    public Object target;

    public ZMethod(Method method) {
        this.method = method;
        method.setAccessible(true);
    }


    public static ZMethod method(Method method) {
        return new ZMethod(method);
    }

    public static ZMethod method(Method method, Object target) {
        ZMethod v = method(method);
        v.target = target;
        return v;
    }

    public static ZMethod method(Object target, int index) {
        Class<?> clazz;
        if (target instanceof Class<?> a) {
            clazz = a;
            target = null;
        } else {
            clazz = target.getClass();
        }
        Method[] methods = clazz.getDeclaredMethods();
        try {
            return method(methods[index], target);
        } catch (IndexOutOfBoundsException ex) {
            String message = "[" + ZMethod.class.getName() + "] IndexOutOfBoundsException: class = " + clazz.getName() + ", index = " + index + ", max = " + methods.length;
            throw new RuntimeException(message, ex);
        }
    }


    public static ZMethod methodRet(Object target, String name, Class<?> ret, Class<?>... parameterTypes) {
        Class<?> clazz;
        if (target instanceof Class<?> a) {
            clazz = a;
            target = null;
        } else {
            clazz = target.getClass();
        }

        try {
            if (parameterTypes.length > 0) return method(clazz.getDeclaredMethod(name, parameterTypes), target);
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getReturnType() == ret && method.getName().equals(name)) return method(method, target);
            }

            throw new NoSuchMethodException();
        } catch (NoSuchMethodException ex) {
            String message = "[" + ZMethod.class.getName() + "] NoSuchMethodException: class = " + clazz.getName() + ", method = " + name + ", parameters = [" + Arrays.stream(parameterTypes).map(Class::getName).collect(Collectors.joining(", ")) + "]";
            throw new RuntimeException(message, ex);
        }
    }

    public static ZMethod method(Object target, String name, Class<?>... parameterTypes) {
        Class<?> clazz;
        if (target instanceof Class<?> a) {
            clazz = a;
            target = null;
        } else {
            clazz = target.getClass();
        }

        try {
            if (parameterTypes.length > 0) return method(clazz.getDeclaredMethod(name, parameterTypes), target);
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(name)) return method(method, target);
            }

            throw new NoSuchMethodException();
        } catch (NoSuchMethodException ex) {
            String message = "[" + ZMethod.class.getName() + "] NoSuchMethodException: class = " + clazz.getName() + ", method = " + name + ", parameters = [" + Arrays.stream(parameterTypes).map(Class::getName).collect(Collectors.joining(", ")) + "]";
            throw new RuntimeException(message, ex);
        }
    }

    public ZMethod target(Object target) {
        this.target = target;
        return this;
    }

    public <A> A invokeTarget(Object target, Object... args) {
        this.target = target;
        return invoke(args);
    }

    public <A> A invoke(Object... args) {
        try {
            return (A) method.invoke(target, args);
        } catch (Exception ex) {
            String message = "[" + ZMethod.class.getName() + "] " + ex.getClass().getName() +": class = " + (target != null ? target.getClass().getName() : "NULL") + ", method = " + method.getName() + ", return = " + method.getReturnType().getName() + ", parameters = [" + Arrays.stream(method.getParameterTypes()).map(Class::getName).collect(Collectors.joining(", ")) + "]";
            throw new RuntimeException(message, ex);
        }
    }
}