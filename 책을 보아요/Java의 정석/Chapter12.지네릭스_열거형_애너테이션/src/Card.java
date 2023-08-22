package org.example;

public class Card {
    public Card(Kind kind, Value value) {
        this.kind = kind;
        this.value = value;
    }

    enum Kind {CLOVER, HEART, DIAMOND};
    enum Value {ONE, TWO, THREE};

    final Kind kind;
    final Value value;
}
