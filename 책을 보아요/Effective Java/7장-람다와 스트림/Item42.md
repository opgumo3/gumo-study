# Item 42. 익명 클래스보다는 람다를 사용하라.
## 익명 클래스
* 클래스를 정의하지 않고, 메소드 내에서 클래스를 정의하고 인스턴스화 할 수 있음.
* 재사용하지 않는 일회성 클래스를 정의하고 생성할 때 사용한다.

```java
// animal.java
public class Animal {
    public void walk() {
        System.out.println("동물이 걷습니다.");
    }
}

// main
Animal dog = new Animal() {
    @Override
    public void walk() {    // 오버라이드한 메소드는 외부에서 사용할 수 있다.
        System.out.println("개가 걷습니다.");
    }

    public void bark() {    // 외부에서 사용할 수 없다.
        System.out.println("개가 짖습니다.");
    }
};
```

<hr>


```java
// 익명 클래스
Collections.sort(list, new Comparator<String>() {
    @Override
    public int compare(String o1, String o2) {
    }
});

// 람다 : 간결함.
Collections.sort(list, (s1, s2) -> Integer.compare(s1.length(), s2.length()));
```
* JDK 1.1에서는 함수 객체를 만드는 주요 수단이 익명 클래스였다.
* Collections.sort는 전략 패턴을 사용한 것으로, Comparator 인터페이스는 정렬을 담당하는 추상 전략이고, 익명 클래스로 구체적인 전략을 구현함.
* 자바 8에서는 함수형 인터페이스라 부르는 인터페이스의 인스턴스를 람다식으로 생성할 수 있음.

> 자바 기본 라이브러리에서 사용된 디자인 패턴. </br>
https://stackoverflow.com/questions/1673841/examples-of-gof-design-patterns-in-javas-core-libraries/2707195#2707195

## 람다
```java
Collections.sort(list, (s1, s2) -> Integer.compare(s1.length(), s2.length()));
```
* 코드에서 언급하지는 않지만, 컴파일러가 람다, 매개변수, 반환값의 타입을 추론한다.

|항목|추론 타입|
|------|------|
|람다| `Comparator<String>`|
|매개변수(s1, s2)|String|
|반환값|int|
* 컴파일러가 추론하지 못하는 경우 명시해야 한다.

### - 간결함 높이기.
1. 비교자 생성 메서드
```java
Collections.sort(list, comparingInt(String::length));
```

2. List 인터페이스의 sort 메서드
```java
list.sort(comparingInt(String::length))
```

### - 열거타입에서 상수별 동작의 예시
```java
public enum Operation {
    PLUS ("+", (x, y) -> x + y),
    MINUS ("+", (x, y) -> x - y);

    private final String symbol;
    private final DoubleBinaryOperator op;

    Operation(String symbol, DoubleBinaryOperator op) {
        this.symbol = symbol;
        this.op = op;
    }

    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }
}

// main
Operation.PLUS.apply(1,2);
```

### - 하지만...
* 람다는 이름이 없고 문서화 할 수 없다. </br> 코드 자체로 동작이 명확히 설명되지 않는 경우에는 지양.
* 열거 타입 생성자의 인수의 타입은 컴파일타임에 추론되어, 열거 타입 생성자 안의 람다는 열거 타입의 인스턴스 멤버에 접근할 수 없음. (인스턴스는 런타임에 만들어지 떄문에.) 💥
> invokedynamic 명령어로 런타임에 동적으로 클래스를 정의하고 인스턴스를 생성함.</br> 
https://dreamchaser3.tistory.com/5 </br>
https://blogs.oracle.com/javamagazine/post/understanding-java-method-invocation-with-invokedynamic
* 람다는 함수형 인터페이스에서만 사용된다. </br> 람다는 추상 클래스의 인스턴스를 만들 때 람다를 쓸 수 없고, 추상 메서드가 여러 개인 인터페이스의 인스턴스를 만들 때 사용할 수 없다.</br>이 경우에는 익명 클래스를 사용해야 한다.
* 람다 안에서의 this 키워드는 바깥 인스턴스를 가리킨다. 인스턴스 자신을 가리키기 위해서는 익명 클래스를 사용해야 한다.
* 람다를 직렬화하는 일은 극히 삼가야 한다.