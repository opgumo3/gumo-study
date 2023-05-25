package org.example;

public class GenericClass<T> {
    T item;
    public T setItem(T item) {
        this.item = item;
        return this.item;
    }
}
