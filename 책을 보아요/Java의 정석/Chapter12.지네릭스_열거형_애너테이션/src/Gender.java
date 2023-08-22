package org.example;

public enum Gender {
    MEN(1, "남자"),
    WOMEN(2, "여자");

    final int value;
    final String name;

    Gender(int value, String name) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Gender{" +
                "value=" + value +
                ", name='" + name + '\'' +
                '}';
    }

}
