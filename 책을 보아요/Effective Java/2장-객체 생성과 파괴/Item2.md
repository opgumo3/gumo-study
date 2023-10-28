# 생성자에 매개변수가 많다면 빌더를 고려하라.
클래스에 선택적인 매개변수가 많을 때는 어떻게 해야할까?

## 1. 점층적 생성자 패턴
필수 매개변수만 받는 생성자 부터
</br>필수 매개변수와 선택 매개변수 1개 받는 생성자,
</br>필수 매개변수와 선택 매개변수 2개 받는 생성자,
</br>...
</br>모든 매개변수를 받는 생성자 까지 만드는 방식이다.

* 매개변수가 많아지면 코드 작성과 읽기가 어렵다.
    * 코드를 읽을 때, 매개변수만 보고 값의 의미를 알기 어렵다.
    * 매개변수의 순서가 바뀐다면? 엉뚱한 동작을 한다.

```java
public class Person {
    private String name; // 필수
    private int age; // 필수
    private int tongueLength; // 선택
    private int footSize; // 선택
    private int noseHeight; // 선택

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    // 생략
    public Person(String name, int age, int tongueLength, int footSize, int noseHeight) {
        this.name = name;
        this.age = age;
        this.tongueLength = tongueLength;
        this.footSize = footSize;
        this.noseHeight = noseHeight;
    }
}
```
대충 사람 정보 담는 클래스가 있고, 선택 사항 필드까지 넣어서 만들어야 한다면 아래처럼 생성해야 할 것이다. 
</br>읽기 쉬운가? 필드가 더 늘어난다면? 혹은 int 매개변수간에 순서가 바뀐다면?
```java
Person person = new Person("Gumo", 24, 6, 245, 5);
```

## 2. 자바 빈즈 패턴
매개변수가 없는 생성자로 객체를 만든 후, Setter를 호출해 원하는 매개변수의 값을 설정하는 방식.

```java
Person person = new Person();
person.setName("Gumo");
person.setAge(24);
person.setTongueLength(6);
person.setFootSize(245);
person.setNoseHeight(5);
```
* 객체 하나를 만들기 위해, 메서드를 여러 개 호출해야 한다.
* 객체가 완전히 생성되기 전까지는 일관성이 무너진 상태에 놓이게 된다.
    * 클래스를 불변으로 만들 수 없고, 스레드 안전성을 얻으려면 추가 작업을 해야한다.
    * 클래스 생성이 끝났다면 freeze 메서드를 부르고, 그 전까지는 사용할 수 없도록 한다.
    </br> 하지만 객체 사용 전에 프로그래머가 freeze 메서드를 확실히 호출했는지 컴파일러가 보증할 방법이 없어, 런타임 오류에 취약하다.


## 3. 빌더 패턴
점층적 생성자보다 코드를 읽고 쓰기가 한결하고, 자바빈즈 패턴보다 안전한 빌더 패턴을 봐보자.

```java
public class Person {
    private final String name;
    private final int age;
    private final int tongueLength;
    private final int footSize;
    private final int noseHeight;

    public static class Builder {
        private final String name;
        private final int age;
        // 선택 사항은 기본값으로 초기화.
        private int tongueLength = 0;
        private int footSize = 0;
        private int noseHeight = 0;

        public Builder(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public Builder tongueLength(int tongueLength) {
            this.tongueLength = tongueLength;
            return this;
        }

        public Builder footSize(int footSize) {
            this.footSize = footSize;
            return this;
        }

        public Builder noseHeight(int noseHeight) {
            this.noseHeight = noseHeight;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }

    private Person(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.tongueLength = builder.tongueLength;
        this.footSize = builder.footSize;
        this.noseHeight = builder.noseHeight;
    }
}
```
빌더 패턴은 위처럼 작성하면되고, 사용은 아래처럼 하면 된다.
```java
Person gumo = new Person.Builder("gumo", 24)
                .noseHeight(5)
                .build();
```

### 특징
* 클래스는 불변이다.
* 빌더를 연쇄적으로 호출할 수 있다.
    * 플루언트 API (fluent API) 혹은 메서드 연쇄 (method chaining)이라고 한다.
* 코드 쓰기 쉽고, 읽기도 쉽다.
* 파이썬과 스칼라에 있는 명명된 선택적 매개변수(named optional parameters)를 흉내낸 것.
* 유효성 검사를 위해서, 빌더의 메서드에서 입력 매개변수를 검사하고, build 메서드가 호출하는 생성자에서 여러 매개변수에 걸친 불변식을 검사.
    * 빌더로부터 매개변수를 복사한 후, 해당 객체 필드들을 검사한다.

> ✔ 불변 (Immutable, Immutability) <-> mutable
</br> 어떠한 변경도 허용하지 않는다는 뜻. 
</br> ✔ 불변식 (Invariant)
</br> 프로그램이 실행되는 동안 혹은 정해진 기간 동안 반드시 만족해야하는 조건.
</br> ex) 리스트의 크기는 반드시 0 이상이어야 한다. 음수 값이 된다면 불변식이 깨지는 것.

* 빌더 패턴은 계층적으로 설계된 클래스와 함께 쓰기 좋다.
* 객체를 만들기 위해, 빌더부터 만들어야 한다.

> ✔ API는 시간이 지날수록 매개변수가 많아지는 경향이 있다는 것을 명심하자.