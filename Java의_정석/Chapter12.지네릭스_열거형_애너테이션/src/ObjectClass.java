package org.example;

public class ObjectClass {
    Object item;

    public Object setItem(Object item) {
        this.item = item;
        return this.item;
    }

    @Override
    public String toString() {
        return "ObjectClass{" +
                "item=" + item +
                '}';
    }
}
