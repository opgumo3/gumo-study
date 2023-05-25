package org.example;

public enum Direction {
    EAST(1),
    WEST(2);

    private final int value;

    Direction(int value) {
        this.value = value;
    }
}
