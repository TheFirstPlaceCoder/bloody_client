package com.client.utils.misc;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class ConcurrentList<E> extends ArrayList<E> {

//	V3

    private boolean setMode = false;

    public ConcurrentList() {
        super();
    }

    public ConcurrentList(int initialCapacity) {
        super(initialCapacity);
    }

    public ConcurrentList(Collection<? extends E> collection) {
        super(collection);
    }

    public static List createSet(Collection collection) {
        ConcurrentList<?> list = new ConcurrentList<>();
        list.setMode = true;
        list.addAll(collection);
        return list;
    }

    public static List createSet() {
        ConcurrentList<?> list = new ConcurrentList<>();
        list.setMode = true;
        return list;
    }

    public List reverse() {
        Collections.reverse(this);
        return this;
    }

    @Override
    public boolean add(E e) {
        if (setMode && contains(e)) return false;
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        if (setMode && contains(element)) return;
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (setMode) {
            boolean hasOne = false;
            Object[] array = c.toArray();
            for (int i = 0; i < array.length; i++) {
                if (!contains(array[i])) {
                    hasOne = true;
                    super.add((E) array[i]);
                }
            }
            return hasOne;
        }
        return super.addAll(c);
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        Object[] current = ConcurrentList.this.toArray();
        for (int i = 0; i < current.length; i++) action.accept((E) current[i]);
        super.forEach(action);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            Object[] current = ConcurrentList.this.toArray();
            E cache;
            int index = 0;

            @Override
            public void remove() {
                ConcurrentList.this.remove(cache);
            }

            @Override
            public boolean hasNext() {
                return index < current.length;
            }

            @Override
            public E next() {
                if (hasNext()) {
                    cache = (E) current[index];
                    index++;
                    return cache;
                }
                return null;
            }
        };
    }
}