package org.example.annotation;

import java.lang.annotation.Repeatable;

// TODO 2-1 어노테이션 하나 만들어봤어요.
@Repeatable(ToDos.class)
public @interface ToDo {
    String value();
}
