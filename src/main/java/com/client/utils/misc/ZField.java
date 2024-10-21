package com.client.utils.misc;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ZField {
//    VERSION 3

    public Field field;
    private Object target;
    static Unsafe unsafe = field(Unsafe.class, "theUnsafe").get();

    public ZField(Field field) {
        this.field = field;
        field.setAccessible(true);
    }

    private static Class createTarget(Object target) {
        return target instanceof Class v ? v : target.getClass();
    }

    private static ZField createIndex(Field[] fields, Object target, int index) {
        try {
            ZField zField = new ZField(fields[index]);
            if (!(target instanceof Class)) zField.target = target;
            return zField;
        } catch (IndexOutOfBoundsException ex) {
            throw new RuntimeException();
        }
    }

    public static ZField field(Field field) {
        return new ZField(field);
    }

    public static ZField field(Object target, int index) {
        Field[] fields = createTarget(target).getDeclaredFields();
        return createIndex(fields, target, index);
    }

    public static ZField fieldVirtual(Object target, int index) {
        Field[] fields = Arrays.stream(createTarget(target).getDeclaredFields()).map(z -> !Modifier.isStatic(z.getModifiers())).toArray(Field[]::new);
        return createIndex(fields, target, index);
    }

    public static ZField fieldStatic(Object target, int index) {
        Field[] fields = Arrays.stream(createTarget(target).getDeclaredFields()).map(z -> Modifier.isStatic(z.getModifiers())).toArray(Field[]::new);
        return createIndex(fields, target, index);
    }

    public static ZField field(Class clazz, Object target, String name) {
        try {
            ZField zField = new ZField(clazz.getDeclaredField(name));
            if (!(target instanceof Class)) zField.target = target;
            return zField;
        } catch (NoSuchFieldException ex) {
            throw new RuntimeException();
        }
    }


    public static ZField field(Object target, String name) {
        return field(createTarget(target), target, name);
    }

    public ZField target(Object target) {
        this.target = target;
        return this;
    }

    public <A> A get() {
        return get(target);
    }

    public <A> A get(Object object) {
        try {
            return (A) field.get(object);
        } catch (IllegalAccessException e) {}
        return null;
    }

    public boolean getBoolean() {
        return get(target);
    }

    public boolean getBoolean(Object object) {
        return get(object);
    }

    public int getInt() {
        return get(target);
    }

    public int getInt(Object object) {
        return get(object);
    }

    public void set(Object value) {
        set(target, value);
    }

    public void set(Object target, Object value) {
        try {
            if (isStatic() && isFinal()) {
                get(); //если переменная была не инициализирована
                unsafe.putObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
            } else field.set(target, value);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }


    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    public boolean isVirtual() {
        return !isStatic();
    }

    public boolean isRecord() {
        return target != null && target.getClass().isRecord();
    }



}