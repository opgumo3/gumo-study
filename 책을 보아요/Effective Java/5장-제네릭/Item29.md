# 이왕이면 제네릭 타입으로 만들라.
## 제네릭 클래스 만드는 방법
1. 클래스 선언에 타입 매개 변수를 추가한다.
    * 타입 이름은 주로 E를 사용한다.
2. 코드에서 사용된 Object를 적절한 타입 매개변수로 바꾼다.

* 실체화 불가 타입으로는 배열을 만들 수 없다.
```java
public class Stack<E> {
    private E[] elements;
    private int size = 0;
    private static final int CAPACITY = 16;

    public Stack() {
        elements = new E[CAPACITY]; // 불가
    }

    public void push(E e) {

    }

    public E pop() {
        if (size == 0) throw new Exception();

        E result = elements[--size];
        // ...
    }

}
```
이 때 사용할 수 있는 방법.
1. Object 배열을 생성한 다음 제네릭 배열로 형변환한다.
```java
(E[]) new Object[CAPACITY];
```
* 컴파일러는 오류 대신 경고를 낼 것이고, 일반적으로 타입 안전하지 않음.
* 만약 타입 안정성을 확신할 수 있으면 @SupressWarning 으로 경고를 숨긴다.
* 형변환을 배열 생성 시 한 번만 해도됨.
* 🚨 배열의 런타임 타입이 컴파일타임 타입과 달라 힙 오염을 일으킨다.

2. elements 필드의 타입을 Ojbect[]로 바꾸고, elements 요소에 접근할 때 형변환
```java
E rsult  (E) elements[--size];
``` 
* E는 실체화 불가 타입으로 컴파일러는 런타임에 이뤄지는 형변환이 안전한지 증명할 수 없음.

<hr>

* 자바가 리스트를 기본 타입으로 제공하지 않으므로, ArrayList 같은 제네릭 타입도 결국은 배열을 사용해 구현해야 함.
* HashMap 같은 제네릭 타입은 성능을 높일 목적으로 배열을 사용하기도 함.
* `Stack<int>`, `Stack<double>`은 사용할 수 없으나 이것은 제네릭 타입 시스템의 근본적인 문제
* `Stack<E extends Something>`으로 하위 타입만 받게 할 수 있음. </br>이러한 타입 매개변수 E를 한정적 타입 매개변수라고 함.